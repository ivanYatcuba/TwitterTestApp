package com.iyatsouba.twittertestapp.ui.publish

import com.iyatsouba.twittertestapp.repository.TweetRepository

class PublishStatusViewModel(private val twitterRepository: TweetRepository) {

    var text: String = "one two thrre four"

    fun publishStatus() {
        twitterRepository.publishTweet(text, "")
    }
}