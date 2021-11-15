package io.demo.kafka.streams

import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.kafka.streams.TopologyTestDriver
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.util.Properties

class OrderPromotionEnricherTopologyTest extends AnyWordSpec with Matchers {

  val sut = OrderPromotionEnricherTopology()

  val props = new Properties()
  val testDriver = new TopologyTestDriver(sut, props)

  val testOrdersTopic = testDriver.createInputTopic("orders", new StringSerializer, new StringSerializer)
  val testPromotionsTopic = testDriver.createInputTopic("promotions", new StringSerializer, new StringSerializer)
  val testOrdersPromotionAppliedTopic = testDriver.createOutputTopic("orders-promotion-applied", new StringDeserializer, new StringDeserializer)

  "OrderPromotionEnricherTopology" when {

    "order with promotion is consumed" should {

      "update price if there is a matching promotion" in {
        testPromotionsTopic.pipeInput("PROM50", """{"promotionId":"PROM50","percentage":0.50}""")
        testOrdersTopic.pipeInput(
          "761237653413",
          """{"orderId":761237653413,"userId":761523,"productId":182736,"quantity":3,"price":300,"promotionId":"PROM50"}"""
        )

        val result = testOrdersPromotionAppliedTopic.readKeyValue()
        result.key shouldBe "761237653413"
        result.value shouldBe """{"orderId":761237653413,"userId":761523,"productId":182736,"quantity":3,"price":150.0,"promotionId":"PROM50"}"""
      }
    }

    "order without promotion is consumed" should {

      "pass through" in {
        testOrdersTopic.pipeInput("761237653413", """{"orderId":761237653413,"userId":761523,"productId":182736,"quantity":3,"price":300,"promotionId":""}""")

        val result = testOrdersPromotionAppliedTopic.readKeyValue()
        result.key shouldBe "761237653413"
        result.value shouldBe """{"orderId":761237653413,"userId":761523,"productId":182736,"quantity":3,"price":300.0,"promotionId":""}"""
      }
    }
  }
}
