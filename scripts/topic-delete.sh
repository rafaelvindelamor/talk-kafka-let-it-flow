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
