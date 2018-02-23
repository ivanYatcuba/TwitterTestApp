package com.iyatsouba.twittertestapp.repository

class Response<T>(val status: Status, var data: T?, val error: Throwable?) {

    fun empty(): Response<T> {
        return Response(Status.EMPTY, null, null)
    }

    fun loading(): Response<T> {
        return Response(Status.LOADING, null, null)
    }

    fun success(data: T): Response<T> {
        return Response(Status.SUCCESS, data, null)
    }

    fun error(error: Throwable): Response<T> {
        return Response(Status.ERROR, null, error)
    }

}