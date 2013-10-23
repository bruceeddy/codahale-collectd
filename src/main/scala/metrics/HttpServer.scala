package metrics


import java.net.InetSocketAddress
import java.util.concurrent.Executors

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.asScalaSet

import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONNECTION
import org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH
import org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE
import org.jboss.netty.handler.codec.http.HttpHeaders.Values.KEEP_ALIVE
import org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive
import org.jboss.netty.handler.codec.http.HttpResponseStatus.OK
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1

import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.channel.ChannelPipelineFactory
import org.jboss.netty.handler.codec.http.HttpRequestDecoder
import org.jboss.netty.handler.codec.http.HttpChunkAggregator
import org.jboss.netty.handler.codec.http.HttpResponseEncoder
import org.jboss.netty.handler.codec.http.HttpContentCompressor
import org.jboss.netty.channel.SimpleChannelUpstreamHandler
import org.jboss.netty.channel.ChannelHandlerContext
import org.jboss.netty.channel.MessageEvent
import org.jboss.netty.handler.codec.http.HttpRequest
import org.jboss.netty.handler.codec.http.QueryStringDecoder
import org.jboss.netty.channel.ExceptionEvent
import org.jboss.netty.channel.Channel
import org.jboss.netty.handler.codec.http.HttpResponse
import org.jboss.netty.handler.codec.http.DefaultHttpResponse
import org.jboss.netty.util.CharsetUtil
import org.jboss.netty.channel.ChannelFutureListener

trait HttpServer {
  def startHttpServer: Unit
}

trait NettyHttpServer extends HttpServer with HttpResponseWriter {

  this: ChannelPipelineFactory =>

  val httpPort = 5000

  def startHttpServer = {

    val bootstrap = new ServerBootstrap(
      new NioServerSocketChannelFactory(
        Executors.newCachedThreadPool,
        Executors.newCachedThreadPool))

    bootstrap.setPipelineFactory(this)
    bootstrap.bind(new InetSocketAddress(httpPort))
  }
}

trait HttpServerPipelineFactory extends ChannelPipelineFactory {

  val handler: SimpleChannelUpstreamHandler

  override def getPipeline = {

    val pipeline = org.jboss.netty.channel.Channels.pipeline

    pipeline.addLast("decoder", new HttpRequestDecoder)
    pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
    pipeline.addLast("encoder", new HttpResponseEncoder)
    pipeline.addLast("deflater", new HttpContentCompressor)
    pipeline.addLast("handler", handler)
    pipeline
  }
}

trait ResponseBody {
  def body: String
}

trait HttpHandler extends ResponseWriter {
  this: ResponseBody =>

  val handler = new SimpleChannelUpstreamHandler {

    override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
      val request: HttpRequest = e.getMessage.asInstanceOf[HttpRequest]


      object message {


        val queryDecoder = new QueryStringDecoder(request.getUri())
        val rawParams = queryDecoder.getParameters
        val decodedParams = rawParams.keySet.map {
          key =>
            key -> rawParams.get(key).head
        }.toMap

        def params = decodedParams

      }

      writeResponse(request, e.getChannel, body)
    }


    override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent) {
      e.getCause.printStackTrace
      e.getChannel.close
    }
  }
}

trait ResponseWriter {
  def writeResponse(request: HttpRequest, channel: Channel, responseText: String)
}

trait HttpResponseWriter extends ResponseWriter {
  override def writeResponse(request: HttpRequest, channel: Channel, responseText: String) = {
    // Decide whether to close the connection or not.
    val keepAlive = isKeepAlive(request)

    // Build the response object.
    val response: HttpResponse = new DefaultHttpResponse(HTTP_1_1, OK)
    response.setContent(copiedBuffer(responseText, CharsetUtil.UTF_8))
    response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8")

    if (keepAlive) {
      // Add 'Content-Length' header only for a keep-alive connection.
      response.setHeader(CONTENT_LENGTH, response.getContent.readableBytes)
      // Add keep alive header as per:
      // -
      // http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
      response.setHeader(CONNECTION, KEEP_ALIVE)
    }

    // Write the response.
    val future = channel.write(response);

    // Close the non-keep-alive connection after the write operation is
    // done.
    if (!keepAlive) {
      future.addListener(ChannelFutureListener.CLOSE);
    }
  }

}


