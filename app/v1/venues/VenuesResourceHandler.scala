package v1.venues

import java.util.UUID

import javax.inject.Inject
import play.api.i18n.{Lang, MessagesApi}
import v1.venues.players.PlayerRepository


class VenuesResourceHandler @Inject() ( venuesRepository: VenuesRepository, messagesApi: MessagesApi, playerRepository: PlayerRepository) {


  def saveVenue(dto : VenueDTO): String = {
    val uuid = if (dto.uuid.isEmpty) UUID.randomUUID().toString else dto.uuid.get
    venuesRepository.saveVenue(VenueId(uuid), dto.name, dto.price, dto.owner.orNull)
  }

  def getVenues : List[Venue] = venuesRepository.getVenues

  def deleteVenue(uuid : String) : Option[String] = venuesRepository.deleteVenue(VenueId(uuid))

  def buyVenue(playerId : String, uuid : String, lang : Lang) : VenuePurchaseResult = {
    playerRepository.getPlayerMoney(playerId) match {
      case Some(money) => venuesRepository.getVenue(VenueId(uuid)) match {
        case Some(venue) => if (venue.price <= money) {
          playerRepository.savePlayer(playerId, money - venue.price)
          venuesRepository.saveVenue(venue.uuid, venue.name, venue.price, playerId)
          VenuePurchaseResult(isBought = true, messagesApi("venue.bought", venue.name, playerId, venue.price) (lang))
        } else VenuePurchaseResult(isBought = false, messagesApi("venue.not.affordable", playerId, venue.name) (lang))
        case None => VenuePurchaseResult(isBought = false, messagesApi("venue.unknown") (lang))
      }
      case None => VenuePurchaseResult(isBought = false, messagesApi("player.unknown") (lang))
    }
  }

}

case class VenuePurchaseResult(isBought : Boolean, statusMessage : String);
