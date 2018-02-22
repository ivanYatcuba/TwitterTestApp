package com.iyatsouba.twittertestapp.ui.feed

import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iyatsouba.twittertestapp.R
import com.twitter.sdk.android.core.TwitterCore

class FeedFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if(TwitterCore.getInstance().sessionManager.activeSession != null) {
            Log.d("TWITTER", "Session is not null")
        }
        return inflater?.inflate(R.layout.feed_fragment, container, false)!!
    }

}