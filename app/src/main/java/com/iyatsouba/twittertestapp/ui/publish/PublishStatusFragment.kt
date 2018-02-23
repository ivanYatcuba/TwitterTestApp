package com.iyatsouba.twittertestapp.ui.publish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iyatsouba.twittertestapp.R
import dagger.android.DaggerFragment
import kotlinx.android.synthetic.main.publish_status_fragment.*
import javax.inject.Inject


class PublishStatusFragment : DaggerFragment() {

    @Inject lateinit var publishStatusViewModel: PublishStatusViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.publish_status_fragment, container, false)!!
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        publish.setOnClickListener({
            publishStatusViewModel.publishStatus()
        })
    }
}