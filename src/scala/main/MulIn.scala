package CIMTop

import chisel3._
import chisel3.util._
import chisel3.experimental.chiselName

class MulInIO(depth:Int=256,width:Int=8) extends Bundle{
	val enable=Input(Bool())
	val dataIn=Input(Vec(depth,UInt(width.W)))
	val dataOut=Output(Vec(depth,UInt(1.W)))
	val inverse=Output(Vec(depth,Bool()))
	override def cloneType = (new MulInIO(depth,width)).asInstanceOf[this.type]
}

@chiselName
class MulIn(depth:Int=256,width:Int=8) extends Module{
	val io=IO(new MulInIO(depth,width))
	val count = RegInit((width-1).U(log2Up(depth).W))
	when(io.enable){
	count:=count-1.U
	for (i <- 0 until depth){	
		io.dataOut(i):=io.dataIn(i)(count)
		io.inverse(i) := count === width.asUInt-1.U
		}
	}
	.otherwise{
		io.dataOut:=VecInit(Seq.fill(depth){0.U(1.W)})
		// io.inverse := VecInit(Seq.fill(depth){false.B})
		io.inverse := Array.fill(depth){false.B}
	}
	printf("MulIn.enable=%d\n",io.enable)
	printf("MulIn.dataIn=%d\n",io.dataIn(1))
	printf("MulIn.dataOut_0=%d\n",io.dataOut(0))
	printf("MulIn.dataOut_1=%d\n",io.dataOut(1))
	printf("MulIn.inverse=%d\n",io.inverse(1))

}