package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by My on 2/24/2016.
 */
public class HomeTimelineFragment extends TweetListFragment {
   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = super.onCreateView(inflater, container, savedInstanceState);
      // set up refresh listener
      mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
         @Override
         public void onRefresh() {
            // re-populate the timeline
            populateTimeline(0);
         }
      });
      // configure the refreshing colors
      mSwipeContainer.setColorSchemeColors(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);
      mFABCompose.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            ComposeFragment dialog = ComposeFragment.newInstance();
            dialog.show(getFragmentManager(), "COMPOSE_FRAGMENT");
         }
      });
      return view;
   }

   // if maxId == 0, fetch a fresh new feed of 25 tweets
   // if maxId != 0, fetch the next 25 tweets beyond the current list of tweets in the timeline
   // populateTimeline() for TweetArrayAdapter; data is added or removed directly from the adapter
   @Override
   protected void populateTimeline(final long maxId) {
      // retrieve a feed of 25 tweets for the home timeline from twitter.com
      mClient.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            // clear adapter and database on a fresh new feed of tweets
            if (maxId == 0) {
               Tweet.deleteAll();
               mAdapter.clear();
            }
               /*
               int count = mAdapter.getItemCount();
               if (maxId == 0) {
                  Tweet.deleteAll();
                  if (count > 0) {
                     mTweets.clear();
                     mAdapter.notifyItemRangeRemoved(0, count);
                  }
               }
               */
            // create tweet objects (and save them to local database) from JSON feed from twitter.com
            List<Tweet> tweets = Tweet.fromJSONArray(response);
            Log.d("NGUYEN", "getHomeTimeline() fetched " + tweets.size() + " tweets from twitter.com");
            // with a load-more feed (endless scroll), just add the feed to the current list of feed
            mTweets.addAll(tweets);
               /*
               count = mAdapter.getItemCount();
               mTweets.addAll(tweets);
               mAdapter.notifyItemRangeInserted(count, tweets.size());
               */
            // signal swipe refresh has finished
            mSwipeContainer.setRefreshing(false);
         }
      });
   }

   public void addNewTweet(Tweet tweet) {
      // add the Tweet at the very first position in the adapter
      mAdapter.insert(tweet, 0);
      /*
      mTweets.add(0, tweet);
      mAdapter.notifyItemInserted(0);
      */
   }
}
