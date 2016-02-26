package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by My on 2/24/2016.
 */
public class HomeTimelineFragment extends TweetListFragment {
   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // populate timeline upon startup
      populateTimeline(0);
   }

   public void addNewTweet(Tweet tweet) {
      // add the Tweet at the very first position in the adapter
      // mAdapter.insert(tweet, 0);
      mTweets.add(0, tweet);
      mAdapter.notifyItemInserted(0);
   }
}
