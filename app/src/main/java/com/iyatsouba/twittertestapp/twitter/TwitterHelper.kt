package com.iyatsouba.twittertestapp.twitter

import android.content.Context
import android.util.Log
import com.twitter.sdk.android.core.*
import okhttp3.OkHttpClient

class TwitterHelper {

    private var twitterSession: TwitterSession? = null
    private var apiClient: TwitterApiClient? = null

    fun initializeTwitter(context: Context) {
        val config = TwitterConfig.Builder(context)
                .logger(DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(TwitterAuthConfig("Nt7jhvHegQUY1qEWVJ1rfQfAK",
                        "fRcxj6Haw9ahUKczAe4BtKMdQU5FSOIo80orLgFikNIMmySi2S"))
                .debug(true)
                .build()
        Twitter.initialize(config)

        val activeSession = TwitterCore.getInstance().sessionManager.activeSession

        if (activeSession != null) {
            setCurrentActiveSession(activeSession)
        } else {
            val customApiClient = TwitterApiClient(buildCustomOkHttpClient())
            TwitterCore.getInstance().addGuestApiClient(customApiClient)
            this.apiClient = customApiClient
        }
    }

    fun setCurrentActiveSession(twitterSession: TwitterSession) {
        this.twitterSession = twitterSession
        this.apiClient = TwitterApiClient(twitterSession, buildCustomOkHttpClient())
        TwitterCore.getInstance().addApiClient(twitterSession, apiClient)
    }

    fun isUserLoggedIn(): Boolean {
        return twitterSession != null
    }

    fun getApiClient(): TwitterApiClient? {
        return apiClient
    }

    fun getCurrentActiveSession(): TwitterSession? {
        return twitterSession
    }

    fun getUserName(): String {
        return twitterSession?.userName ?: ""
    }

    fun getUserId(): Long {
        return twitterSession?.userId ?: 0
    }


    private fun buildCustomOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

}