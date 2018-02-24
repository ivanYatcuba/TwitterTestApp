package com.iyatsouba.twittertestapp.ui.publish

import android.databinding.BaseObservable
import com.iyatsouba.twittertestapp.repository.TweetRepository

class PublishStatusViewModel(private val twitterRepository: TweetRepository): BaseObservable() {

    var text: String = ""

    fun publishStatus() {
        twitterRepository.publishTweet(text, "")
    }
}