package v1.venues

import org.scalatestplus.play.PlaySpec
import play.api.Application
import play.api.i18n.Lang
import play.api.inject.guice.GuiceApplicationBuilder

class VenuesResourceHandlerTest extends PlaySpec {



  "should allow purchase of affordable venue"  in {
    val app : Application = GuiceApplicationBuilder().build()
    val playerId = "player2"
    val venuesResourceHandler : VenuesResourceHandler = app.injector.instanceOf[VenuesResourceHandler]
    val uuid : String = venuesResourceHandler.saveVenue(VenueDTO(None, "Maksimir", 1200.00, None))
    val result : VenuePurchaseResult = venuesResourceHandler.buyVenue(playerId, uuid, Lang("en"))
    result.isBought must equal(true)
    venuesResourceHandler.getVenues.filter( v => v.uuid == VenueId(uuid)).head.owner must equal(playerId)
  }

  "should not allow purchase of unaffordable venue" in {
    val app : Application = GuiceApplicationBuilder().build()
    val venuesResourceHandler : VenuesResourceHandler = app.injector.instanceOf[VenuesResourceHandler]
    val uuid : String = venuesResourceHandler.saveVenue(VenueDTO(None, "Maksimir", 1200.00, None))
    val result : VenuePurchaseResult = venuesResourceHandler.buyVenue("player1", uuid, Lang("en"))
    result.isBought must equal(false)
    venuesResourceHandler.getVenues.filter(v => v.uuid == VenueId(uuid)).head.owner must equal(null)
  }
}
