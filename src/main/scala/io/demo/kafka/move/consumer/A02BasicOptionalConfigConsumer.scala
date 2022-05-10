package io.demo.kafka.move.consumer

import org.apache.kafka.clients.consumer.KafkaConsumer

import java.time.Duration
import java.util.Properties
import scala.jdk.CollectionConverters._

object A02BasicOptionalConfigConsumer extends App {

  // https://kafka.apache.org/documentation/#consumerconfigs
  val properties: Properties = {
    val props = new Properties()
    // Mandatory properties
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("group.id", "consumer-example-2")
    // Optional properties
    props.put("heartbeat.interval.ms", "3000") // https://kafka.apache.org/documentation/#consumerconfigs_heartbeat.interval.ms
    props.put("auto.offset.reset", "earliest") // https://kafka.apache.org/documentation/#consumerconfigs_auto.offset.reset
    props.put("enable.auto.commit", "true") // https://kafka.apache.org/documentation/#consumerconfigs_enable.auto.commit
    props.put("auto.commit.interval.ms", "500") // https://kafka.apache.org/documentation/#consumerconfigs_auto.commit.interval.ms
    props.put("max.poll.interval.ms", "30000") // https://kafka.apache.org/documentation/#consumerconfigs_max.poll.interval.ms
    props.put("partition.assignment.strategy", "org.apache.kafka.clients.consumer.RangeAssignor") // https://kafka.apache.org/documentation/#consumerconfigs_partition.assignment.strategy
    props
  }

  val topics = List("quickstart-events").asJava

  val consumer = new KafkaConsumer[String, String](properties)
  consumer.subscribe(topics)

  while (true) {
    val records = consumer.poll(Duration.ofMillis(100))
    for (record <- records.asScala) {
      println(s"""offset=${record.offset}, key=${record.key}, value=${record.value}""")
    }
  }

  consumer.close()
}
