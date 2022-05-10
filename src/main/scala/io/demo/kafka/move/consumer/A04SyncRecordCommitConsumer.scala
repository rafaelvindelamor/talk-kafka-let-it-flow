package io.demo.kafka.move.consumer

import org.apache.kafka.clients.consumer.{KafkaConsumer, OffsetAndMetadata}
import org.apache.kafka.common.TopicPartition

import java.time.Duration
import java.util
import java.util.Properties
import scala.jdk.CollectionConverters._

object A04SyncRecordCommitConsumer extends App {

  // https://kafka.apache.org/documentation/#consumerconfigs
  val properties: Properties = {
    val props = new Properties()
    // Mandatory properties
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("group.id", "consumer-example-4")
    // Optional properties
    props.put("auto.offset.reset", "earliest") // https://kafka.apache.org/documentation/#consumerconfigs_auto.offset.reset
    props.put("enable.auto.commit", "false") // https://kafka.apache.org/documentation/#consumerconfigs_enable.auto.commit
    props
  }

  val topics = List("quickstart-events").asJava

  val consumer = new KafkaConsumer[String, String](properties)
  consumer.subscribe(topics)

  while (true) {
    val records = consumer.poll(Duration.ofMillis(100))
    for (record <- records.asScala) {
      println(s"""offset=${record.offset}, key=${record.key}, value=${record.value}""")
      val map = new util.HashMap[TopicPartition, OffsetAndMetadata]()
      val topicPartition = new TopicPartition(record.topic(), record.partition())
      val offsetAndMetadata = new OffsetAndMetadata(record.offset())
      map.put(topicPartition, offsetAndMetadata)
      consumer.commitSync(map)
    }
  }

  consumer.close()
}
