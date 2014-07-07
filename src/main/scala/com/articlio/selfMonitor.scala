package com.articlio
import org.vertx.scala.core._
import org.vertx.scala.platform.Verticle

class selfMonitor extends Verticle {

  def percentThreshold = 10
  def interval = 1000

  // get the hook to memory consumption
  import runtime.{ totalMemory, freeMemory, maxMemory }
  private val runtime = Runtime.getRuntime()

  var former = getMem
  var current = former

  def getMem: Map[String, Float] = {
    val heapUsed  = totalMemory
    val heapTotal = maxMemory
    Map("heapUsed" -> heapUsed,
        "heapTotal" -> heapTotal,
        "heapPercent" -> heapUsed.toFloat / heapTotal * 100)
  }
  
  def logUsage {
    System.out.println(f"JVM Heap usage is ${current("heapPercent")}%.1f" + "%" + f" of JVM heap (${current("heapUsed")/1024/1024}%.0fMB of ${current("heapTotal")/1024/1024}%.0fMB)")
  }

  def logUsageIfChanged {

    current = getMem

    if (math.abs(current("heapPercent") - former("heapPercent")) / former("heapPercent") > (percentThreshold/100)) {    
      if (current("heapPercent") > former("heapPercent"))
        System.out.println("heap usage increased. New usage is:")
      else
        System.out.println("heap usage decreased. New usage is:")

      logUsage
      former = current
    }

  }

  override def start {
    logUsage
    logUsageIfChanged
    val timer = vertx.setPeriodic(interval, { timerID: Long => logUsageIfChanged })
  }
}

