package com.iyatsouba.twittertestapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.iyatsouba.twittertestapp.R
import com.iyatsouba.twittertestapp.twitter.TwitterHelper
import com.iyatsouba.twittertestapp.ui.timeline.TimeLineFragment
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import dagger.android.DaggerFragment
import kotlinx.android.synthetic.main.login_fragment.*
import javax.inject.Inject

class LoginFragment : DaggerFragment() {

    @Inject lateinit var twitterHelper: TwitterHelper

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.login_fragment, container, false)!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        login_button.callback = object : Callback<TwitterSession>() {

            override fun success(result: Result<TwitterSession>) {
                twitterHelper.setCurrentActiveSession(result.data)
                activity.fragmentManager.beginTransaction().replace(R.id.container, TimeLineFragment()).addToBackStack(null).commit()
            }

            override fun failure(exception: TwitterException) {
                Toast.makeText(activity, "Fail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        login_button.onActivityResult(requestCode, resultCode, data)
    }
}