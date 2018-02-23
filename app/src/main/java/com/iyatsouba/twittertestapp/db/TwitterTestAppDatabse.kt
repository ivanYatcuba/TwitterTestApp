package com.iyatsouba.twittertestapp.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.iyatsouba.twittertestapp.db.dao.LocalTweetDao
import com.iyatsouba.twittertestapp.db.model.LocalTweet

@Database(entities = [LocalTweet::class], version = 1)
abstract class TwitterTestAppDatabse: RoomDatabase() {

    abstract fun localTweetDao(): LocalTweetDao

}