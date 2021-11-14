package io.demo.kafka.streams

import io.demo.kafka.streams.domain.{Order, OrderId, Promotion, PromotionId}
import io.demo.kafka.streams.serdes._
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{Branched, Consumed, Produced}
import org.apache.kafka.streams.scala.serialization.Serdes._

object OrderPromotionEnricherTopology {

  def apply(): Topology = {

    // We start invoking StreamsBuilder
    val builder = new StreamsBuilder()

    // Stream of orders
    val orders = builder.stream("orders")(Consumed.`with`[OrderId, Order])
    // GlobalKTable of promotions
    val promotions = builder.globalTable("promotions")(Consumed.`with`[PromotionId, Promotion])

    // Predicates for split
    val promotionPresent = (_: OrderId, order: Order) => order.promotionId.nonEmpty
    val promotionNotPresent = (orderId: OrderId, order: Order) => !promotionPresent(orderId, order)

    // Branching for promotion to apply present or not
    val branches = orders
      .split(Named.as("promotion_"))
      .branch(promotionPresent, Branched.as("present"))
      .branch(promotionNotPresent, Branched.as("not_present"))
      .noDefaultBranch()

    // If promotion is present in the order, apply it by joining with promotions GlobalKTable
    val promotionApplied = branches("promotion_present")
      .join(promotions)(
        (_: OrderId, o: Order) => o.promotionId,
        (o: Order, p: Promotion) => o.copy(price = o.price * (1 - p.percentage))
      )

    // Merge promotions branches back
    val merged = branches("promotion_not_present").merge(promotionApplied)

    // Send all the orders to another topic
    merged.to("orders-promotion-applied")(Produced.`with`[OrderId, Order])

    builder.build()
  }
}
