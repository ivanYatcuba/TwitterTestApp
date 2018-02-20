package com.iyatsouba.twittertestapp.ui.login

import com.iyatsouba.twittertestapp.repository.UserRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginViewModel(private val userRepository: UserRepository) {

    fun showDataFromApi(): Single<String> = Single.just(userRepository.getName())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

}