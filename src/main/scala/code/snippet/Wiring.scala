/*
 * Copyright 2007-2010 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fmpwizard.code.snippet


import _root_.scala.xml._
import _root_.net.liftweb._
import http._
import S._
import SHtml._
import common._
import util._
import Helpers._
import js._
import JsCmds._
import js.jquery._







class Wiring {




  var state = Wiring.state
  var city = ""

  def taxRate = SHtml.ajaxSelect(
                  Wiring.states.map(i => (i, i)),
                  Full(1.toString),
                  selected => {
                    replace(selected)
                  }
                  )

  private def cityChoice(state: String): Elem = {
    val cities = Wiring.citiesFor(state)
    val first = cities.head
    // make the select "untrusted" because we might put new values
    // in the select
    untrustedSelect(cities.map(s => (s,s)), Full(first), s => city = s)
  }

  private def replace(state: String): JsCmd = {
    val cities = Wiring.citiesFor(state)
    val first = cities.head
    ReplaceOptions("city_select", cities.map(s => (s,s)), Full(first))
  }


  // bind the view to the dynamic HTML
  def show(xhtml: Group): NodeSeq = {
    val (name, js) = ajaxCall(JE.JsRaw("this.value"),
                              s => After(200, replace(s)))
    bind("select", xhtml,
         "city" -> cityChoice(state) % ("id" -> "city_select"),
         "submit" -> submit(?("Save"),
                            () =>
                            {S.notice("City: "+city+" State: "+state);
                             redirectTo("/")}))
  }
}

object Wiring {
  val citiesAndStates = List("Alabama" -> "Birmingham",
                             "Alabama" -> "Huntsville",
                             "Alabama" -> "Mobile",
                             "Alabama" -> "Montgomery",
                             "Arizona" -> "Phoenix",
                             "Arizona" -> "Scottsdale",
                             "Ohio" -> "Akron",
                             "Ohio" -> "Cincinnati",
                             "Texas" -> "Arlington",
                             "Texas" -> "Carrollton",
                             "Texas" -> "Corpus Christi",
                             "Texas" -> "Dallas",
                             "Texas" -> "Denton",
                             "Wisconsin" -> "Milwaukee")

  val states = citiesAndStates.map(_._1).distinct

  val state: String = states.head

  def citiesFor(state: String): List[String] = citiesAndStates.filter(_._1 == state).map(_._2)
}
