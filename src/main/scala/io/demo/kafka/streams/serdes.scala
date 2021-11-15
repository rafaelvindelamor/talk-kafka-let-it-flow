package io.demo.kafka.streams

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import io.demo.kafka.streams.domain.{Order, Voucher}
import org.apache.kafka.streams.scala.serialization.Serdes

object serdes {

  val orderSerializer: Order => Array[Byte] = (o: Order) => o.asJson.noSpaces.getBytes

  val orderDeserializer: Array[Byte] => Option[Order] = (data: Array[Byte]) => {
    val orderOrError = decode[Order](new String(data))
    orderOrError match {
      case Right(a) => Some(a)
      case Left(error) =>
        println(s"There was an error converting the message $orderOrError, $error")
        None
    }
  }

  implicit val orderSerde = Serdes.fromFn(orderSerializer, orderDeserializer)

  val voucherSerializer: Voucher => Array[Byte] = (v: Voucher) => v.asJson.noSpaces.getBytes

  val voucherDeserializer: Array[Byte] => Option[Voucher] = (data: Array[Byte]) => {
    val voucherOrError = decode[Voucher](new String(data))
    voucherOrError match {
      case Right(a) => Some(a)
      case Left(error) =>
        println(s"There was an error converting the message $voucherOrError, $error")
        None
    }
  }

  implicit val voucherSerde = Serdes.fromFn(voucherSerializer, voucherDeserializer)
}
