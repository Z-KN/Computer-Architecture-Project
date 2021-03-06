// See LICENSE.txt for license details.
package utils

import scala.collection.mutable.ArrayBuffer
import chisel3.iotesters._

object OptionsCopy {
  def apply(t: TesterOptionsManager): TesterOptionsManager = {
    new TesterOptionsManager {
      testerOptions = t.testerOptions.copy()
      interpreterOptions = t.interpreterOptions.copy()
      chiselOptions = t.chiselOptions.copy()
      firrtlOptions = t.firrtlOptions.copy()
      treadleOptions = t.treadleOptions.copy()
    }
  }
}

object TutorialRunner {
  def apply(section: String, tutorialMap: Map[String, TesterOptionsManager => Boolean], args: Array[String]): Unit = {
    var successful = 0
    val errors = new ArrayBuffer[String]

    val optionsManager = new TesterOptionsManager()
    optionsManager.doNotExitOnHelp()

    optionsManager.parse(args)

    val programArgs = optionsManager.commonOptions.programArgs

    if(programArgs.isEmpty) {
      println("Available modules")
      for(x <- tutorialMap.keys) {
        println(x)
      }
      println("all")
      System.exit(0)
    }

    val problemsToRun = if(programArgs.exists(x => x.toLowerCase() == "all")) {
      tutorialMap.keys
    }
    else {
      programArgs
    }

    for(testName <- problemsToRun) {
      tutorialMap.get(testName) match {
        case Some(test) =>
          println(s"Starting module $testName")
          try {
            // Start with a (relatively) clean set of options.
            val testOptionsManager = OptionsCopy(optionsManager)
            testOptionsManager.setTopName(testName)
            testOptionsManager.setTargetDirName(s"test_run_dir/$testName")
            if(test(testOptionsManager)) {
              successful += 1
            }
            else {
              errors += s"Module $testName: test error occurred"
            }
          }
          catch {
            case exception: Exception =>
              exception.printStackTrace()
              errors += s"Module $testName: exception ${exception.getMessage}"
            case t : Throwable =>
              errors += s"Module $testName: throwable ${t.getMessage}"
          }
        case _ =>
          errors += s"Bad module name: $testName"
      }

    }
    if(successful > 0) {
      println(s"Modules passing: $successful")
    }
    if(errors.nonEmpty) {
      println("=" * 80)
      println(s"Errors: ${errors.length}: in the following modules")
      println(errors.mkString("\n"))
      println("=" * 80)
      System.exit(1)
    }
  }
}
