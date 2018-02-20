package com.iyatsouba.twittertestapp.ui.login

import com.iyatsouba.twittertestapp.repository.UserRepository
import dagger.Module
import dagger.Provides

@Module
class LoginModule {

    @Provides
    fun provideViewModel(userRepository: UserRepository) = LoginViewModel(userRepository)

}