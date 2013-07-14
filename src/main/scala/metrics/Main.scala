package metrics

import com.codahale.metrics._
import java.util.concurrent.{TimeUnit, ArrayBlockingQueue}
import java.util
import scala.util.Random
import com.codahale.metrics.jvm._

/**
 * Created with IntelliJ IDEA.
 * User: bruce
 * Date: 12/10/13
 * Time: 22:04
 * To change this template use File | Settings | File Templates.
 */
object Main {
                     /*  $nc -U /var/run/collectd-unixsock > /tmp/out < /tmp/in

                     $cat > /tmp/in
        LISTVAL
                              $cat /tmp/out */


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


/*          val reporter = ConsoleReporter.forRegistry(registry)
             .convertRatesTo(TimeUnit.SECONDS)
             .convertDurationsTo(TimeUnit.MILLISECONDS)
             .build()*/;


           val reporter = new CollectdReporter(registry, "collectd-reporter", MetricFilter.ALL, TimeUnit.SECONDS, TimeUnit.MILLISECONDS)

           reporter.start(5, TimeUnit.SECONDS);
         }
}


class CollectdReporter(registry: MetricRegistry,
                       name: String,
                       filter: MetricFilter,
                       rateUnit: TimeUnit,
                       durationUnit: TimeUnit) extends ScheduledReporter(registry, name, filter, rateUnit, durationUnit) {

  override def report(gauges: util.SortedMap[String, Gauge[_]],
                      counters: util.SortedMap[String, Counter],
                      histograms: util.SortedMap[String, Histogram],
                      meters: util.SortedMap[String, Meter],
                      timers: util.SortedMap[String, Timer]) = {
    println(gauges)
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