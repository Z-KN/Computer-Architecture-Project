package CIMTop

import chisel3._
import chisel3.util._
import chisel3.iotesters._

class SRAMTest(c: SRAM) extends PeekPokeTester(c){
	poke(c.io.enable,false)
	poke(c.io.write,false)
	// poke(c.io.addr,1)
	// val dataIn="h01010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101".U(2048.W)
	// val dataIn=Vec(256,0.U)
	for(i<- 0 until 256)
	{
		poke(c.io.dataIn(i),1.U)
	}
	// println(dataIn.toString)
	// poke(c.io.dataIn,dataIn)
	println("-------------In Testbench----------------")
	println("@ "+this.t.toString +" cycle")
	println(peek(c.io.dataOut).toString)

	step(2)
	poke(c.io.enable,true)
	poke(c.io.write,true)
	// poke(c.io.addr,1)
	// poke(c.io.dataIn,VecInit(Seq.fill(256)(3.U)))
	println("@ "+this.t.toString +" cycle")
	println(peek(c.io.dataOut).toString)

	// step(2)
	// poke(c.io.enable,true)
	// poke(c.io.write,false)
	// // poke(c.io.addr,1)
	// poke(c.io.dataIn,Array.fill(256)(4.U))
	// println("@ "+this.t.toString +" cycle")
	// println(peek(c.io.dataOut).toString)

	// step(2)
	// poke(c.io.enable,true)
	// poke(c.io.write,false)
	// // poke(c.io.addr,1)
	// poke(c.io.dataIn,Array.fill(256)(5.U))
	// println("@ "+this.t.toString +" cycle")
	// println(peek(c.io.dataOut).toString)

	// step(2)
	// println("@ "+this.t.toString +" cycle")
	// println(peek(c.io.dataOut).toString)
	println("-----------------------------------------")
}
