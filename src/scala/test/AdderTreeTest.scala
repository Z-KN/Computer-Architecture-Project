package CIMTop

import chisel3._
import chisel3.util._
import chisel3.iotesters._

class AdderTreeTest(c:AdderTree) extends PeekPokeTester(c){
	val addend=Array.fill(256){-1.S}
	for (i <- 0 until 256){
	poke(c.io.addend(i),addend(i))
	}
	step(1)
	println("-------------In Testbench----------------")
	println("@ "+this.t.toString +" cycle")
	println(peek(c.io.sum).toString)
	println("-----------------------------------------")
}