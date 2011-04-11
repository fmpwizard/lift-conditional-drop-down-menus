package com.fmpwizard.code.snippet

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

import scala.xml._
import net.liftweb._
import http._
import S._
import SHtml._
import common._
import util._
import Helpers._
import js._
import JsCmds._
import js.jquery._


import reactive._
import web._

class reactiveweb extends Logger with Observing{
  import html.Select
  val statesSelect= Select(Val(CitiesAndStates3.states))
  statesSelect.selectedIndex.value ()= Some(0)

  val validCitiesSignal= statesSelect.selectedItem map {
    case None => Nil
    case Some(state) => CitiesAndStates3.citiesFor(state)
  }

  val citiesSelect = Select(validCitiesSignal)
  citiesSelect.selectedIndex.value ()= Some(0)

  val validIdsSignal = citiesSelect.selectedItem.map(_.toList flatMap CitiesAndStates3.idsFor)
  val idsSelect = Select(validIdsSignal)
  idsSelect.selectedIndex.value ()= Some(0)
  //idsSelect.selectedIndex.value foreach {i => println("changed to " + i)}
  //println(idsSelect.selectedIndex.value.now==None)

  def render ={
    "#states" #> statesSelect &
    "#cities" #> citiesSelect &
    "#ids" #> idsSelect
  }
 
}

object CitiesAndStates3 extends Logger {
  val citiesAndStates = List("Alabama" -> "Birmingham",
                             "Alabama" -> "Huntsville",
                             "Alabama" -> "Mobile",
                             "Alabama" -> "Montgomery",
                             "Alaska" -> "Anchorage municipality",
                             "Arizona" -> "Chandler",
                             "Arizona" -> "Gilbert town",
                             "Arizona" -> "Glendale",
                             "Arizona" -> "Mesa",
                             "Arizona" -> "Peoria",
                             "Arizona" -> "Phoenix",
                             "Arizona" -> "Scottsdale",
                             "Arizona" -> "Tempe",
                             "Arizona" -> "Tucson",
                             "Arkansas" -> "Little Rock",
                             "California" -> "Anaheim"
)


  val idsAndCities =    List(
                             "Birmingham" -> "1",
                             "Huntsville" -> "2",
                             "Mobile" -> "3",
                             "Montgomery" -> "4",
                             "Anchorage municipality" -> "5",
                             "Chandler" -> "6",
                             "Gilbert town" -> "7",
                             "Glendale" -> "8",
                             "Mesa" -> "9",
                             "Peoria" -> "10",
                             "Phoenix" -> "11",
                             "Scottsdale" -> "12",
                             "Tempe" -> "13",
                             "Tucson" -> "14",
                             "Little Rock" -> "15",
                             "Anaheim" -> "16"
  )


  val states = citiesAndStates.map(_._1).distinct

  val state: String = states.head

  def citiesFor(state: String): List[String] = citiesAndStates.filter(_._1 == state).map(_._2)
  def idsFor(city: String): List[String] = {
    info("idsFor is called with city: :%s".format(city))
    idsAndCities.filter(_._1 == city).map(_._2)
  }
}
