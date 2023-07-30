package com.example.demo.client

import com.example.demo.config.DynamoDBConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.net.URI

interface DynamoDBClient {
    fun getItem(tableName: String, key: Map<String, AttributeValue>): GetItemResponse
    fun putItem(tableName: String, item: Map<String, AttributeValue>): PutItemResponse
    fun deleteItem(tableName: String, key: Map<String, AttributeValue>): DeleteItemResponse
}

@Component
class DynamoDBClientImpl(
    private val dynamoDBConfig: DynamoDBConfig
) : DynamoDBClient {

    @Value("\${app.isLocal}")
    private var isLocal: Boolean = false

    override fun getItem(tableName: String, key: Map<String, AttributeValue>): GetItemResponse {
        val req = GetItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .build()
        return createClient().getItem(req)
    }

    override fun putItem(tableName: String, item: Map<String, AttributeValue>): PutItemResponse {
        val req = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build()
        return createClient().putItem(req)
    }

    override fun deleteItem(tableName: String, key: Map<String, AttributeValue>): DeleteItemResponse {
        val req = DeleteItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .build()
        return createClient().deleteItem(req)
    }

    fun createClient(): DynamoDbClient {
        return DynamoDbClient.builder()
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        dynamoDBConfig.accessKey, dynamoDBConfig.secretKey
                    )
                )
            ).apply {
                if (isLocal) {
                    this.region(Region.US_EAST_1)
                    this.endpointOverride(URI.create(dynamoDBConfig.url))
                }
            }
            .build()
    }
}