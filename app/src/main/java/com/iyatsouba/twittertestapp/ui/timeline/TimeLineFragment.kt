package com.iyatsouba.twittertestapp.ui.timeline

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iyatsouba.twittertestapp.R
import com.iyatsouba.twittertestapp.ui.publish.PublishStatusFragment
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.TimelineResult
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter
import dagger.android.DaggerFragment
import kotlinx.android.synthetic.main.user_timeline_fragment.*
import javax.inject.Inject


class TimeLineFragment : DaggerFragment() {

    @Inject lateinit var timeLineViewModel: TimeLineViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.user_timeline_fragment, container, false)!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user_timeline.layoutManager = LinearLayoutManager(activity)


        val adapter = TweetTimelineRecyclerViewAdapter.Builder(activity)
                .setTimeline(timeLineViewModel.getTweetTimeline())
                .setViewStyle(R.style.tw__TweetDarkWithActionsStyle)
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
                    // Toast or some other action
                }
            })
        })
    }
}