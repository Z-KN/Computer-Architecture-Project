package CIMTop

import chisel3._
import chisel3.util._
import chisel3.iotesters._

class MulInTest(c:MulIn) extends PeekPokeTester(c){
	val multiplier=Array.fill(256){"b11001100".U(8.W)}
	for (i <- 0 until 256){
	poke(c.io.dataIn(i),multiplier(i))
	}
	poke(c.io.enable,true)
	println("-------------In Testbench----------------")
	println("@ "+this.t.toString +" cycle")
	println(peek(c.io.dataOut).toString)

	step(1)
	println("@ "+this.t.toString +" cycle")
	println(peek(c.io.dataOut).toString)
	step(1)
	println("@ "+this.t.toString +" cycle")
	println(peek(c.io.dataOut).toString)
	step(1)
	println("@ "+this.t.toString +" cycle")
	println(peek(c.io.dataOut).toString)
	step(1)
	println("@ "+this.t.toString +" cycle")
	println(peek(c.io.dataOut).toString)
	step(1)
	println("@ "+this.t.toString +" cycle")
	println(peek(c.io.dataOut).toString)
	step(1)
	println("@ "+this.t.toString +" cycle")
	println(peek(c.io.dataOut).toString)
	step(1)
	println("@ "+this.t.toString +" cycle")
	println(peek(c.io.dataOut).toString)
	step(1)
	println("@ "+this.t.toString +" cycle")
	println(peek(c.io.dataOut).toString)
	step(1)
	println("@ "+this.t.toString +" cycle")
	println(peek(c.io.dataOut).toString)
	step(1)
	println("@ "+this.t.toString +" cycle")
	println(peek(c.io.dataOut).toString)
	step(1)
	println("@ "+this.t.toString +" cycle")
	println(peek(c.io.dataOut).toString)
	println("-----------------------------------------")
}