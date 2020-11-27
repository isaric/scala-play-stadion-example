package v1.venues

import javax.inject.Inject
import play.api.libs.json.{Format, Json}
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class VenuesRouter @Inject()(controller: VenuesController) extends SimpleRouter {

  override def routes: Routes = {
    case GET(p"") =>
      controller.getVenues
    case PUT(p"/$uuid") =>
      controller.saveVenue(Option(uuid).filter(_.trim.nonEmpty))
    case DELETE(p"/$uuid") =>
      controller.deleteVenue(uuid)
    case POST(p"/$uuid") =>
      controller.buyVenue(Option(uuid).filter(_.trim.nonEmpty))
  }

}

case class VenueDTO(uuid : Option[String], name : String, price : Double, owner : Option[String]) {
  implicit val format: Format[VenueDTO] = Json.format
}
