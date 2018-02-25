package com.iyatsouba.twittertestapp.ui.timeline

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.user_timeline_fragment.*
import javax.inject.Inject


class TimeLineFragment : DaggerFragment() {

    @Inject lateinit var timeLineViewModel: TimeLineViewModel

    private var currentDisposables: CompositeDisposable? = null

    private var currentTimeline: LocalTweetTimeline? = null
    private var adapter: TweetTimelineRecyclerViewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.user_timeline_fragment, container, false)!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentDisposables = CompositeDisposable()

        (activity as AppCompatActivity).findViewById<TextView>(R.id.user_name)
                ?.text = timeLineViewModel.getUsername()

        currentTimeline = timeLineViewModel.getTweetTimeline()

        adapter = TweetTimelineRecyclerViewAdapter.Builder(activity)
                .setTimeline(currentTimeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .build()

        user_timeline.layoutManager = LinearLayoutManager(activity)
        user_timeline.adapter = adapter

        refresh_timeline.setOnRefreshListener({
            refresh_timeline.isRefreshing = true
            adapter?.refresh(getRefreshCallback())
        })

        add_new_tweet.setOnClickListener({
            activity.fragmentManager.beginTransaction().replace(R.id.container, PublishStatusFragment()).addToBackStack(null).commit()
        })

    }

    override fun onStart() {
        super.onStart()
        currentDisposables?.add(currentTimeline?.subscribeForLoadingState(Consumer {
            when(it) {
                DataLoadingState.IN_PROGRESS -> {
                    if(!refresh_timeline.isRefreshing) {
                        refresh_timeline.isRefreshing = true
                    }
                }
                DataLoadingState.SUCCESS -> {
                    refresh_timeline.isRefreshing = false
                    showEmptyPlaceholder(adapter?.itemCount == 0)
                }
                DataLoadingState.ERROR -> {
                    refresh_timeline.isRefreshing = false
                    Toast.makeText(activity, getString(R.string.error_loading_tweets), Toast.LENGTH_SHORT).show()
                }
            }
        })!!)

        currentDisposables?.add(timeLineViewModel.subscribeForNetworkChangeState(Consumer {
            if(it) {
                if(!refresh_timeline.isRefreshing) {
                    refresh_timeline.isRefreshing = true
                    adapter?.refresh(getRefreshCallback())
                }
            }
        }))
    }

    override fun onStop() {
        super.onStop()
        currentDisposables?.dispose()
        currentDisposables?.clear()
    }

    private fun showEmptyPlaceholder(show: Boolean) {
        if(show) {
            data_empty_placeholder.visibility = VISIBLE
        } else {
            data_empty_placeholder.visibility = GONE
        }
    }

    private fun getRefreshCallback():  Callback<TimelineResult<Tweet>> {
        return object : Callback<TimelineResult<Tweet>>() {

            override fun success(result: Result<TimelineResult<Tweet>>) {
                refresh_timeline.isRefreshing = false
                showEmptyPlaceholder(adapter?.itemCount == 0)
            }

            override fun failure(exception: TwitterException) {
                refresh_timeline.isRefreshing = false
                Toast.makeText(activity, getString(R.string.error_loading_tweets), Toast.LENGTH_SHORT).show()
            }
        }
    }
}