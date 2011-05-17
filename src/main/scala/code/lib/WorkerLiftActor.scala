package com.fmpwizard.code
package lib


import net.liftweb._
import util._
import actor._
import common.Logger

import comet._
import comet.MyListeners._
import snippet.cometName

class WorkerLiftActor extends LiftActor  with Logger{


  private var doneMessage= DoneMessage("name", "Asheville", "North Carolina")

  def createUpdate = doneMessage

  override def messageHandler  = {
    /**
     * Go through the the list of actors and send them a cellToUpdate message
     */
    case DoneMessage(name, city, state) => {
      doneMessage = DoneMessage(name, city, state)

      /**
       * Here we pretend to do some
       * intensive work that takes about
       * 5 seconds
       */

      Thread.sleep(5000)
      listenerFor(name) match {
        case a: LiftActor => info("LiftActor is done, do the update! %s".format(name)); a ! doneMessage
        case _ => info("No actor to send an update")
      }
    }
    case x => "I don't know what to do with this message: %s".format(x)
  }

}