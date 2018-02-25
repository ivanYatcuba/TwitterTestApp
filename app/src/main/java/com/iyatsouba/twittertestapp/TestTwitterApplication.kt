package com.iyatsouba.twittertestapp

import android.app.Activity
import android.app.Application
import com.iyatsouba.twittertestapp.di.DaggerAppComponent
import com.iyatsouba.twittertestapp.twitter.TwitterHelper
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class TestTwitterApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var twitterHelper: TwitterHelper

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)

        twitterHelper.initializeTwitter(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityDispatchingAndroidInjector

}