package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TimelineActivity extends AppCompatActivity implements ComposeFragment.ComposeFragmentListener {
   private static final int   REQUEST_CODE = 100;
   private TwitterClient         mClient;
   private List<Tweet>           mTweets;
   private TweetRecyclerViewAdapter mAdapter;
   private User                  mCurrentUser = null;
   @Bind(R.id.swipe_container)   SwipeRefreshLayout   mSwipeContainer;
   @Bind(R.id.tweets_view)       RecyclerView         mListView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_timeline);

      ButterKnife.bind(this);
      // Attach the listener to the AdapterView onCreate
      /*
      mListView.setOnScrollListener(new ListViewScrollListener() {
         @Override
         public boolean onLoadMore(int page, int totalItemsCount) {
            // triggered only when new data needs to be appended to the list, in this case when
            // lowestId is not 0.
            populateTimeline(findLowestId());
            // returns true ONLY if more data is actually being loaded; false otherwise.
            return true;
         }
      });
      */
      // create an ArrayList data source
      mTweets = new ArrayList<>();
      // construct an adapter from the data source
      mAdapter = new TweetRecyclerViewAdapter(mTweets);
      // connect the adapter to the ListView
      mListView.setAdapter(mAdapter);
      // set layout manager to position the items
      mListView.setLayoutManager(new LinearLayoutManager(this));
      // get the singleton client
      mClient = TwitterApplication.getRestClient();
      // populate timeline upon startup
      populateTimeline(0);
      // fetch and save the current user's credentials, for use in composing a new Tweet
      getUserCredentials();
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
      /*
      // set up click which leads to Tweet detail screen
      mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Tweet tweet = mTweets.get(position);
            DetailFragment detailFragment = DetailFragment.newInstance(tweet, mCurrentUser);
            detailFragment.show(getSupportFragmentManager(), "DETAIL_FRAGMENT");
         }
      });
      */
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.timeline, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.compose:
            /*
            Intent intent = ComposeActivity.newIntent(this, mUser);
            startActivityForResult(intent, REQUEST_CODE);
             */
            ComposeFragment dialog = ComposeFragment.newInstance(mCurrentUser);
            dialog.show(getSupportFragmentManager(), "COMPOSE_FRAGMENT");
            return true;
         default:
            return super.onOptionsItemSelected(item);
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
         // extract the Tweet created in the ComposeActivity
         Tweet tweet = (Tweet)data.getExtras().getSerializable("TWEET_OUT");
         // add the Tweet at the very first position in the adapter
         // mAdapter.insert(tweet, 0);
         mTweets.add(0, tweet);
         mAdapter.notifyItemInserted(0);
      }
   }

   @Override
   public void onFinishComposeFragment(Tweet tweet) {
      // add the Tweet at the very first position in the adapter
      // mAdapter.insert(tweet, 0);
      mTweets.add(0, tweet);
      mAdapter.notifyItemInserted(0);
   }

   // lowestId == 0 means this is a fresh new feed of 25 tweets
   // lowestId != 0 means to fetch the next 25 tweets beyond the current list of tweets in the timeline
   private void populateTimeline(final long lowestId) {
      if (!isNetworkAvailable() || !isOnline()) {
         Log.d("NGUYEN", "Network isn't available");
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
               // mAdapter.addAll(tweets);
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
      /*
      long lowest = ((Tweet)mAdapter.getItem(0)).uid;
      for (int i = 1; i < mAdapter.getCount(); i++)
         if (lowest > ((Tweet)mAdapter.getItem(i)).uid)
            lowest = ((Tweet)mAdapter.getItem(i)).uid;
            */
      long lowest = mTweets.get(0).uid;
      for (int i = 1; i < mTweets.size(); i++)
         if (lowest > mTweets.get(i).uid)
            lowest = mTweets.get(i).uid;
      return lowest;
   }

   private void getUserCredentials() {
      mClient.getUserCredentials(new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            mCurrentUser = User.fromJsonObject(response);
         }
      });
   }

   private Boolean isNetworkAvailable() {
      ConnectivityManager connectivityManager
            = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
