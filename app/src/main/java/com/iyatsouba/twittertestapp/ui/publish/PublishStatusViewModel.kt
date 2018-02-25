package com.iyatsouba.twittertestapp.ui.publish

import com.iyatsouba.twittertestapp.repository.DataLoadingState
import com.iyatsouba.twittertestapp.repository.TweetRepository
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

class PublishStatusViewModel(private val twitterRepository: TweetRepository) {

    private val networkStateRelay = BehaviorRelay.create<DataLoadingState>()
    private val networkStateRelayObservable = networkStateRelay.share()

    var text: String = ""

    fun publishStatus() {
        twitterRepository.publishTweet(text, "", networkStateRelay)
    }

    fun subscribeOnTwitterPublishProgress(consumer: Consumer<DataLoadingState>): Disposable {
        return networkStateRelayObservable.subscribe(consumer)
    }
}