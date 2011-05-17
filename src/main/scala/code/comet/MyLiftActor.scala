package com.fmpwizard.code
package comet


import scala.xml.{NodeSeq, Text, Elem}

import net.liftweb._
import util._
import actor._
import http._
import common.{Box, Full,Logger}
import mapper.{OrderBy, Descending, SelectableField}
import http.SHtml._
import http.S._
import http.js.JsCmds.{SetHtml, SetValueAndFocus, Replace}
import http.js.jquery.JqJE._
import net.liftweb.http.js.JE.Str
import Helpers._


/**
 * This is the message we pass around to
 * register each named comet actor with a dispatcher that
 * only updates the specific version it monitors
 */
case class registerCometActor(actor: CometActor, version: String)

/**
 * The message we pass around to update the UI
 * once we are done with our long running process
 */
case class DoneMessage(CometActorName: String, City: String, State: String)

class Myliftactor extends CometActor with Logger {

  override def defaultPrefix = Full("comet")

  // time out the comet actor if it hasn't been on a page for 2 minutes
  override def lifespan = Full(120 seconds)



  /**
   * On page load, this method does a full page render
   */
  def render= {
    "#result *" #> "5 seconds after you submit this form, you will see a new message here."

  }

  /**
   * We can get two kinds of messages
   * 1- A DoneMessage, which has info about the long
   * running process we just finished.
   * A LiftActor sends this message
   *
   * 2- A string which is the version the comet actor is displaying info about
   * On page load we get this message
   *
   */
  override def lowPriority: PartialFunction[Any,Unit] = {
    case DoneMessage(cometActorName, city, state) => {
      info("Comet Actor %s will do a partial update".format(this))

      /**
       * each td in the html grid has an id that is
       * [0-9] + browser name
       * I use this to uniquely identify which cell to update
       *
       */
      partialUpdate(
        SetHtml("result", <span>I finished processing your form.<br />
          You submitted <b>{state}, {city}</b></span>)
      )
    }
    case Full(name: String)=> {
      info("[URL]: CometActor monitoring session: %s".format(name))

      /**
       * We get the DispatcherActor that sends message to all the
       * CometActors that are displaying a specific version number.
       * And we register ourselves with the dispatcher
       */

      //name map{n => MyListeners.listenerFor(n) ! registerCometActor(this, n) }
      MyListeners.listenerFor(name) ! registerCometActor(this, name)
      //MyListeners.listenerFor(name)
      info("Registering comet actor: %s".format(this))
      reRender()
    }
    case _ => info("Not sure how we got here.")
  }


}

/**
 * This class keeps a list of comet actors that need to update the UI
 * if we get new data through the rest api
 */
class DispatcherActor(name: String) extends LiftActor  with Logger{

  //info("DispatcherActor got version: %s".format(name))
  private var doneMessage= DoneMessage("", "Asheville", "North Carolina")
  private var cometActorsToUpdate: List[CometActor]= List()

  def createUpdate = doneMessage

  override def messageHandler  = {
    /**
     * if we do not have this actor in the list, add it (register it)
     */
    case registerCometActor(actor, name) =>
      if(cometActorsToUpdate.contains(actor) == false){
        info("We are adding actor: %s to the list".format(actor))
        cometActorsToUpdate= actor :: cometActorsToUpdate
      } else {
        info("The list so far is %s".format(cometActorsToUpdate))
      }

    /**
     * Go through the the list of actors and send them a cellToUpdate message
     */
    case DoneMessage(name, city, state) => {
      doneMessage = DoneMessage(name, city, state)
      info("We will update these comet actors: %s showing name: %s".format(
        cometActorsToUpdate, name))
      cometActorsToUpdate.foreach(_ ! doneMessage)
    }
    case _ => "Bye"
  }

}


/**
 * Keep a map of versions -> dispatchers, if no dispatcher is found, create one
 * comet actors get the ref to their dispatcher using this object,
 * so they can register themselves and the rest
 * api gets the dispatcher that is monitoring a specific version
 *
 */
object MyListeners extends Logger{
  //How about creating a ListenerManager (a separate Actor)
  //for each of the items you're going to have:


  private var listeners: Map[String, LiftActor] = Map()

  def listenerFor(str: String): LiftActor = synchronized {
    listeners.get(str) match {
      case Some(a) => info("Our map is %s".format(listeners)); a
      case None => {
        val ret = new DispatcherActor(str)
        listeners += str -> ret
        info("Our map is %s".format(listeners))
        ret
      }
    }
  }



  //So, you'll have a separate dispatcher for each of your URL parameters
  //and the CometActors can register with them and the REST thing can find
  //them to send the messages.



}