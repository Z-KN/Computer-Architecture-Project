// This is the top module
// M*K times K*N
// default:M=2048, K=256, N=256 (sometimes called depth)
package CIMTop

import chisel3._
import chisel3.util._
import chisel3.experimental.chiselName

class CIMTopIO(w_in:Int=8,w_out:Int=24,K:Int=256,N:Int=256) extends Bundle{
	val sram_enable=Input(Bool())
	val sram_write=Input(Bool())
	val sram_addr=Input(UInt(log2Up(N).W)) //not within a single sram, but index all srams
	val sram_dataIn=Input(UInt((w_in*K).W))
	val compute_enable=Input(Bool())
	val mat_in=Input(Vec(K,UInt(w_in.W)))  //TODO:not full input
	val mat_out=Output(Vec(N,SInt(w_out.W)))
	val done=Output(Bool())
	override def cloneType = (new CIMTopIO(w_in,w_out,K,N)).asInstanceOf[this.type]
}

@chiselName
class CIMTop(w_in:Int=8,w_out:Int=24,K:Int=256,N:Int=256) extends Module{
	val io=IO(new CIMTopIO(w_in,w_out,K,N))
	val mul_in=Module(new MulIn(K,w_in)) //TODO:must be modified
	val cim_array=Module(new CIMArray(w_in,w_out,K=K,N=N))
	val accumulators=Array.fill(N){Module(new Accumulator(w_out))} //256

	// mul_in
	mul_in.io.enable:=io.compute_enable
	mul_in.io.dataIn:=io.mat_in
	//cim_array
	cim_array.io.sram_enable:=io.sram_enable
	cim_array.io.write:=io.sram_write
	cim_array.io.addr:=io.sram_addr
	cim_array.io.dataIn:=io.sram_dataIn
	cim_array.io.mat_in:=mul_in.io.dataOut
	//accumulator
	for (n <- 0 until N){
		accumulators(n).io.enable:=io.compute_enable
		accumulators(n).io.addend_A:=cim_array.io.tree_out(n)
		accumulators(n).io.inverse:=mul_in.io.inverse(n)
		io.mat_out(n):=accumulators(n).io.sum
	}
	io.done:= accumulators(0).io.done
	// for (i<-0 until depth){
	// 	//sram
	// 	srams(i).io.enable:=io.enable
	// 	srams(i).io.write:=io.sram_write
	// 	srams(i).io.addr:=io.sram_addr   //TODO:still error
	// 	srams(i).io.dataIn:=io.mat_in(i) //TODO:still error
	// 	//adder_tree
	// 	adder_trees(i).io.addend:=Fill(depth,srams(i).io.dataOut)
	// 	//accumulator
	// 	accumulators(i).io.enable:=io.enable
	// 	accumulators(i).io.addend_A:=adder_trees(i).io.sum
	// 	//cimtop
	// 	io.mat_out(i):=accumulators(i).io.sum
	// }
}