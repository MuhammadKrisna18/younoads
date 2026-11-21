package com.example.yoouutt.playback

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.TransferListener
import androidx.media3.datasource.okhttp.OkHttpDataSource
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import java.io.IOException
import java.util.concurrent.TimeUnit

@OptIn(UnstableApi::class)
class ResilientHttpDataSourceFactory(
    userAgent: String = DEFAULT_USER_AGENT
) : DataSource.Factory {

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectionPool(ConnectionPool(8, 5, TimeUnit.MINUTES))
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .retryOnConnectionFailure(true)
        .build()

    private val primaryFactory: HttpDataSource.Factory = OkHttpDataSource.Factory(okHttpClient)
        .setUserAgent(userAgent)

    private val fallbackFactory: HttpDataSource.Factory = DefaultHttpDataSource.Factory()
        .setUserAgent(userAgent)
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(15000)
        .setReadTimeoutMs(20000)

    override fun createDataSource(): DataSource {
        return ResilientHttpDataSource(
            primarySource = primaryFactory.createDataSource(),
            fallbackSource = fallbackFactory.createDataSource()
        )
    }

    companion object {
        const val DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    }
}

@OptIn(UnstableApi::class)
class ResilientHttpDataSource(
    private val primarySource: DataSource,
    private val fallbackSource: DataSource
) : DataSource {

    private var activeSource: DataSource = primarySource
    private var currentDataSpec: DataSpec? = null
    private var currentPosition: Long = 0L

    override fun addTransferListener(transferListener: TransferListener) {
        primarySource.addTransferListener(transferListener)
        fallbackSource.addTransferListener(transferListener)
    }

    override fun open(dataSpec: DataSpec): Long {
        currentDataSpec = dataSpec
        currentPosition = dataSpec.position
        return try {
            activeSource = primarySource
            primarySource.open(dataSpec)
        } catch (_: Exception) {
            activeSource = fallbackSource
            fallbackSource.open(dataSpec)
        }
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        return try {
            val bytesRead = activeSource.read(buffer, offset, length)
            if (bytesRead > 0) {
                currentPosition += bytesRead
            }
            bytesRead
        } catch (ex: Exception) {
            if (activeSource !== fallbackSource) {
                val spec = currentDataSpec ?: throw (ex as? IOException ?: IOException(ex))
                try {
                    primarySource.close()
                } catch (_: Exception) {
                }
                activeSource = fallbackSource
                val remainingSpec = spec.subrange(currentPosition - spec.position)
                fallbackSource.open(remainingSpec)
                val bytesRead = fallbackSource.read(buffer, offset, length)
                if (bytesRead > 0) {
                    currentPosition += bytesRead
                }
                bytesRead
            } else {
                throw ex as? IOException ?: IOException(ex)
            }
        }
    }

    override fun getUri(): Uri? = activeSource.uri

    override fun getResponseHeaders(): Map<String, List<String>> = activeSource.responseHeaders

    override fun close() {
        try {
            activeSource.close()
        } finally {
            currentDataSpec = null
        }
    }
}
