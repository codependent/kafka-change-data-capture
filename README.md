# kafka-change-data-capture
Spring Cloud Stream Kafka projects that show how to use CDC with Kafka Connect

Detailed information about the JDBC Connector can be found in this blog post: https://www.confluent.io/blog/kafka-connect-deep-dive-jdbc-source-connector

1. Download the Confluent Platform:

```
git clone https://github.com/confluentinc/cp-docker-images
```

2. Configure transactional support in development mode (1 broker) in `examples/cp-all-in-one/docker-compose.yml`:

* Necessary for transactional producers:

```
KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
```

* Necessary for exactly_once KStreams:

```
KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
```

3. Start the Confluent Platform:

```
cd cp-docker-images
git checkout 5.2.2-post
cd examples/cp-all-in-one/
docker-compose up -d --build
```

4. Register the JDBC connector for the postgre database
```
curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" -d '
{
  "name": "movement-jdbc-source",
  "config": {
    "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
    "tasks.max": "1",
    "connection.url": "jdbc:postgresql://localhost:5432/postgres",
    "connection.user": "postgres",
    "connection.password": "mysecretpassword",
    "mode": "incrementing",
    "incrementing.column.name": "id",
    "topic.prefix": "test-postgresql-jdbc-",
    "name": "movement-jdbc-source",
    "table.whitelist": "movement",
    "transforms":"AddNamespace,ValueToKey", 
    "transforms.AddNamespace.type":"org.apache.kafka.connect.transforms.SetSchemaMetadata$Value",
    "transforms.AddNamespace.schema.name": "com.codependent.cdc.account.Movement",
    "transforms.ValueToKey.type":"org.apache.kafka.connect.transforms.ValueToKey",
    "transforms.ValueToKey.fields": "transaction_id"
  }
}'
```

NOTE: localhost should be replaced by the machine's IP.

The topic name could be customized using a transformer, e.g.:

```
"transforms":"dropTopicPrefix",
    "transforms.dropTopicPrefix.type":"org.apache.kafka.connect.transforms.RegexRouter",
    "transforms.dropTopicPrefix.regex":"test-postgresql-jdbc-(.*)",
    "transforms.dropTopicPrefix.replacement":"$1"
``` 

5. Start an avro console consumer to check the migrated messages:
```
./kafka-avro-console-consumer --bootstrap-server localhost:9092 --topic test-postgresql-jdbc-movement --from-beginning
./kafka-avro-console-consumer --bootstrap-server localhost:9092 --topic account --from-beginning
./kafka-avro-console-consumer --bootstrap-server localhost:9092 --topic fraudulent-transfer --from-beginning
```


6. Create two accounts and a transfer between them
```
curl -X POST http://localhost:8080/accounts -H "content-type: application/json" -d '{"ownerId": "1234X", "ownerName": "John Doe", "funds": 2000.00}'
curl -X POST http://localhost:8080/accounts -H "content-type: application/json" -d '{"ownerId": "5555X", "ownerName": "Ann Mary", "funds": 2000.00}'
curl -X PUT http://localhost:8080/accounts/transfers -H "content-type: application/json" -d '{"sourceAccountId": 1, "destinationAccountId": 2, "ammount": 500}'
```
