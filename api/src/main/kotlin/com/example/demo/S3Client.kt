package com.example.demo

import com.amazonaws.HttpMethod
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.util.Base64
import com.example.demo.config.S3Config
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

interface S3Client {
    fun upload(base64: String, folder: String, fileName: String)
    fun delete(folder: String, fileName: String)
    fun getPresignedUrl(folder: String, fileName: String): String?
}

@Component
class S3ClientImpl(
    private val s3Config: S3Config
) : S3Client {

    override fun upload(base64: String, folder: String, fileName: String) {
        val bi = Base64.decode(base64)
        val metadata = ObjectMetadata()
        metadata.contentLength = bi.size.toLong()
        ByteArrayInputStream(bi).use {
            val request = PutObjectRequest(
                s3Config.bucket,
                "$folder/$fileName",
                it,
                metadata
            )
            createClient().putObject(request)
        }
    }

    override fun delete(folder: String, fileName: String) {
        createClient().deleteObject(
            DeleteObjectRequest(
                s3Config.bucket,
                "$folder/$fileName",
            )
        )
    }

    override fun getPresignedUrl(folder: String, fileName: String): String? {
        val s3Client = createClient()
        val key = "$folder/$fileName"
        return runCatching {
            s3Client.getObject(s3Config.bucket, key)
        }.fold (
            onSuccess = {
                val expiration = LocalDateTime.now().plusHours(1L)
                val generatePresignedUrlRequest = GeneratePresignedUrlRequest(
                    s3Config.bucket,
                    key,
                )
                generatePresignedUrlRequest.method = HttpMethod.GET
                generatePresignedUrlRequest.expiration = Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant())
                s3Client.generatePresignedUrl(generatePresignedUrlRequest)?.toString()
            },
            onFailure = {
                null
            }
        )
    }

    private fun createClient(): AmazonS3 {
        return AmazonS3ClientBuilder
            .standard()
            .withEndpointConfiguration(
                AwsClientBuilder.EndpointConfiguration(
                    s3Config.url, Regions.US_EAST_1.name
                )
            )
            .withCredentials(
                AWSStaticCredentialsProvider(
                    BasicAWSCredentials(s3Config.accessKey, s3Config.secretKey)
                )
            )
            .build()
    }
}