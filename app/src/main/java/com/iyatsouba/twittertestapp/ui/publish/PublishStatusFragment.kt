package com.iyatsouba.twittertestapp.ui.publish

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import com.iyatsouba.twittertestapp.R
import com.iyatsouba.twittertestapp.databinding.PublishStatusFragmentBinding
import com.iyatsouba.twittertestapp.repository.DataLoadingState
import dagger.android.DaggerFragment
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.publish_status_fragment.*
import javax.inject.Inject

class PublishStatusFragment : DaggerFragment() {

    @Inject lateinit var publishStatusViewModel: PublishStatusViewModel

    private lateinit var dataLoadingDisposable: Disposable

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
                inflater, R.layout.publish_status_fragment, container, false) as PublishStatusFragmentBinding
        binding.viewmodel = publishStatusViewModel
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dataLoadingDisposable = publishStatusViewModel.subscribeOnTwitterPublishProgress(Consumer {
            when(it) {
                DataLoadingState.SUCCESS -> {
                    publishing_progress.visibility = GONE
                    activity.fragmentManager.popBackStack()
                }
                DataLoadingState.ERROR -> {
                    publish.isEnabled = true
                    publishing_progress.visibility = GONE
                    Toast.makeText(activity, getString(R.string.error_publishing_status), Toast.LENGTH_SHORT).show()
                }
                DataLoadingState.IN_PROGRESS -> {
                    publish.isEnabled = false
                    publishing_progress.visibility = VISIBLE
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        dataLoadingDisposable.dispose()
    }
}