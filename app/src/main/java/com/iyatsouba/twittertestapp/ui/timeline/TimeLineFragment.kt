package com.iyatsouba.twittertestapp.ui.timeline

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.iyatsouba.twittertestapp.R
import com.iyatsouba.twittertestapp.repository.DataLoadingState
import com.iyatsouba.twittertestapp.twitter.LocalTweetTimeline
import com.iyatsouba.twittertestapp.ui.publish.PublishStatusFragment
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.TimelineResult
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter
import dagger.android.DaggerFragment
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.user_timeline_fragment.*
import javax.inject.Inject


class TimeLineFragment : DaggerFragment() {

    @Inject lateinit var timeLineViewModel: TimeLineViewModel

    private var disposableDataLoadingState: Disposable? = null
    private var currentTimeline: LocalTweetTimeline? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.user_timeline_fragment, container, false)!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).findViewById<TextView>(R.id.user_name)
                ?.text = timeLineViewModel.getUsername()

        currentTimeline = timeLineViewModel.getTweetTimeline()
        user_timeline.layoutManager = LinearLayoutManager(activity)
        val adapter = TweetTimelineRecyclerViewAdapter.Builder(activity)
                .setTimeline(currentTimeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .build()

        user_timeline.adapter = adapter

        add_new_tweet.setOnClickListener({
            activity.fragmentManager.beginTransaction().replace(R.id.container, PublishStatusFragment()).addToBackStack(null).commit()
        })

        refresh_timeline.setOnRefreshListener({
            refresh_timeline.isRefreshing = true
            adapter.refresh(object : Callback<TimelineResult<Tweet>>() {

                override fun success(result: Result<TimelineResult<Tweet>>) {
                    refresh_timeline.isRefreshing = false
                }

                override fun failure(exception: TwitterException) {
                    Toast.makeText(activity, "Error publishing status!", Toast.LENGTH_SHORT).show()
                }
            })
        })
    }

    override fun onStart() {
        super.onStart()
        disposableDataLoadingState = currentTimeline?.subscribeForLoadingState(Consumer {
            when(it) {
                DataLoadingState.IN_PROGRESS -> { refresh_timeline.isRefreshing = true }
                DataLoadingState.SUCCESS -> { refresh_timeline.isRefreshing = false }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        disposableDataLoadingState?.dispose()
    }
}