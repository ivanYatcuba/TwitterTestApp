package com.iyatsouba.twittertestapp.di

import com.iyatsouba.twittertestapp.ui.login.LoginFragment
import com.iyatsouba.twittertestapp.ui.login.LoginModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilder {

    @ContributesAndroidInjector(modules = [(LoginModule::class)])
    abstract fun bindLogInFrgamnet(): LoginFragment
}