package CIMTop

import chisel3._
import chisel3.util._
import freechips.rocketchip.config._
import freechips.rocketchip.tile._
import freechips.rocketchip.diplomacy._

class WithCIMRoCC extends Config((site,here,up) => {
    //  case InitZeroKey => Some(InitZeroConfig(base, size))
    case BuildRoCC => Seq((p:Parameters)=>{
      implicit val cimrocc = implicitly[ValName]
      LazyModule(new CIMRoCC(OpcodeSet.custom1)(p))
    })
})

