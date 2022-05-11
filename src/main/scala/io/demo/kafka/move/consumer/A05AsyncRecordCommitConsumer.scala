package io.demo.kafka.move.consumer

import org.apache.kafka.clients.consumer.{KafkaConsumer, OffsetAndMetadata, OffsetCommitCallback}
import org.apache.kafka.common.TopicPartition

import java.time.Duration
import java.util
import java.util.Properties
import scala.jdk.CollectionConverters._

object A05AsyncRecordCommitConsumer extends App {

  // https://kafka.apache.org/documentation/#consumerconfigs
  val properties: Properties = {
    val props = new Properties()
    // Mandatory properties
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("group.id", "consumer-example-5")
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
      val callback = new OffsetCommitCallback {
        override def onComplete(offsets: util.Map[TopicPartition, OffsetAndMetadata], exception: Exception): Unit = {
          println(offsets.toString)
        }
      }
      consumer.commitAsync(map, callback)
    }
  }

  consumer.close()
}
