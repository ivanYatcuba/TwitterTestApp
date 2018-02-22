package com.iyatsouba.twittertestapp.db.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "tweets")
data class LocalTweet(@ColumnInfo(name = "userId") var userId: Long,
                      @ColumnInfo(name = "tweeterId") var tweeterId: Long,
                      @ColumnInfo(name = "tweetJsonData") var tweetJsonData: String) {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}