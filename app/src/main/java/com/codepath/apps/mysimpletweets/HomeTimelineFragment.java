package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

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
      // set up the refreshing colors
      mSwipeContainer.setColorSchemeColors(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);
      // set up the floating action button
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
            onSuccessTimeline(maxId, response);
         }
      });
   }

   @Override
   protected String timelineId() {
      return "HOME";
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
