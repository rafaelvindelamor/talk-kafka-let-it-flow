package io.demo.kafka.streams

import io.demo.kafka.streams.domain.{Order, OrderId, Voucher, VoucherId}
import io.demo.kafka.streams.serdes._
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Named
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{Branched, Consumed, Produced}
import org.apache.kafka.streams.scala.serialization.Serdes._

object OrderVoucherEnricherTopology {

  def apply(): Topology = {

    // We start invoking StreamsBuilder
    val builder = new StreamsBuilder()

    // Stream of orders
    val orders = builder.stream("orders")(Consumed.`with`[OrderId, Order])
    // GlobalKTable of vouchers
    val vouchers = builder.globalTable("vouchers")(Consumed.`with`[VoucherId, Voucher])

    // Predicates for split
    val voucherPresent = (_: OrderId, order: Order) => order.voucherId.nonEmpty
    val voucherNotPresent = (orderId: OrderId, order: Order) => !voucherPresent(orderId, order)

    // Branching for voucher to apply present or not
    val branches = orders
      .split(Named.as("voucher-"))
      .branch(voucherPresent, Branched.as("present"))
      .branch(voucherNotPresent, Branched.as("not-present"))
      .noDefaultBranch()

    // If voucher is present in the order, apply it by joining with vouchers GlobalKTable
    val voucherApplied = branches("voucher-present")
      .join(vouchers)(
        (_: OrderId, o: Order) => o.voucherId,
        (o: Order, v: Voucher) => o.copy(price = o.price * (1 - v.percentage))
      )

    // Merge vouchers branches back
    val merged = branches("voucher-not-present").merge(voucherApplied)

    // Send all the orders to another topic
    merged.to("orders-voucher-applied")(Produced.`with`[OrderId, Order])

    builder.build()
  }
}
