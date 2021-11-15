package io.demo.kafka.streams

object domain {

  type OrderId = String
  type VoucherId = String

  case class Order(id: Long, userId: Long, productId: Long, quantity: Int, price: Double, voucherId: String)
  case class Voucher(id: String, percentage: Double)
}
