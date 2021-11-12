package io.demo.kafka.streams

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.demo.kafka.streams.domain.{Order, Promotion}
import org.apache.kafka.streams.scala.serialization.Serdes

object serdes {

  val orderSerializer: Order => Array[Byte] = (o: Order) => o.asJson.noSpaces.getBytes

  val orderDeserializer: Array[Byte] => Option[Order] = (data: Array[Byte]) => {
    val orderOrError = decode[Order](new String(data))
    orderOrError match {
      case Right(a) => Option(a)
      case Left(error) =>
        println(s"There was an error converting the message $orderOrError, $error")
        Option.empty
    }
  }

  implicit val orderSerde = Serdes.fromFn(orderSerializer, orderDeserializer)

  val promotionSerializer: Promotion => Array[Byte] = (p: Promotion) => p.asJson.noSpaces.getBytes

  val promotionDeserializer: Array[Byte] => Option[Promotion] = (data: Array[Byte]) => {
    val promotionOrError = decode[Promotion](new String(data))
    promotionOrError match {
      case Right(a) => Option(a)
      case Left(error) =>
        println(s"There was an error converting the message $promotionOrError, $error")
        Option.empty
    }
  }

  implicit val promotionSerde = Serdes.fromFn(promotionSerializer, promotionDeserializer)
}
