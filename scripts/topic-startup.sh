kafka-topics \
  --bootstrap-server localhost:9092 \
  --delete \
  --topic orders

kafka-topics \
  --bootstrap-server localhost:9092 \
  --delete \
  --topic vouchers

kafka-topics \
  --bootstrap-server localhost:9092 \
  --delete \
  --topic orders-voucher-applied

kafka-topics \
  --bootstrap-server localhost:9092 \
  --delete \
  --topic product-counts

kafka-topics \
  --bootstrap-server localhost:9092 \
  --topic orders \
  --replication-factor 1 \
  --partitions 3 \
  --create

kafka-topics \
  --bootstrap-server localhost:9092 \
  --topic vouchers \
  --replication-factor 1 \
  --partitions 3 \
  --create \
  --config "cleanup.policy=compact"

kafka-topics \
  --bootstrap-server localhost:9092 \
  --topic orders-voucher-applied \
  --replication-factor 1 \
  --partitions 3 \
  --create

kafka-console-producer \
  --broker-list localhost:9092 \
  --topic orders < ../data/orders.txt \
  --property parse.key=true \
  --property key.separator=,

kafka-console-producer \
  --broker-list localhost:9092 \
  --topic vouchers < ../data/vouchers.txt \
  --property parse.key=true \
  --property key.separator=,

kafka-console-consumer \
    --bootstrap-server localhost:9092 \
    --topic orders-voucher-applied \
    --from-beginning
