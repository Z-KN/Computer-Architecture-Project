package CIMTop

import chisel3._
import chisel3.util._
import chisel3.iotesters._

class CIMTopTest(c: CIMTop) extends PeekPokeTester(c){
	poke(c.io.sram_enable,true)
	poke(c.io.sram_write,true)
	poke(c.io.sram_addr,1.U)
	// val dataIn="h01010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101".U(2048.W)
	for(i<- 0 until 256)
	{
		poke(c.io.dataIn(i),1.U)
	}
	poke(c.io.sram_dataIn,dataIn)
	poke(c.io.compute_enable,false)
	println("-------------In Testbench----------------")
	println("@ "+this.t.toString +" cycle")
	step(1)
	// printf("%d\n",c.cim_array.srams(0).dataIn(30,0))
	// println(peek(c.cim_array.srams(1).dataOut).toString)
	// println(peek(c.cim_array.adder_trees(0).sum).toString)
	
	println("@ "+this.t.toString +" cycle")
	step(1)
	// println(peek(c.cim_array.srams(0).dataIn).toString)
	// println(peek(c.cim_array.srams(1).dataOut).toString)
	// println(peek(c.cim_array.adder_trees(0).sum).toString)
	
	println("@ "+this.t.toString +" cycle")
	poke(c.io.sram_write,false)
	val mat_in=Array.fill(256){-5.S}
	for (i <- 0 until 256){
	poke(c.io.mat_in(i),mat_in(i))
	}
	poke(c.io.compute_enable,true)
	step(1)

	println("@ "+this.t.toString +" cycle")
	step(1)
	println("@ "+this.t.toString +" cycle")
	step(1)
	println("@ "+this.t.toString +" cycle")
	step(1)
	println("@ "+this.t.toString +" cycle")
	step(1)
	println("@ "+this.t.toString +" cycle")
	step(1)
	println("@ "+this.t.toString +" cycle")
	step(1)
	println("@ "+this.t.toString +" cycle")
	step(1)
	println("@ "+this.t.toString +" cycle")
	step(1)
	println("@ "+this.t.toString +" cycle")
	step(1)
	println("@ "+this.t.toString +" cycle")
	step(1)


	// println("cim_array.srams(0).dataOut = "+peek(c.cim_array.srams(0).dataOut).toString)
	// println("cim_array.srams(1).dataOut = "+peek(c.cim_array.srams(1).dataOut).toString)
	// println("cim_array.adder_trees(0).sum = "+peek(c.cim_array.adder_trees(0).sum).toString)
	// println("accumulators(0).addend_A = "+peek(c.accumulators(0).io.addend_A).toString)
	// println("accumulators(0).addend_B = "+peek(c.accumulators(0).addend_B).toString)
	// println("mat_out = "+peek(c.io.mat_out).toString)

	println("-----------------------------------------")
}