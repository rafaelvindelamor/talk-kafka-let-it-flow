# Kafka: Let It Flow

### Environment
- Java 16
- Scala 2.13
- sbt 1.4.3
- Docker
- Kafka 3.0.0 (https://kafka.apache.org/downloads)
- Confluent Platform (https://www.confluent.io/get-started/)

### Kafka Streams Examples

In order to run the example in `io.demo.kafka.streams.Application`, you need to first start a Kafka broker. One of the easiest ways, as we are going to use ksqlDB later, is by using Confluent Platform. We decided to use `docker-compose` option:

```docker-compose up -d```

You can check the status with:

```docker-compose ps```

To create and populate the topics used in the example, run the following scripts:
- `scripts/topic-creation.sh`
- `scripts/topic-filling.sh`

If you want to see the output in realtime, run also the following script:
- `scripts/topic-consume.sh`

Finally, execute `io.demo.kafka.streams.Application` and you should see some events published and consumed by the previous consumer.

### ksqlDB Examples

We are going to create a `table` in which we are going to have the count by the different `productId`.

First, connect to ksqlDB:

```
docker-compose exec ksqldb-cli ksql http://ksqldb-server:8088
```

Now, create the `stream` from the `orders` topic:

```
CREATE STREAM orders_stream (
    orderId BIGINT,
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
    KAFKA_TOPIC = 'product_counts',
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

Some other useful queries:

```
SHOW streams;

DROP TABLE product_counts;

DROP STREAM orders_stream;
```
