package com.iyatsouba.twittertestapp.ui.login

import dagger.Module
import dagger.Provides

@Module
class LoginModule {

    @Provides
    fun provideViewModel() = LoginViewModel()

}