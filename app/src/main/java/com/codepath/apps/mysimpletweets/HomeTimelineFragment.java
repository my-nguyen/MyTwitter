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
import android.widget.AdapterView;

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
public class HomeTimelineFragment extends TweetListFragment {
   private List<Tweet> mTweets;
   private TweetArrayAdapter mAdapter;
   // private TweetRecyclerViewAdapter mAdapter;

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // create an ArrayList data source
      mTweets = new ArrayList<>();
      // construct an adapter from the data source
      mAdapter = new TweetArrayAdapter(getActivity(), mTweets);
      // mAdapter = new TweetRecyclerViewAdapter(getActivity(), mTweets);
   }

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = super.onCreateView(inflater, container, savedInstanceState);

      // connect the adapter to the ListView
      mListView.setAdapter(mAdapter);
      // set up onScrollListener for ListView
      mListView.setOnScrollListener(new ListViewScrollListener() {
         @Override
         public boolean onLoadMore(int page, int totalItemsCount) {
            // triggered only when new data needs to be appended to the list, in this case when
            // lowestId is not 0.
            fillTimeline(findLowestId(mTweets));
            // true only if more data is actually being loaded; false otherwise
            return true;
         }
      });
      /*
      // set up onScrollListener for RecyclerView
      mListView.addOnScrollListener(new RecyclerViewScrollListener(mLayoutManager) {
         @Override
         public void onLoadMore(int page, int totalItemsCount) {
            // triggered only when new data needs to be appended to the list, in this case when
            // lowestId is not 0.
            fillTimeline(findLowestId());
         }
      });
      */
      // set up OnItemClickListener
      mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Tweet tweet = mTweets.get(position);
            DetailFragment detailFragment = DetailFragment.newInstance(tweet);
            detailFragment.show(getFragmentManager(), "DETAIL_FRAGMENT");
         }
      });
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

   @Override
   protected void loadFromDatabase() {
      Log.d("NGUYEN", "NO NETWORK CONNECTION.");
      mAdapter.clear();
         /*
         int count = mAdapter.getItemCount();
         if (count > 0) {
            mTweets.clear();
            mAdapter.notifyItemRangeRemoved(0, count);
         }
         */
      // load tweet feed from local database instead of from twitter.com
      List<Tweet> tweets = Tweet.getAll();
      Log.d("NGUYEN", "fetched " + tweets.size() + " tweets from the database");
      mAdapter.addAll(tweets);
         /*
         count = mAdapter.getItemCount();
         mTweets.addAll(tweets);
         mAdapter.notifyItemRangeInserted(count, tweets.size());
         */
      // signal swipe refresh has finished
      mSwipeContainer.setRefreshing(false);
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
