package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 2/24/2016.
 */
public class TweetListFragment extends Fragment {
   protected TwitterClient mClient;
   protected List<Tweet> mTweets;
   protected TweetRecyclerViewAdapter mAdapter;
   protected SwipeRefreshLayout mSwipeContainer;
   protected RecyclerView mListView;

   // creation lifecycle event
   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // create an ArrayList data source
      mTweets = new ArrayList<>();
      // get the singleton client
      mClient = TwitterApplication.getRestClient();
      // construct an adapter from the data source
      mAdapter = new TweetRecyclerViewAdapter(getActivity(), mTweets);
   }

   // inflation logic
   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);
      // set up SwipeRefreshLayout
      mSwipeContainer = (SwipeRefreshLayout)view.findViewById(R.id.swipe_container);
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
      // set up RecyclerView
      mListView = (RecyclerView)view.findViewById(R.id.tweets_view);
      // connect the adapter to the ListView
      mListView.setAdapter(mAdapter);
      // set layout manager to position the items
      LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
      mListView.setLayoutManager(layoutManager);
      // mListView.setOnScrollListener(new ListViewScrollListener() {
      mListView.addOnScrollListener(new RecyclerViewScrollListener(layoutManager) {
         @Override
         public void onLoadMore(int page, int totalItemsCount) {
            // triggered only when new data needs to be appended to the list, in this case when
            // lowestId is not 0.
            populateTimeline(findLowestId());
         }
      });
      // add dividers (decorators) between RecyclerView items
      RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(),
            DividerItemDecoration.VERTICAL_LIST);
      mListView.addItemDecoration(itemDecoration);
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
}
