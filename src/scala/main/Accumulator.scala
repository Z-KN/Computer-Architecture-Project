package CIMTop

import chisel3._
import chisel3.util._
import chisel3.experimental.chiselName

class AccumulatorIO(w_in:Int=16) extends Bundle{
	val enable=Input(Bool())
	val addend_A=Input(UInt(w_in.W))
	val inverse=Input(Bool())
	val sum=Output(SInt(24.W))
	val done=Output(Bool())
	override def cloneType = (new AccumulatorIO(w_in)).asInstanceOf[this.type]
}

@chiselName
class Accumulator(w_in:Int=16) extends Module{
	val io=IO(new AccumulatorIO(w_in))
	val reg=RegInit(0.U(24.W))
	val addend_B=Wire(UInt(24.W)) //TODO:width?
	val cnt=RegInit(7.U)
	val inv_A=Mux(io.inverse,~io.addend_A,io.addend_A)
	// printf("%d\n",inv_A);
	addend_B:=reg<<1.U
	reg:=inv_A+addend_B+io.inverse.asUInt
	when(io.enable){
		io.sum:=reg.asSInt
		cnt := -1.U
		io.done := cnt===0.U
	}
	.otherwise{
		io.sum:=0.S
		io.done:=false.B
	}
}