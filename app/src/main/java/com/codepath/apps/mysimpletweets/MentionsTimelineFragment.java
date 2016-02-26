package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.List;

/**
 * Created by My on 2/25/2016.
 */
public class MentionsTimelineFragment extends TweetListFragment {
   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      populateTimeline();
   }

   private void populateTimeline() {
      mClient.getMentionsTimeline(new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            Log.d("NGUYEN", response.toString());
            // clear adapter and database on a fresh new feed of tweets
            int count = mAdapter.getItemCount();
            // create tweet objects (and save them to local database) from JSON feed from twitter.com
            List<Tweet> tweets = Tweet.fromJSONArray(response);
            Log.d("NGUYEN", "fetched " + tweets.size() + " tweets from twitter.com");
            // with a load-more feed (endless scroll), just add the feed to the current list of feed
            count = mAdapter.getItemCount();
            mTweets.addAll(tweets);
            mAdapter.notifyItemRangeInserted(count, tweets.size());
            // signal swipe refresh has finished
            mSwipeContainer.setRefreshing(false);
         }

         @Override
         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            if (errorResponse != null)
               Log.d("NGUYEN", errorResponse.toString());
         }
      });
   }
}
