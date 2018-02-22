package com.iyatsouba.twittertestapp.db.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.iyatsouba.twittertestapp.db.model.LocalTweet

@Dao
interface LocalTweetDao {

    @Query("select * from tweets where userId = :userId")
    fun findTweetsByUserId(userId: Long): List<LocalTweet>

    @Insert(onConflict = REPLACE)
    fun insertTweet(tweet: LocalTweet)

    @Update(onConflict = REPLACE)
    fun updateTweet(tweet: LocalTweet)

    @Delete
    fun deleteTweet(tweet: LocalTweet)

}