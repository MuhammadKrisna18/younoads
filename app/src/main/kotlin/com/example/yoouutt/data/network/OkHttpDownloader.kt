package com.example.yoouutt.data.network

import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import java.util.concurrent.TimeUnit

class OkHttpDownloader(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
) : Downloader() {

    override fun execute(request: Request): Response {
        val httpMethod = request.httpMethod()
        val url = request.url()
        val headers = request.headers()
        val dataToSend = request.dataToSend()

        val requestBuilder = okhttp3.Request.Builder().url(url)

        headers.forEach { (name, values) ->
            values.forEach { value ->
                requestBuilder.addHeader(name, value)
            }
        }

        val requestBody = dataToSend?.toRequestBody()

        when (httpMethod.uppercase()) {
            "GET" -> requestBuilder.get()
            "HEAD" -> requestBuilder.head()
            "POST" -> requestBuilder.post(requestBody ?: ByteArray(0).toRequestBody())
            else -> requestBuilder.method(httpMethod, requestBody)
        }

        client.newCall(requestBuilder.build()).execute().use { response ->
            val responseCode = response.code
            val responseMessage = response.message
            val responseHeaders = response.headers.toMultimap()
            val responseBody = response.body?.string().orEmpty()
            val latestUrl = response.request.url.toString()

            return Response(responseCode, responseMessage, responseHeaders, responseBody, latestUrl)
        }
    }
}

