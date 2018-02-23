package com.iyatsouba.twittertestapp.di

import com.iyatsouba.twittertestapp.ui.login.LoginFragment
import com.iyatsouba.twittertestapp.ui.login.LoginModule
import com.iyatsouba.twittertestapp.ui.timeline.TimeLineFragment
import com.iyatsouba.twittertestapp.ui.timeline.TimeLineModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilder {

    @ContributesAndroidInjector(modules = [(LoginModule::class)])
    abstract fun bindLogInFrgamnet(): LoginFragment

    @ContributesAndroidInjector(modules = [(TimeLineModule::class)])
    abstract fun bindTimeLineFrgamnet(): TimeLineFragment
}