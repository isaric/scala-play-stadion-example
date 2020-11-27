package v1.venues

import javax.inject.Inject
import play.api.http.FileMimeTypes
import play.api.i18n.{Lang, Langs, MessagesApi}
import play.api.libs.json.{JsError, JsSuccess, Json, OFormat}
import play.api.mvc._

class VenuesController @Inject() (vcc: VenuesControllerComponents) extends MessagesBaseController {

  implicit val venueDTOJSon: OFormat[VenueDTO] = Json.format[VenueDTO]

  def saveVenue(uuid : Option[String]) : Action[AnyContent] = Action { implicit  request =>
    request.body.asJson match {
      case Some(v) => Json.fromJson[VenueDTO](v) match {
        case JsSuccess(dto, _) => var venue = dto
          if (uuid.isDefined) venue = VenueDTO(Some(uuid.get), dto.name, dto.price, dto.owner)
          Ok(vcc.venuesResourceHandler.saveVenue(venue))
        case JsError(errors) => BadRequest(errors.toString())
      }
      case None => BadRequest
    }

  }

  def getVenues : Action[AnyContent] = Action { implicit request =>
    Ok(Json.toJson(vcc.venuesResourceHandler.getVenues.map(v => Json.toJson(VenueDTO(Some(v.uuid.value), v.name, v.price, Option(v.owner))))))
  }

  def deleteVenue(uuid : String) : Action[AnyContent] = Action { implicit request =>
    vcc.venuesResourceHandler.deleteVenue(uuid) match {
      case Some(v) => Ok(v)
      case None => NotFound
    }
  }

  def buyVenue(uuid : Option[String]) : Action[AnyContent] = Action { implicit request =>
    uuid match {
      case Some(uv) => request.body.asJson.flatMap(js => (js \ "playerId").asOpt[String]) match {
        case Some(pv) => vcc.venuesResourceHandler.buyVenue(pv, uv, request.acceptLanguages.headOption.getOrElse(Lang("en"))) match {
          case VenuePurchaseResult(true, description) => Ok(description)
          case VenuePurchaseResult(false, description) => BadRequest(description)
        }
        case None => BadRequest(request.messages("no.player.id") )
      }
      case None => BadRequest(request.messages("no.venue.specified") )
    }

  }


  override protected def controllerComponents: MessagesControllerComponents = vcc
}

case class VenuesControllerComponents @Inject()(
                                               venuesRepository: VenuesRepository,
                                               venuesResourceHandler: VenuesResourceHandler,
                                               actionBuilder: DefaultActionBuilder,
                                               parsers: PlayBodyParsers,
                                               messagesApi: MessagesApi,
                                               langs: Langs,
                                               fileMimeTypes: FileMimeTypes,
                                               messagesActionBuilder: MessagesActionBuilder,
                                               executionContext: scala.concurrent.ExecutionContext)
  extends MessagesControllerComponents {
}
