# kafka-change-data-capture
Spring Cloud Stream Kafka projects that show how to use CDC with Kafka Connect

Detailed information about the JDBC Connector in this blog post: https://www.confluent.io/blog/kafka-connect-deep-dive-jdbc-source-connector

1. Start PostgreSQL

```
docker run -p 5432:5432 --name some-postgresql -e POSTGRES_PASSWORD=mysecretpassword -d postgres
```

2. Download the Confluent Platform and start it:

```
./confluent start
```

3. Register the JDBC connector for the postgre database
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
    "transforms":"AddNamespace", 
    "transforms.AddNamespace.type":"org.apache.kafka.connect.transforms.SetSchemaMetadata$Value",
    "transforms.AddNamespace.schema.name": "com.codependent.cdc.account.Movement"
  }
}'
```

The topic name could be customized using a transformer, e.g.:

```
"transforms":"dropTopicPrefix",
    "transforms.dropTopicPrefix.type":"org.apache.kafka.connect.transforms.RegexRouter",
    "transforms.dropTopicPrefix.regex":"test-postgresql-jdbc-(.*)",
    "transforms.dropTopicPrefix.replacement":"$1"
``` 

4. Start an avro console consumer to check the migrated messages:
```
./kafka-avro-console-consumer --bootstrap-server localhost:9092 --topic test-postgresql-jdbc-account_entity --from-beginning
./kafka-avro-console-consumer --bootstrap-server localhost:9092 --topic test-postgresql-jdbc-movement_entity --from-beginning
```


5. Create two accounts and a transfer between them
```
curl -X POST http://localhost:8080/accounts -H "content-type: application/json" -d '{"ownerId": "1234X", "ownerName": "John Doe", "funds": 2000.00}'
curl -X POST http://localhost:8080/accounts -H "content-type: application/json" -d '{"ownerId": "5555X", "ownerName": "Ann Mary", "funds": 2000.00}'
curl -X PUT http://localhost:8080/accounts/transfers -H "content-type: application/json" -d '{"sourceAccountId": 1, "destinationAccountId": 2, "ammount": 500}'
```
