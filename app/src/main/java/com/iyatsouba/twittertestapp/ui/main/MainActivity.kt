package com.iyatsouba.twittertestapp.ui.main

import android.content.Intent
import android.os.Bundle
import com.iyatsouba.twittertestapp.R
import com.iyatsouba.twittertestapp.ui.login.LoginFragment
import dagger.android.DaggerActivity


class MainActivity : DaggerActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val login = LoginFragment()
        fragmentManager.beginTransaction().replace(R.id.container, login).addToBackStack(null).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = fragmentManager.findFragmentById(R.id.container)
        fragment?.onActivityResult(requestCode, resultCode, data)
    }
}
