package io.demo.kafka.streams

import io.demo.kafka.streams.domain.{Order, OrderId, Promotion, PromotionId}
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.{Named, Printed}
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{Branched, Consumed, Produced}
import org.apache.kafka.streams.scala.serialization.Serdes._
import serdes._

object OrderPromotionEnricherTopology {

  def apply(): Topology = {

    val builder = new StreamsBuilder()

    // Stream of orders
    val orders = builder.stream("orders")(Consumed.`with`[OrderId, Order])
    // GlobalKTable of promotions
    val promotions = builder.globalTable("promotions")(Consumed.`with`[PromotionId, Promotion])

    // Remove non-spanish orders as per business requirement
    val filtered = orders.filter { (_, order) =>
      order.country == "es"
    }

    // Predicates for split
    val promotionPresent = (_: OrderId, order: Order) => order.promotionId.nonEmpty
    val promotionNotPresent = (_: OrderId, order: Order) => order.promotionId.isEmpty

    // Branching for promotion to apply present or not
    val branches = filtered
      .split(Named.as("promotion_"))
      .branch(promotionPresent, Branched.as("present"))
      .branch(promotionNotPresent, Branched.as("not_present"))
      .noDefaultBranch()

    // If promotion is present apply it by joining with "promotions" GlobalKTable
    val promotionApplied = branches("promotion_present")
      .join(promotions)(
        (_: OrderId, o: Order) => o.promotionId,
        (o: Order, p: Promotion) => o.copy(price = o.price * (1 - p.percentage))
      )

    // Merge two branches back
    val merged = branches("promotion_not_present").merge(promotionApplied)

    // Send all the orders to another topic
    merged.to("orders-promotion-applied")(Produced.`with`[OrderId, Order])

    builder.build()
  }
}
