vertx.createHttpServer.requestHandler { req: HttpServerRequest =>
  container.logger.info("request received")
  req.response.end("This is a Verticle script")
}.listen(sys.env.get("PORT").map(_.toInt).getOrElse(8091))

vertx.createHttpClient.setPort(3080).setHost("localhost").getNow("/", { response: HttpClientResponse =>
  response.bodyHandler({ data: Buffer =>
    container.logger.info("response received")
  })
})
