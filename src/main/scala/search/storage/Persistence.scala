package search.storage

import java.net.InetAddress

import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.common.unit.TimeValue
import org.elasticsearch.index.query.QueryBuilders._
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import search.actors.TranslationBundle
import search.core.Settings

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Persistence { //TODO refactor combinators, finish scroll implementation
  private val aggregationName = "aggr"
  private var client: TransportClient = null

  def init(name: String, port: Int): Unit = {
    client = TransportClient.builder().build()
      .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(name), port))
  }

  def close(): Unit = {
    client.close()
  }

  def findMostFrequent(fieldName: String, count: Int): Future[Map[String, Long]] = {
    val aggregation = AggregationBuilders
      .terms(aggregationName)
      .field(fieldName)
      .size(count)

    val request = client
      .prepareSearch()
      .setSize(0)
      .addAggregation(aggregation)

    val respond = RequestExecutor[SearchResponse]().execute(request)
    val result = respond map {
        _.getAggregations
        .get[Terms](aggregationName)
        .getBuckets
        .map(t => (t.getKeyAsString, t.getDocCount))
        .toMap
    }
    result
  }

  def scroll(fields: String*)(implicit size: Int = Settings.dbScrollSize): Future[Array[TranslationBundle]] = {
    val scrollResp = client.prepareSearch()
      .setScroll(new TimeValue(Settings.dbScrollTimeout))
      .addFields(fields:_*)
      .setQuery(matchAllQuery())
      .setSize(size)

    val respond = RequestExecutor[SearchResponse]().execute(scrollResp)
    val result = respond map {
        _.getHits
        .hits()
        .map(_.getFields)
        .map(f => f.values().map(_.getValues.get(0).toString))
        .map(f => TranslationBundle(f.head, f.tail.head))
    }
    result

//      scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet(); //TODO use wor continue scrolling through reviews
//    }
  }
}
