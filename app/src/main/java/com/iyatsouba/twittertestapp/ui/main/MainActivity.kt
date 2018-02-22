package com.iyatsouba.twittertestapp.ui.main

import android.content.Intent
import android.os.Bundle
import com.iyatsouba.twittertestapp.R
import com.iyatsouba.twittertestapp.twitter.TwitterHelper
import com.iyatsouba.twittertestapp.ui.login.LoginFragment
import com.iyatsouba.twittertestapp.ui.timeline.TimeLineFragment
import dagger.android.DaggerActivity
import javax.inject.Inject


class MainActivity : DaggerActivity() {

    @Inject lateinit var twitterHelper: TwitterHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if(twitterHelper.isUserLoggedIn()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, TimeLineFragment())
                    .addToBackStack(null).commit()
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, LoginFragment())
                    .addToBackStack(null).commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = fragmentManager.findFragmentById(R.id.container)
        fragment?.onActivityResult(requestCode, resultCode, data)
    }
}
