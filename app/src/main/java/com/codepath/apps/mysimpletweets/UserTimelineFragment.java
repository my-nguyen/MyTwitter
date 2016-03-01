package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 2/25/2016.
 */
public class UserTimelineFragment extends TweetListFragment {
   private List<Tweet> mTweets;
   private TweetArrayAdapter mAdapter;

   public static UserTimelineFragment newInstance(String screenName) {
      UserTimelineFragment fragment = new UserTimelineFragment();
      Bundle args = new Bundle();
      args.putString("SCREEN_NAME", screenName);
      fragment.setArguments(args);
      return fragment;
   }

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // create an ArrayList data source
      mTweets = new ArrayList<>();
      // construct an adapter from the data source
      mAdapter = new TweetArrayAdapter(getActivity(), mTweets);
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
      return view;
   }

   @Override
   protected void populateTimeline(final long maxId) {
      String screenName = getArguments().getString("SCREEN_NAME");
      mClient.getUserTimeline(screenName, maxId, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            // create tweet objects (and save them to local database) from JSON feed from twitter.com
            List<Tweet> tweets = Tweet.fromJSONArray(response);
            Log.d("NGUYEN", "getUserTimeline() fetched " + tweets.size() + " tweets from twitter.com");
            // with a load-more feed (endless scroll), just add the feed to the current list of feed
            mAdapter.addAll(tweets);
         }
      });
   }

   @Override
   protected void loadFromDatabase() {
      Log.d("NGUYEN", "NO NETWORK CONNECTION.");
      mAdapter.clear();
      // load tweet feed from local database instead of from twitter.com
      List<Tweet> tweets = Tweet.getAll();
      Log.d("NGUYEN", "fetched " + tweets.size() + " tweets from the database");
      mAdapter.addAll(tweets);
      // signal swipe refresh has finished
      mSwipeContainer.setRefreshing(false);
   }
}
