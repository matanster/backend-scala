package com.articlio

import org.vertx.scala.core._
import org.vertx.scala.core.http.HttpServerRequest
import org.vertx.scala.core.http.HttpClientResponse
import org.vertx.scala.platform.Verticle
import org.vertx.scala.core.buffer.Buffer
import java.net.URLEncoder

import spray.json._
import DefaultJsonProtocol._

class deployer extends Verticle {
  override def start() {
    container.logger.info("starting deployer verticle")
    container.deployVerticle("scala:com.articlio.articlioScala")
    container.deployVerticle("scala:com.articlio.selfMonitor")
  }
}

class articlioScala extends Verticle {
  override def start() {

    container.logger.info("starting real verticle")
    val client = vertx.createHttpClient.setPort(3080).setHost("localhost")
    val selfInvoker = vertx.createHttpClient.setPort(8091).setHost("localhost")

    //
    // http response handler
    //
    vertx.createHttpServer.requestHandler { req: HttpServerRequest =>
      container.logger.info("request received")
      req.response.setChunked(true)
      req.response.write("Verticle script received request\n")
      container.logger.info(req.path)
      container.logger.info(req.params)
      for ((key, value) <- req.params) { println(s"request parameters: ${key}=${value.mkString(";")}") }

      //
      // make request to node.js backend
      //

      //val file: String = "To Belong or Not to Belong, That Is the Question"
      val file: String = "Second language1"
      val requestUrl: String = "/handleInputFile/?localLocation=" + URLEncoder.encode(file, "UTF-8")
      container.logger.info(requestUrl)

      client.getNow(requestUrl, { response: HttpClientResponse => // LaeUusATIi5FHXHmF4hU
        response.bodyHandler({ data: Buffer =>
          container.logger.info("Got a response: " + response.statusCode())
          val jsonAst = data.toString.parseJson
          //container.logger.info(jsonAst.prettyPrint)
          //container.logger.info(data)

          req.response.end("Verticle script finished handling request")
        })
      })
    }.listen(sys.env.get("PORT").map(_.toInt).getOrElse(8091))

  // invoke self on startup, for iterative development
  selfInvoker.getNow("/?a=b", { response: HttpClientResponse => })
  }
}