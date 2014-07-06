package com.articlio

import org.vertx.scala.core._
import org.vertx.scala.core.http.HttpServerRequest
import org.vertx.scala.core.http.HttpClientResponse
import org.vertx.scala.platform.Verticle
import org.vertx.scala.core.buffer.Buffer

class deployer extends Verticle {
  override def start() {
    container.logger.info("starting")
    container.deployVerticle("scala:com.articlio.articlioScala")
  }
}

class articlioScala extends Verticle {
  override def start() {

    container.logger.info("starting")
    val client = vertx.createHttpClient.setPort(3080).setHost("localhost")

    //
    // http response handler
    //
    vertx.createHttpServer.requestHandler { req: HttpServerRequest =>
      container.logger.info("request received")
      req.response.setChunked(true)
      req.response.write("Verticle script received request\n")

      //
      // make request to node.js backend
      //
      client.getNow("/handleInputFile/?localLocation=LaeUusATIi5FHXHmF4hU", { response: HttpClientResponse =>
        response.bodyHandler({ data: Buffer =>
          container.logger.info("Got a response: " + response.statusCode())
          req.response.end("Verticle script finished handling request")
        })
      })
    }.listen(sys.env.get("PORT").map(_.toInt).getOrElse(8091))
  }
}
