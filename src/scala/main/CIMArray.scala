// CIMArray = bit-wise AND + 256 1-depth SRAMs + 256 adder trees
// input need be AND'ed
// output to accumulator 
package CIMTop

import chisel3._
import chisel3.util._
import chisel3.experimental.chiselName

class CIMArrayIO(w_in:Int=8,w_out:Int=16,K:Int=256,N:Int=256) extends Bundle{
	val sram_enable=Input(Bool())
	val write=Input(Bool())
	val addr=Input(UInt(log2Up(N).W)) //not within a single sram, but index all srams
	val dataIn=Input(UInt((w_in*K).W)) //TODO:error, (n)
	val mat_in=Input(Vec(K,UInt(1.W)))  //TODO:not full input
	val tree_out=Output(Vec(N,UInt(w_out.W)))
	// val inverse=Output(Vec(N,UInt(1.W)))
	override def cloneType = (new CIMArrayIO(w_in,w_out,K,N)).asInstanceOf[this.type]
}

class CIMArray(w_in:Int=8,w_out:Int=16,K:Int=256,N:Int=256) extends Module{
	val io=IO(new CIMArrayIO(w_in,w_out,K,N))
	// val srams=Array.fill(N){Module(new SRAM(K*w_in))} // 256*(256*8)
	// val adder_trees=Array.fill(N){Module(new AdderTree(N,w_in))} //256
	val srams=Array.fill(N)(Module(new SRAM(K*w_in)).io) // 256*(256*8)
	val adder_trees=Array.fill(N)(Module(new AdderTree(N,w_in)).io) //256
	val bit_mul=Wire(Vec(N,Vec(K,UInt(w_in.W))))
	//bit-wise multiplication
	for(n<- 0 until N){
		for (k<-0 until K){
			bit_mul(n)(k):=Fill(w_in,io.mat_in(k)) & srams(n).dataOut(w_in*k)
		}
	}
	for(n<-0 until N){
		//sram
		srams(n).enable:=io.sram_enable
		srams(n).write:=io.write
		srams(n).addr:=0.U(1.W)   //TODO:maybe error
		srams(n).dataIn:=io.dataIn //TODO:still error,(n)
		//adder_tree
		adder_trees(n).addend:=bit_mul(n)
		//cimtop
		io.tree_out(n):=adder_trees(n).sum
	}

	printf("adder_trees(0).addend0=%d , 1=%d\n",adder_trees(0).addend(0),adder_trees(0).addend(1))
	printf("adder_trees(0).sum=%d\n",adder_trees(0).sum)
	printf("cim_array.srams(0).dataIn=%d\n",this.srams(0).dataIn)
	printf("cim_array.srams(1).dataOut=%d\n",this.srams(1).dataOut)
	printf("mat_in=%d\n",io.mat_in(4))
}