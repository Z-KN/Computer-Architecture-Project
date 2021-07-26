package chipyard

import CIMTop._
import freechips.rocketchip.config.{Config}
import freechips.rocketchip.diplomacy.{AsynchronousCrossing}

class CustomAcceleratorConfig extends Config(
    new WithCustomAccelerator ++
    new RocketConfig)
   

class CustomRoCC extends Config(
    new WithCIMRoCC ++
      new RocketConfig)
