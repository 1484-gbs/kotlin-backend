package com.example.demo.client

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
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
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

    @Value("\${app.isLocal}")
    private var isLocal: Boolean = false

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun upload(base64: String, folder: String, fileName: String) {
        val bi = Base64.decode(base64)
        ByteArrayInputStream(bi).use {
            val request = PutObjectRequest(
                s3Config.bucket,
                "$folder/$fileName",
                it,
                ObjectMetadata().apply {
                    this.contentLength = bi.size.toLong()
                }
            )
            createClient().putObject(request)
        }
        log.debug("s3 upload success. key: $folder/$fileName")
    }

    override fun delete(folder: String, fileName: String) {
        createClient().deleteObject(
            DeleteObjectRequest(
                s3Config.bucket,
                "$folder/$fileName",
            )
        )
        log.debug("s3 delete success. key: $folder/$fileName")
    }

    override fun getPresignedUrl(folder: String, fileName: String): String? {
        val s3Client = createClient()
        val key = "$folder/$fileName"
        return runCatching {
            s3Client.getObject(s3Config.bucket, key)
        }.fold(
            onSuccess = {
                val expiration = LocalDateTime.now().plusHours(1L)
                val generatePresignedUrlRequest = GeneratePresignedUrlRequest(
                    s3Config.bucket,
                    key,
                ).apply {
                    this.method = HttpMethod.GET
                    this.expiration = Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant())
                }
                s3Client.generatePresignedUrl(generatePresignedUrlRequest)?.toString()
            },
            onFailure = {
                log.debug(it.message)
                null
            }
        )
    }

    private fun createClient(): AmazonS3 {
        return AmazonS3ClientBuilder
            .standard()
            .withCredentials(
                AWSStaticCredentialsProvider(
                    BasicAWSCredentials(s3Config.accessKey, s3Config.secretKey)
                )
            ).apply {
                if (isLocal) {
                    this.withEndpointConfiguration(
                        AwsClientBuilder.EndpointConfiguration(
                            s3Config.url, Regions.US_EAST_1.name
                        )
                    )
                }
            }
            .build()
    }
}