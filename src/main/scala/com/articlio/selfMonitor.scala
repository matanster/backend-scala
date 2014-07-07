package com.articlio
import org.vertx.scala.core._
import org.vertx.scala.platform.Verticle

class selfMonitor extends Verticle {

  // get the hook to memory consumption
  import runtime.{ totalMemory, freeMemory, maxMemory }
  private val runtime = Runtime.getRuntime()

  var former = getMem
  var current = former

  def getMem: Map[String, Float] = {
     return Map("heapUsed" -> totalMemory,
        "heapTotal" -> maxMemory,
        "heapPercent" -> totalMemory.toFloat / maxMemory * 100)
  }
  
  def logUsageIfChanged {

    //System.out.println(f"JVM heap usage : ${heapPercent}%.1f")
    System.out.println(f"Heap usage   : ${current("heapUsed")/1024/1024}%.0fMB")
    System.out.println(f"Heap size    : ${current("heapTotal")/1024/1024}%.0fMB")
    System.out.println(f"Heap percent : ${current("heapPercent")}%.1f" + "%")

  }

  override def start {
    logUsageIfChanged
    //val timer = vertx.setPeriodic(1000, { timerID: Long => log })
  }
}

