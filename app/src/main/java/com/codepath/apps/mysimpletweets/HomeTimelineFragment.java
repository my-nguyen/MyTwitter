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
   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // populate timeline upon startup
      populateTimeline(0);
   }

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
      // mListView.setOnScrollListener(new ListViewScrollListener() {
      mListView.addOnScrollListener(new RecyclerViewScrollListener(mLayoutManager) {
         @Override
         public void onLoadMore(int page, int totalItemsCount) {
            // triggered only when new data needs to be appended to the list, in this case when
            // lowestId is not 0.
            populateTimeline(findLowestId());
         }
      });
      mFABCompose.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            ComposeFragment dialog = ComposeFragment.newInstance();
            dialog.show(getFragmentManager(), "COMPOSE_FRAGMENT");
         }
      });
      return view;
   }

   // if lowestId == 0, fetch a fresh new feed of 25 tweets
   // if lowestId != 0, fetch the next 25 tweets beyond the current list of tweets in the timeline
   protected void populateTimeline(final long lowestId) {
      if (!isNetworkAvailable() || !isOnline()) {
         Log.d("NGUYEN", "THERE IS NO NETWORK CONNECTION.");
         // mAdapter.clear();
         int count = mAdapter.getItemCount();
         if (count > 0) {
            mTweets.clear();
            mAdapter.notifyItemRangeRemoved(0, count);
         }
         // load tweet feed from local database instead of from twitter.com
         List<Tweet> tweets = Tweet.getAll();
         Log.d("NGUYEN", "fetched " + tweets.size() + " tweets from the database");
         // mAdapter.addAll(tweets);
         count = mAdapter.getItemCount();
         mTweets.addAll(tweets);
         mAdapter.notifyItemRangeInserted(count, tweets.size());
         // signal swipe refresh has finished
         mSwipeContainer.setRefreshing(false);
      }
      else {
         // retrieve a feed of 25 tweets for the home timeline from twitter.com
         mClient.getHomeTimeline(lowestId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
               Log.d("NGUYEN", response.toString());
               // clear adapter and database on a fresh new feed of tweets
               int count = mAdapter.getItemCount();
               if (lowestId == 0) {
                  Tweet.deleteAll();
                  // mAdapter.clear();
                  if (count > 0) {
                     mTweets.clear();
                     mAdapter.notifyItemRangeRemoved(0, count);
                  }
               }
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
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
               if (errorResponse != null)
                  Log.d("NGUYEN", errorResponse.toString());
            }
         });
      }
   }

   // this method finds the new lowest id, for subsequent fetches beyond the current 25 tweets
   private long findLowestId() {
      long lowest = mTweets.get(0).uid;
      for (int i = 1; i < mTweets.size(); i++)
         if (lowest > mTweets.get(i).uid)
            lowest = mTweets.get(i).uid;
      return lowest;
   }

   private Boolean isNetworkAvailable() {
      ConnectivityManager connectivityManager
            = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
   }

   private boolean isOnline() {
      Runtime runtime = Runtime.getRuntime();
      try {
         Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
         int     exitValue = ipProcess.waitFor();
         return (exitValue == 0);
      } catch (IOException e)          { e.printStackTrace(); }
      catch (InterruptedException e) { e.printStackTrace(); }
      return false;
   }

   public void addNewTweet(Tweet tweet) {
      // add the Tweet at the very first position in the adapter
      // mAdapter.insert(tweet, 0);
      mTweets.add(0, tweet);
      mAdapter.notifyItemInserted(0);
   }
}
