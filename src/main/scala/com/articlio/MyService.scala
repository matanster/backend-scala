package com.articlio

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._

import akka.event.Logging

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

  //val logger = context.actorSelection("/user/logger")
  val logger = actorRefFactory.actorSelection("../logger")

  val myRoute =
  {
    def forward(): String = {
      logger ! Log("forwarding to backend")      
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