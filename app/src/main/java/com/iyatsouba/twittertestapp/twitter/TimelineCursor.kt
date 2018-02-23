package com.iyatsouba.twittertestapp.twitter

import com.twitter.sdk.android.tweetui.TimelineCursor

/**
 * TimelineCursor represents the position and containsLastItem data from a Timeline response.
 */
class TimelineCursor(minPosition: Long?, maxPosition: Long?) : TimelineCursor(minPosition, maxPosition)