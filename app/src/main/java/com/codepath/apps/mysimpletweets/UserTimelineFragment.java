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
public class UserTimelineFragment extends TweetListFragment {
   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      populateTimeline();
   }

   public static UserTimelineFragment newInstance(String screenName) {
      UserTimelineFragment fragment = new UserTimelineFragment();
      Bundle args = new Bundle();
      args.putString("SCREEN_NAME", screenName);
      fragment.setArguments(args);
      return fragment;
   }

   private void populateTimeline() {
      String screenName = getArguments().getString("SCREEN_NAME");
      mClient.getUserTimeline(screenName, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            // create tweet objects (and save them to local database) from JSON feed from twitter.com
            List<Tweet> tweets = Tweet.fromJSONArray(response);
            Log.d("NGUYEN", "getUserTimeline() fetched " + tweets.size() + " tweets from twitter.com");
            // with a load-more feed (endless scroll), just add the feed to the current list of feed
            // mAdapter.addAll(tweets);
            int count = mAdapter.getItemCount();
            mTweets.addAll(tweets);
            mAdapter.notifyItemRangeInserted(count, tweets.size());
         }
      });
   }
}
