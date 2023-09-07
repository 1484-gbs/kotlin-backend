package com.example.demo

import com.example.demo.client.DynamoDBClientImpl
import com.example.demo.type.dynamodb.OneTimeTokenType
import com.example.demo.type.dynamodb.TokenType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
    val ctx = runApplication<DemoApplication>(*args)
    val dynamoDBClient = ctx.getBean(DynamoDBClientImpl::class.java)
    createDynamoDbTables(dynamoDBClient.createClient())
}

private fun createDynamoDbTables(client: DynamoDbClient) {
    val tableNames = client.listTables().tableNames()
    createDynamoDbTable(
        client, tableNames, OneTimeTokenType.TABLE_NAME.value,
        KeySchemaElement.builder()
            .attributeName(OneTimeTokenType.EMPLOYEE_ID.value)
            .keyType(KeyType.HASH)
            .build(),
        AttributeDefinition.builder()
            .attributeName(OneTimeTokenType.EMPLOYEE_ID.value)
            .attributeType(ScalarAttributeType.N)
            .build(),
        OneTimeTokenType.TTL.value
    )

    createDynamoDbTable(
        client, tableNames, TokenType.TABLE_NAME.value,
        KeySchemaElement.builder()
            .attributeName(TokenType.TOKEN_ID.value)
            .keyType(KeyType.HASH)
            .build(),
        AttributeDefinition.builder()
            .attributeName(TokenType.TOKEN_ID.value)
            .attributeType(ScalarAttributeType.S)
            .build(),
        OneTimeTokenType.TTL.value
    )
}

private fun createDynamoDbTable(
    client: DynamoDbClient,
    tableNames: List<String>,
    tableName: String, keySchemaElement: KeySchemaElement,
    attributeDefinition: AttributeDefinition,
    ttl: String = "",
) {
    if (!tableNames.contains(tableName)) {
        client.createTable(
            CreateTableRequest.builder()
                .tableName(tableName)
                .keySchema(keySchemaElement)
                .attributeDefinitions(attributeDefinition)
                .provisionedThroughput(
                    ProvisionedThroughput.builder()
                        .readCapacityUnits(1L)
                        .writeCapacityUnits(1L)
                        .build()
                )
                .build()
        )
        if (ttl.isNotEmpty())
            client.updateTimeToLive(
                UpdateTimeToLiveRequest.builder()
                    .tableName(tableName)
                    .timeToLiveSpecification(
                        TimeToLiveSpecification.builder()
                            .attributeName(ttl)
                            .enabled(true)
                            .build()
                    ).build()
            )
    }
}
