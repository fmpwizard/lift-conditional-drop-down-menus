package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._

import reactive._
import web._
//import code.model._


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends Observing{
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _)

    // where to search snippet
    LiftRules.addToPackages("com.fmpwizard.code")

    // Build SiteMap
    def sitemap = SiteMap(
      Menu.i("Home") / "index" ,
      Menu.i("Ajax Form") / "ajax-form",
      Menu.i("Ajax Form 2") / "ajax2",
      Menu.i("Wiring") / "wiring",
      Menu.i("Wiring2") / "wiring2",
      Menu.i("Reactive-web") / "reactive",
      Menu.i("LiftActor") / "liftactorform" submenus (
        Menu.i("LiftActor In Action") / "myliftactor" >> Hidden
        )
      ,

      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu(Loc("Static", Link(List("static"), true, "/static/index"),
	       "Static Content")))



    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() =>sitemap)

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))



    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))    

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)

    //Test for Naftoli
    import reactive._
    import web._
    import html.Select
    import com.fmpwizard.code.snippet.CitiesAndStates3
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
  idsSelect.selectedIndex.value foreach {i => println("changed to " + i)}
  println(idsSelect.selectedIndex.value.now)

    println("Id is: %s".format(idsSelect.selectedItem.now))
    statesSelect.selectedIndex.value()=Some(5)
    println("Id is: %s".format(idsSelect.selectedItem.now))
    statesSelect.selectedIndex.value()=Some(0)
    println("Id is: %s".format(idsSelect.selectedItem.now))
    println(idsSelect.selectedIndex.value.now)
    println(citiesSelect.selectedIndex.value.now)
    citiesSelect.selectedIndex.value()=Some(1)
    println("Id is: %s".format(idsSelect.selectedItem.now))
    println(citiesSelect.selectedIndex.value.now)
    println(idsSelect.selectedIndex.value.now)



  }
}
