package CIMTop

import chisel3._
import chisel3.util._
import freechips.rocketchip.config._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.rocket._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.tile._
import testchipip._

class  CIMRoCC(opcodes: OpcodeSet,val n: Int = 256)(implicit p: Parameters) extends LazyRoCC(opcodes) {
  override lazy val module = new CIMRoCCModuleImp(this)
}
class CIMRoCCModuleImp(outer: CIMRoCC)(implicit p: Parameters) extends LazyRoCCModuleImp(outer)
  with HasCoreParameters {

  val cmd = Queue(io.cmd)
  val funct = cmd.bits.inst.funct
  val addr = cmd.bits.rs2(log2Up(outer.n)-1,0) //addr is 0-8191(256*256/8)
  val in_data = cmd.bits.rs1  // 64bit
  val doLoad_SRAM = funct === 0.U
  val doLoad_Input = funct === 1.U
  val doSend_Output = funct === 2.U

  val cimtop=Module(new(CIMTop))
  val regfile = Mem(outer.n, UInt(xLen.W))
  val busy = RegInit(VecInit(Seq.fill(outer.n){false.B}))

	// val sram_enable=Reg(Bool())
	// val sram_write=Reg(Bool())
	// val sram_addr=Reg(UInt(8.W)) //not within a single sram, but index all srams
	// val sram_dataIn=Reg(UInt(2048.W)) // now problem
	// val compute_enable=Reg(Bool())
	// val mat_in=Reg(Vec(256,UInt(8.W)))  //TODO:not full input
  val s_idle :: s_load_sram :: s_load_input :: s_done :: Nil = Enum(4)
  val state = RegInit(s_idle)

  val memRespTag = io.mem.resp.bits.tag(log2Up(outer.n)-1,0)

  val cnt=RegInit(0.U(log2Up(outer.n).W))

  when (cmd.fire() && (doSend_Output)) {
    regfile(addr) := cimtop.io.mat_out(cnt/8.U)(63.U,0,U)
  }

  when (io.mem.resp.valid) {
    regfile(memRespTag) := io.mem.resp.bits.data
    busy(memRespTag) := false.B
  }

  // control
  when (io.mem.req.fire()) {
    busy(addr) := true.B
  }

  when (cmd.fire() && doLoad_SRAM){
    state:=s_load_sram
    cimtop.io.sram_enable:=true.B
    cimtop.io.sram_write:=true.B
    cimtop.io.compute_enable:=false.B
    cimtop.io.sram_addr:=addr/256.U
    cimtop.io.sram_dataIn:=in_data // prob
    for (i<- 0 until 256)
    cimtop.io.mat_in(i):= regfile(addr)
    printf("doLoad_SRAM,in_data=%x\n",in_data)
  }

  .elsewhen (cmd.fire() && doLoad_Input){
    state:=s_load_input
    cimtop.io.sram_enable:=true.B
    cimtop.io.sram_write:=false.B
    cimtop.io.compute_enable:=true.B
    cimtop.io.sram_addr:= 0.U(8.W)
    cimtop.io.sram_dataIn := 0.U(2048.W)
    printf("doLoad_Input,in_data=%x\n",in_data)
    // for (i<- 0 until 8){
      // mat_in(i.U+addr/8.U):= in_data
      // cimtop.io.mat_in(i.U+addr/8.U):= in_data(7.U+i.U*8.U,i.U*8.U)
      // cimtop.io.mat_in(i.U+addr/8.U):= 0.U
    // }
    for (i<- 0 until 256)
    cimtop.io.mat_in(i):= regfile(addr)
  }

  .elsewhen (cmd.fire() && doSend_Output){
    state:=s_done
    cnt:=cnt+1.U
    cimtop.io.sram_enable:=false.B
    cimtop.io.sram_write:=false.B
    cimtop.io.compute_enable:=false.B
    cimtop.io.sram_addr:= DontCare
    cimtop.io.sram_dataIn := DontCare
    for (i<- 0 until 256)
    cimtop.io.mat_in(i):= regfile(addr)
    printf("doSend_Output,out_data=%x\n", cimtop.io.mat_out(0))
  }
  .otherwise{
    state:=s_idle
    cimtop.io.sram_enable:=false.B
    cimtop.io.sram_write:=false.B
    cimtop.io.compute_enable:=false.B
    cimtop.io.sram_addr:= 0.U(8.W)
    cimtop.io.sram_dataIn := 0.U(2048.W)
    for (i<- 0 until 256)
    cimtop.io.mat_in(i):= 0.U(256.W)
  }
  
  val doResp = cmd.bits.inst.xd

  cmd.ready := true.B
  // command resolved if no stalls AND not issuing a load that will need a request

  // PROC RESPONSE INTERFACE
  io.resp.valid := cmd.valid && doResp && cimtop.io.done  // here!!
  // valid response if valid command, need a response, and no stalls
  io.resp.bits.rd := cmd.bits.inst.rd
  // Must respond with the appropriate tag or undefined behavior
  // io.resp.bits.data := Cat(cimtop.io.mat_out(0)(23.U,8.U),cimtop.io.mat_out(1)(23.U,8.U),cimtop.io.mat_out(2)(23.U,8.U),cimtop.io.mat_out(3)(23.U,8.U)).asUInt  //here!
  io.resp.bits.data := 0.U
  // Semantics is to always send out prior accumulator register value

  io.busy := cmd.valid
  // Be busy when have pending memory requests or committed possibility of pending requests
  io.interrupt := false.B
  // Set this true to trigger an interrupt on the processor (please refer to supervisor documentation)

  // MEMORY REQUEST INTERFACE
  io.mem.req.valid := cmd.valid && doSend_Output
  io.mem.req.bits.addr := addend
  io.mem.req.bits.tag := addr
  io.mem.req.bits.cmd := M_XRD // perform a load (M_XWR for stores)
  io.mem.req.bits.size := log2Ceil(8).U
  io.mem.req.bits.signed := false.B
  io.mem.req.bits.data := cimtop.io.mat_out(cnt)(63.U,0,U)
  io.mem.req.bits.phys := false.B
  io.mem.req.bits.dprv := cmd.bits.status.dprv

}