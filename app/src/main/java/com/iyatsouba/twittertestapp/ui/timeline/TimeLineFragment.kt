package com.iyatsouba.twittertestapp.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iyatsouba.twittertestapp.R
import dagger.android.DaggerFragment

class TimeLineFragment : DaggerFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.user_timeline_fragment, container, false)!!
    }

}