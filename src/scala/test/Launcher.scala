import CIMTop._

import chisel3._
import chisel3.iotesters.{Driver, TesterOptionsManager}
import utils.TutorialRunner

object Launcher {
  val tests = Map(
    "SRAM" -> { (manager: TesterOptionsManager) =>
      Driver.execute(() => new SRAM(), manager) {
        (c) => new SRAMTest(c)
      }
    },
    "AdderTree" -> { (manager: TesterOptionsManager) =>
      Driver.execute(() => new AdderTree(), manager) {
        (c) => new AdderTreeTest(c)
      }
    },
    "MulIn" -> { (manager: TesterOptionsManager) =>
      Driver.execute(() => new MulIn(), manager) {
        (c) => new MulInTest(c)
      }
    },
    "Accumulator" -> { (manager: TesterOptionsManager) =>
      Driver.execute(() => new Accumulator(), manager) {
        (c) => new AccumulatorTest(c)
      }
    },
    "CIMArray" -> { (manager: TesterOptionsManager) =>
      Driver.execute(() => new CIMArray(), manager) {
        (c) => new CIMArrayTest(c)
      }
    },
    "CIMTop" -> { (manager: TesterOptionsManager) =>
      Driver.execute(() => new CIMTop(), manager) {
        (c) => new CIMTopTest(c)
      }
    },
  )

  def main(args: Array[String]): Unit = {
    TutorialRunner("Project", tests, args)
  }
}
