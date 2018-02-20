package com.iyatsouba.twittertestapp.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iyatsouba.twittertestapp.R
import dagger.android.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.login_fragment.*
import javax.inject.Inject

class LoginFragment : DaggerFragment() {

    private val compositeDisposable by lazy { CompositeDisposable() }

    @Inject lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.login_fragment, container, false)!!
    }

    override fun onStart() {
        super.onStart()
        compositeDisposable.add(loginViewModel.showDataFromApi()
                .subscribe({
                    Log.d("DI", it)
                    testText.text = it
                }, {
                    Log.d("DI", it.message)
                }))
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

}