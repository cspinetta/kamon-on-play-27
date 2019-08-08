package support.kamon

import java.net.InetAddress
import java.util.concurrent.ThreadLocalRandom

import kamon.context.HttpPropagation.{HeaderReader, HeaderWriter}
import kamon.context.{Context, HttpPropagation, Propagation}
import kamon.tag.Lookups.option
import kamon.tag.TagSet


object HeadersPropagation {

  object Headers {
    val CustomTraceId = "X-Custom-Trace-Id"
    val CustomSpanId = "X-Custom-Span-Id"
    val UserAgent = "User-Agent"
  }

  val EmptyForwardedFor = "undefined-forwarded-for"
  val EmptyUserAgent = "undefined-user-agent"


  case class TagFiller(key: String, value: String)

  object Utils {
    lazy val hostname: String = InetAddress.getLocalHost.getHostName

    private def nextId: Long = ThreadLocalRandom.current().nextLong(0, Long.MaxValue)

    def nextCustomTraceId: String = s"$hostname-$nextId"

    def nextCustomSpanId: String = nextId.toString
  }

}

class HeadersPropagation extends Propagation.EntryReader[HeaderReader] with Propagation.EntryWriter[HeaderWriter] {

  import HeadersPropagation.{Headers, Utils}

  override def read(reader: HttpPropagation.HeaderReader, context: Context): Context = {
    context.withTags(TagSet.builder()
      .add(Headers.CustomTraceId, reader.read(Headers.CustomTraceId).getOrElse(Utils.nextCustomTraceId))
      .add(Headers.CustomSpanId, Utils.nextCustomSpanId)
      .add(Headers.UserAgent, reader.read(Headers.UserAgent).getOrElse(HeadersPropagation.EmptyUserAgent))
      .build())
  }

  override def write(context: Context, writer: HttpPropagation.HeaderWriter): Unit = {
    outgoingTags.foreach(tag => context.getTag(option(tag)).foreach(value => writer.write(tag, value)))
  }

  val outgoingTags = List(
    Headers.CustomTraceId,
    Headers.CustomSpanId,
  )
}
