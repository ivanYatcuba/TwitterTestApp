package com.iyatsouba.twittertestapp.ui.main

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.iyatsouba.twittertestapp.R
import com.iyatsouba.twittertestapp.twitter.TwitterHelper
import com.iyatsouba.twittertestapp.ui.login.LoginFragment
import com.iyatsouba.twittertestapp.ui.timeline.TimeLineFragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasFragmentInjector
import javax.inject.Inject


class MainActivity : AppCompatActivity(), HasFragmentInjector {

    @Inject lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var twitterHelper: TwitterHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)


        if(twitterHelper.isUserLoggedIn()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, TimeLineFragment()).commit()
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, LoginFragment()).commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = fragmentManager.findFragmentById(R.id.container)
        if(fragment is LoginFragment) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun fragmentInjector(): AndroidInjector<Fragment> {
        return fragmentInjector
    }
}
