package CIMTop

import chisel3._
import chisel3.util._
import chisel3.iotesters._

class AccumulatorTest(c:Accumulator) extends PeekPokeTester(c){
	poke(c.io.enable,true)
	poke(c.io.addend_A,3.U)
	poke(c.io.inverse,false)
	println("-------------In Testbench----------------")
	for (i<-0 until 5){
		step(1)
		println("@ "+this.t.toString +" cycle")
		println(peek(c.io.sum).toString)
	}
	println("-----------------------------------------")
}