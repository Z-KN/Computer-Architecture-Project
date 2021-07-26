package CIMTop

import chisel3._
import chisel3.util._
import chisel3.iotesters._

class CIMArrayTest(c: CIMArray) extends PeekPokeTester(c){
	poke(c.io.sram_enable,true)
	poke(c.io.write,true)
}
