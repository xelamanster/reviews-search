package search.storage

import org.elasticsearch.action.{ActionListener, ActionRequestBuilder, ActionResponse}
import scala.concurrent.{Future, Promise}

object RequestExecutor {
  def apply[T <: ActionResponse](): RequestExecutor[T] = new RequestExecutor[T]
}

class RequestExecutor[T <: ActionResponse] extends ActionListener[T] {
  private val promise = Promise[T]()

  override def onResponse(response: T): Unit = promise.success(response)

  override def onFailure(e: Throwable): Unit = promise.failure(e)

  def execute[RB <: ActionRequestBuilder[_, T, _]](request: RB): Future[T] = {
    request.execute(this)
    promise.future
  }
}