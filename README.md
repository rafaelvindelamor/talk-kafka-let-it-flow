# Kafka: Let It Flow

### Environment Versions
- Java 17 LTS
- Scala 2.13.8
- sbt 1.6.2
- docker 20.10.14
- Confluent Platform (https://www.confluent.io/get-started/) (https://docs.confluent.io/platform/current/quickstart/ce-docker-quickstart.html)
- Kafka 3.1.0 (https://kafka.apache.org/downloads)

### Kafka Environment

Install Kafka CLI by running `brew install kafka`.

To run all the examples, you need to initialize our Kafka environment first. In order to do so, one of the easiest ways is using Confluent Platform.

Download [Confluent Platform](https://docs.confluent.io/platform/current/quickstart/ce-docker-quickstart.html#step-1-download-and-start-cp), and then run the following command:

```
docker compose up -d
```

You can check the status with:

```
docker compose ps
```

Once everything is up, you are ready to go! We can go to http://localhost:9021/clusters and check that your cluster is healthy.

To do a clean restart, perform the following command:

```
docker compose down
```

### Kafka Move APIs Examples

#### Producer

You can run the following examples and play with different configuration options:
- `io.demo.kafka.move.producer.A01BasicMandatoryConfigProducer`
- `io.demo.kafka.move.producer.A02BasicOptionalConfigProducer`
- `io.demo.kafka.move.producer.A03FireAndForgetProducer`
- `io.demo.kafka.move.producer.A04SyncProducer`
- `io.demo.kafka.move.producer.A05AsyncProducer`

#### Consumer

You can run the following examples and play with different configuration options:
- `io.demo.kafka.move.consumer.A01BasicMandatoryConfigConsumer`
- `io.demo.kafka.move.consumer.A02BasicOptionalConfigConsumer`
- `io.demo.kafka.move.consumer.A03SyncPollCommitConsumer`
- `io.demo.kafka.move.consumer.A04SyncRecordCommitConsumer`
- `io.demo.kafka.move.consumer.A05AsyncRecordCommitConsumer`

### Kafka Streams Examples

In order to run the example in `io.demo.kafka.streams.Application`, you need to first start the Kafka environment. 

To create and populate the topics used in the example, let's move to `scripts` folder and run the following scripts:
- `./topic-creation.sh`
- `./topic-filling.sh`

If you want to see the output in realtime, run also the following script:
- `./topic-consume.sh`

Finally, execute `io.demo.kafka.streams.Application` and you should see some events published and consumed by the previous consumer.

Also, we are printing the `topology` in the `console`. If you want to visualize it, you can use this awesome tool: https://zz85.github.io/kafka-streams-viz/

### ksqlDB Examples

We are going to create a `table` in which we are going to have the count by the different `productId`.

First, connect to ksqlDB:

```
docker compose exec ksqldb-cli ksql http://ksqldb-server:8088
```

Configure `auto.offset.reset`:

```
SET 'auto.offset.reset'='earliest';
```

Now, create the `stream` from the `orders` topic:

```
CREATE STREAM orders_stream (
    id BIGINT,
    productId BIGINT
) WITH (
    KAFKA_TOPIC='orders',
    VALUE_FORMAT='JSON'
);
```

To visualize the data:

```
SELECT * FROM orders_stream EMIT CHANGES;
```

Then, create the table with the aggregation:

```
CREATE TABLE product_counts
WITH (
    KAFKA_TOPIC = 'product-counts',
    VALUE_FORMAT = 'JSON',
    PARTITIONS = 1
) AS SELECT
        productId,
        COUNT(*) AS products_count
    FROM
        orders_stream
    GROUP BY
        productId
    EMIT CHANGES;
```

To visualize the data:

```
SELECT * FROM product_counts EMIT CHANGES;
```

Now we can run again `./topic-filling.sh` in a different terminal and see how PRODUCT_COUNT column changes in real time.

Some other useful queries:

```
SHOW streams;

SHOW tables;

DROP TABLE product_counts;

DROP STREAM orders_stream;
```

### Troubleshooting

We have found that sometimes we need to add more memory to our docker local in order to keep all containers of Confluent Platform running, please increase memory if some containers are exited after 
starting the platform.

### References

#### Books

- [Mastering Kafka Streams and ksqlDB](https://www.confluent.io/resources/ebook/mastering-kafka-streams-and-ksqldb/)
- [Kafka: The Definitive Guide](https://www.confluent.io/resources/kafka-the-definitive-guide/)
- [Designing Event-Driven Systems](https://www.confluent.io/designing-event-driven-systems/)

#### Links

- [Consume From Closest Replica](https://cwiki.apache.org/confluence/display/KAFKA/KIP-392%3A+Allow+consumers+to+fetch+from+closest+replica)
- [Exactly-Once Semantics](https://www.confluent.io/blog/exactly-once-semantics-are-possible-heres-how-apache-kafka-does-it)
- [KRaft](https://developer.confluent.io/learn/kraft)
- [Cooperative Rebalancing](https://www.confluent.io/blog/cooperative-rebalancing-in-kafka-streams-consumer-ksqldb)
- [Kafka Configuration](https://kafka.apache.org/documentation/#configuration)
