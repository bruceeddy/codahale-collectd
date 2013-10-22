package metrics

import com.codahale.metrics._
import java.util.concurrent.{TimeUnit, ArrayBlockingQueue}
import java.util
import scala.util.Random
import com.codahale.metrics.jvm._

import scala.collection.JavaConversions._
import java.io.PrintWriter
import java.net.InetAddress
import org.jboss.netty.channel.SimpleChannelUpstreamHandler

/**
 * Created with IntelliJ IDEA.
 * User: bruce
 * Date: 12/10/13
 * Time: 22:04
 * To change this template use File | Settings | File Templates.
 */
object Main {

         val registry = new MetricRegistry


         def main(args: Array[String]) = {
           val queue = new ArrayBlockingQueue[String](1000)

           registry.register(MetricRegistry.name("queue", "size"),
              new Gauge[Integer] {
                override def getValue = queue.size
              }
           )

           queuePlay.start(queue)

           registry.registerAll(new GarbageCollectorMetricSet)
           registry.registerAll(new ThreadStatesGaugeSet)
           registry.register(MetricRegistry.name("filedescriptor", "ratio"),new FileDescriptorRatioGauge)
           registry.registerAll(new MemoryUsageGaugeSet)

           val hostname = InetAddress.getLocalHost.getCanonicalHostName
           val appname = "test-app"

           val httpServer = new NettyHttpServer with HttpServerPipelineFactory with HttpHandler with ResponseBody {
             def body = {
               val timestamp = System.currentTimeMillis

               def gaugeLine(x: (String,Metric)): String = {
                 val key = x._1
                 val gauge = x._2
                 val set = gauge.getClass.getName.split("\\.").reverse.take(2).reverse.mkString(".").split("\\$").head
                 val value = gauge.toString


                 s"""PUTVAL "$hostname/$appname/gauge-$set.$key" $timestamp:$value\n"""
               }

               registry.getMetrics.map(gaugeLine).toString
             }
           }
           httpServer.startHttpServer
         }
}

object queuePlay {

  def start(queue: util.Queue[String]) = {
    val enqueue = new Runnable {
      def run = {
        while( true )  {
        Thread.sleep(Random.nextInt(100))
        queue.add("foo")
        }
      }
    }

    val dequeue = new Runnable {
      def run = {
        while( true )  {
        Thread.sleep(Random.nextInt(300))
        queue.poll()
        }
      }
    }

    new Thread(dequeue).start
    new Thread(enqueue).start
  }

}