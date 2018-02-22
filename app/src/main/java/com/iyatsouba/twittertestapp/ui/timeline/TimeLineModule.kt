package com.iyatsouba.twittertestapp.ui.timeline

import dagger.Module
import dagger.Provides

@Module
class TimeLineModule {

    @Provides
    fun provideViewModel() = TimeLineViewModel()

}