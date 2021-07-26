package CIMTop

import chisel3._
import chisel3.util._
import chisel3.experimental.chiselName

class AdderTreeIO(num_in:Int=256,width:Int=8) extends Bundle{
	val w_out=width+log2Up(num_in)
	val addend = Input(Vec(num_in,UInt(width.W)))
	val sum = Output(UInt(w_out.W))
	override def cloneType = (new AdderTreeIO(num_in,width)).asInstanceOf[this.type]
}

@chiselName
class AdderTree(num_in:Int=256,width:Int=8) extends Module{
	val io=IO(new AdderTreeIO(num_in,width))
	val adderWire=Wire(Vec(num_in-1,UInt(16.W)))
	// var num = num_in
	// def oneLayer(in:Vec[UInt]):Vec[UInt]={
	// 	when(in.length.asUInt === 1.U)
	// 	{
	// 	in
	// 	}
	// 	.otherwise{
	// 		val out = Vec(in.length/2,UInt())
	// 		for(i<- 0 until in.length/2){
	// 			out(i):=in(2*i)+in(2*i+1)
	// 		}
	// 	oneLayer(out)
	// 	}
	// }

	// num = num /2
	// var index = 0
	// for(layer <- 0 until 8)
	// {
	// 	for(i<-0 until num)
	// 	{
	// 		when((i+index < num_in/2).asBool)
	// 		{
	// 			adderWire(i+index):=io.addend(2*i+index)+io.addend(2*i+1+index)
	// 		}
	// 		.otherwise
	// 		{
	// 			adderWire(i+index):=adderWire(index-2*num+2*i)+adderWire(index-2*num+2*i+1)
	// 		}
	// 	}
	// 	index = num+index
	// 	num = num/2
	// }

	for (i <- 0 until 128)
	adderWire(i):= io.addend(2*i)+io.addend(2*i+1)
	for (i <- 0 until 64)
	adderWire(128+i):= adderWire(2*i)+adderWire(2*i+1)
	for (i <- 0 until 32)
	adderWire(128+64+i):= adderWire(128+2*i)+adderWire(128+2*i+1)
	for (i <- 0 until 16)
	adderWire(128+64+32+i):= adderWire(128+64+2*i)+adderWire(128+64+2*i+1)
	for (i <- 0 until 8)
	adderWire(128+64+32+16+i):= adderWire(128+64+32+2*i)+adderWire(128+64+32+2*i+1)
	for (i <- 0 until 4)
	adderWire(128+64+32+16+8+i):= adderWire(128+64+32+16+2*i)+adderWire(128+64+32+16+2*i+1)
	for (i <- 0 until 2)
	adderWire(128+64+32+16+8+4+i):= adderWire(128+64+32+16+8+2*i)+adderWire(128+64+32+16+8+2*i+1)
	for (i <- 0 until 1)
	adderWire(128+64+32+16+8+4+2+i):= adderWire(128+64+32+16+8+4+2*i)+adderWire(128+64+32+16+8+4+2*i+1)
	io.sum:=adderWire(num_in-2)
	// printf("---%d",adderWire(4).getWidth)
}