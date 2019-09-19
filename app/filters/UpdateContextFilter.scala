package filters

import akka.stream.Materializer
import javax.inject.Inject
import kamon.Kamon
import kamon.context.Context.Key
import org.joda.time.DateTime
import play.api.mvc.{Filter, RequestHeader, Result}
import support.LogSupport

import scala.concurrent.{ExecutionContext, Future}

class UpdateContextFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter with LogSupport {

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {

    val context = Kamon.currentContext().withEntry(UpdateContextFilter.customKey, DateTime.now())

    logger.info {
      s"New entry ${context.get(UpdateContextFilter.customKey)} set by ${Thread.currentThread()}"
    }

    Kamon.runWithContext(context)(f(rh))

  }

}

object UpdateContextFilter {
  val customKey = new Key[DateTime]("XXX", null)
}