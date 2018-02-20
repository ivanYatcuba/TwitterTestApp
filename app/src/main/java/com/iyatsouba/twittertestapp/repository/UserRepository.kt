package com.iyatsouba.twittertestapp.repository

import javax.inject.Inject

class UserRepository @Inject constructor () {

    fun getName(): String {
        return "Sucess DI!"
    }

}