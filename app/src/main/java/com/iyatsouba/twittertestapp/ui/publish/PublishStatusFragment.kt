package com.iyatsouba.twittertestapp.ui.publish

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iyatsouba.twittertestapp.R
import com.iyatsouba.twittertestapp.databinding.PublishStatusFragmentBinding
import dagger.android.DaggerFragment
import javax.inject.Inject

class PublishStatusFragment : DaggerFragment() {

    @Inject lateinit var publishStatusViewModel: PublishStatusViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
                inflater, R.layout.publish_status_fragment, container, false) as PublishStatusFragmentBinding
        binding.viewmodel = publishStatusViewModel
        return binding.root
    }
}