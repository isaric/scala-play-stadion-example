package v1.venues

import java.util.concurrent.ConcurrentHashMap

import javax.inject.{Inject, Singleton}
import play.api.{ConfigLoader, Configuration}

import scala.collection._
import scala.jdk.CollectionConverters.ConcurrentMapHasAsScala

@Singleton
class VenuesRepository @Inject() (configuration: Configuration ){

  val venueMap: concurrent.Map[VenueId, Venue] = new ConcurrentHashMap[VenueId, Venue]().asScala

  val playerMap: concurrent.Map[String, Double] = new ConcurrentHashMap[String, Double]().asScala
  configuration.get("startup.players")(ConfigLoader.seqStringLoader).grouped(2)
                                                                          .filter(g => g.length > 1)
                                                                          .foreach( g => playerMap.put(g.head, g.tail.head.toDouble))


  def saveVenue(uuid : VenueId, name : String, price: Double, owner : String): String = {
    venueMap.put(uuid, Venue(uuid, name, price, owner))
    uuid.value
  }

  def getVenues: List[Venue] = venueMap.values.toList

  def getVenue(uuid : VenueId) : Option[Venue] = {
    venueMap.get(uuid)
  }

  def deleteVenue(uuid : VenueId) : Option[String] = {
    venueMap.remove(uuid).map(v => v.uuid.toString)
  }

}

case class Venue(uuid : VenueId, name : String, price : Double, owner : String)
case class VenueId(value : String)
