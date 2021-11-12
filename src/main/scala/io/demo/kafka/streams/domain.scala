package io.demo.kafka.streams

object domain {

  type OrderId = String
  type PromotionId = String

  case class Order(orderId: Long, userId: Long, productId: Long, country: String, quantity: Int, price: Double, promotionId: String)
  case class Promotion(promotionId: String, percentage: Double)
}
