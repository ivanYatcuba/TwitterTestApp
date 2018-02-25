package com.iyatsouba.twittertestapp.di

import android.app.Application
import com.iyatsouba.twittertestapp.TestTwitterApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, ActivityBuilder::class,
                                                                        FragmentBuilder::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance fun application(app: Application): Builder
        fun build(): AppComponent
    }

    fun inject(app: TestTwitterApplication)
}