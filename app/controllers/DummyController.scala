package controllers

import clients.SomeClient
import filters.UpdateContextFilter
import javax.inject.{Inject, Singleton}
import kamon.Kamon
import kamon.tag.Tag
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import support.LogSupport

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class DummyController @Inject()(val controllerComponents: ControllerComponents,
                                val someClient: SomeClient) extends BaseController with LogSupport {

  def get: Action[AnyContent] = Action { request =>

    logger.info(
      s"Entry ${Kamon.currentContext().get(UpdateContextFilter.customKey)} read by ${Thread.currentThread()}"
    )

    Ok(bodyResponse(controllers.routes.DummyController.get().url))
      .withHeaders(headers = "Custom-Header" -> "xxx")
  }

  def outgoingRequest: Action[AnyContent] = Action.async {
    someClient
      .call
      .map(response => {
        logger.info(s"Client response: $response")
        Ok(bodyResponse(
          name = controllers.routes.DummyController.outgoingRequest().url,
          msg = Some(
            s"""
               |Outgoing call to ${someClient.url}
               |Response:
               |\tHeaders:
               |\t\t${response.headers.mkString("[\n\t\t\t", "\n\t\t\t", "\n\t\t]")}
               |\tStatus: ${response.statusText}
               |\tBody: ${response.body}
             """.stripMargin)))
      })
  }

  private def bodyResponse(name: String, msg: Option[String] = None): String = {
    val tags = Kamon.currentContext()
      .tags.iterator()
      .map(t => {
        (t.key, Tag.unwrapValue(t).toString)
      })
      .mkString("[", ", ", "]")
    s"""
       |Request to: $name
       |Trace ID: ${Kamon.currentSpan().trace.id.string}
       |Span ID: ${Kamon.currentSpan().id.string}
       |Tags: $tags
       |${msg.getOrElse("")}
       |""".stripMargin
  }
}
