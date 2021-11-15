package io.demo.kafka.streams

import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.streams.TopologyTestDriver
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.util.Properties

class OrderVoucherEnricherTopologyTest extends AnyWordSpec with Matchers {

  val sut = OrderVoucherEnricherTopology()

  val props = new Properties()
  val testDriver = new TopologyTestDriver(sut, props)

  val testOrdersTopic = testDriver.createInputTopic("orders", new StringSerializer, new StringSerializer)
  val testVouchersTopic = testDriver.createInputTopic("vouchers", new StringSerializer, new StringSerializer)
  val testOrdersVoucherAppliedTopic = testDriver.createOutputTopic("orders-voucher-applied", new StringDeserializer, new StringDeserializer)

  "OrderVoucherEnricherTopology" when {

    "order with voucher is consumed" should {

      "update price if there is a matching voucher" in {
        testVouchersTopic.pipeInput("PROM50", """{"id":"PROM50","percentage":0.50}""")
        testOrdersTopic.pipeInput(
          "761237653413",
          """{"id":761237653413,"userId":761523,"productId":182736,"quantity":3,"price":300.0,"voucherId":"PROM50"}"""
        )

        val result = testOrdersVoucherAppliedTopic.readKeyValue()
        result.key shouldBe "761237653413"
        result.value shouldBe """{"id":761237653413,"userId":761523,"productId":182736,"quantity":3,"price":150.0,"voucherId":"PROM50"}"""
      }
    }

    "order without voucher is consumed" should {

      "pass through" in {
        testOrdersTopic.pipeInput("761237653413", """{"id":761237653413,"userId":761523,"productId":182736,"quantity":3,"price":300.0,"voucherId":""}""")

        val result = testOrdersVoucherAppliedTopic.readKeyValue()
        result.key shouldBe "761237653413"
        result.value shouldBe """{"id":761237653413,"userId":761523,"productId":182736,"quantity":3,"price":300.0,"voucherId":""}"""
      }
    }
  }
}
