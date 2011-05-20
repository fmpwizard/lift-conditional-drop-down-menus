package com.fmpwizard.code
package snippet


import scala.xml.NodeSeq

import net.liftweb._
import util._
import actor._
import http._
import Helpers._
import common.{Full, Logger, Box, Empty, Failure}


object cometName extends RequestVar[Box[String]](Empty)

/**
  * This object adds a ComeActor of type Myliftactor with a name == random string
  * This allows having multiple tabs open displaying data for different contexts
  */
object PlaceCometOnPage extends Logger{



  def render(xhtml: NodeSeq): NodeSeq = {
    val id = Helpers.nextFuncName
    info("The current cometActor name is %s".format(cometName.is))
    cometName.is match {
      case Empty => cometName.set(Full(id))
      case _ => Unit


    }

    info("The current cometActor name is %s".format(cometName.is))

    /**
     * You can set the id of the comet actor to be something you know the
     * value in advance. Using something like S.param("query_param")
     * or using the Menu.Param technique
     */
    //val id= "notifier" + S.param("q")
    info("Using CometActor with name: %s".format(cometName.is))
    for (sess <- S.session) sess.sendCometActorMessage("Myliftactor", cometName.is, cometName.is)
    <lift:comet type="Myliftactor" name={cometName.is.openOr("noName")}>{xhtml}</lift:comet>
  }
}