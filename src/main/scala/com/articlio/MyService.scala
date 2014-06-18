package com.articlio

import spray.routing._
import akka.actor.Actor
import akka.actor.ActorSystem
import spray.http._
import MediaTypes._
import akka.io.IO
import spray.httpx.RequestBuilding._
import scala.concurrent.Future
import spray.can.Http
import spray.http._
import akka.util.Timeout
import HttpMethods._
import akka.pattern.ask
import akka.event.Logging
import scala.concurrent.duration._
import spray.routing.RejectionHandler.Default
import spray.util.LoggingContext

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService with akka.actor.ActorLogging {

  log.info("Starting")

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  implicit val system: ActorSystem = ActorSystem()

  implicit val timeout: Timeout = Timeout(15.seconds)

  import system.dispatcher // implicit execution context

  //val logger = context.actorSelection("/user/logger")
  val logger = actorRefFactory.actorSelection("../logger")

  val myRoute =
  {
    def forward(): String = {
      logger ! Log("forwarding to backend")  
      val response: Future[HttpResponse] =
      (IO(Http) ? Get("http:3080//localhost/handleInputFile/?localLocation=LaeUusATIi5FHXHmF4hU")).mapTo[HttpResponse]    
      "<html><body><h1>api response after backend processing</h1></body></html>"
    }

    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete(forward)
        }
      }
    }
  }
}