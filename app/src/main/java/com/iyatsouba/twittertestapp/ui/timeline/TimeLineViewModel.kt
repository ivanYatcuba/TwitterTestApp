package com.iyatsouba.twittertestapp.ui.timeline

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.iyatsouba.twittertestapp.repository.TweetRepository
import com.iyatsouba.twittertestapp.twitter.LocalTweetTimeline
import com.iyatsouba.twittertestapp.twitter.TwitterHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class TimeLineViewModel(private val twitterRepository: TweetRepository, private val twitterHelper: TwitterHelper) {

    fun getTweetTimeline(): LocalTweetTimeline {
        return twitterRepository.getTweetTimeline()
    }

    fun getUsername(): String {
        return twitterHelper.getUserName()
    }

    fun subscribeForNetworkChangeState(consumer: Consumer<Boolean>): Disposable {
        return ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer)
    }
}