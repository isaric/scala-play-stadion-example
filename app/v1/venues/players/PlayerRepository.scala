package v1.venues.players

import java.util.concurrent.ConcurrentHashMap

import javax.inject.Singleton

import scala.collection._
import scala.jdk.CollectionConverters.ConcurrentMapHasAsScala

@Singleton
class PlayerRepository {

  val playerMap: concurrent.Map[String, Double] = new ConcurrentHashMap[String, Double]().asScala
  playerMap.put("player1", 500)
  playerMap.put("player2", 2000)

  def savePlayer(name : String, money : Double): Unit = {
    playerMap.put(name, money)
  }

  def getPlayerMoney(name : String): Option[Double] = {
    playerMap.get(name)
  }

}
