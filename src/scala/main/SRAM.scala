package CIMTop

import chisel3._
import chisel3.util._
import chisel3.experimental.chiselName

// input and output definition is uint
// actually input and output is sint
class SRAMIO(depth:Int=1,width:Int=2048) extends Bundle{
    val enable = Input(Bool())
    val write = Input(Bool())
    val addr = Input(UInt(log2Up(depth).W))
    val dataIn = Input(UInt(width.W))
    val dataOut = Output(UInt(width.W))
	override def cloneType = (new SRAMIO(depth,width)).asInstanceOf[this.type]
}

@chiselName
class SRAM(depth:Int=1,width:Int=2048) extends Module {
    val io=IO(new SRAMIO(depth,width))
    val mem = SyncReadMem(depth, UInt(width.W))
    // Create one write port and one read port
    when(io.write&&io.enable)
    {mem.write(io.addr, io.dataIn)}
    io.dataOut := mem.read(io.addr, io.enable)
    // println(SRAM.getClass.getName.toString)
    // printf("dataOut=%d",io.dataOut)
}
