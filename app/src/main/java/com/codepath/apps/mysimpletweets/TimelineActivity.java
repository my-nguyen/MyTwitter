package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {
   private static final int   REQUEST_CODE = 100;
   private TwitterClient      mClient;
   private List<Tweet>        mTweets;
   private TweetsArrayAdapter mAdapter;
   private ListView           mListView;
   private SwipeRefreshLayout mSwipeContainer;
   private User               mUser = null;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_timeline);

      // find the SwipeRefreshLayout container
      mSwipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
      // find the ListView
      mListView = (ListView)findViewById(R.id.tweets_view);
      // Attach the listener to the AdapterView onCreate
      mListView.setOnScrollListener(new EndlessScrollListener() {
         @Override
         public boolean onLoadMore(int page, int totalItemsCount) {
            // triggered only when new data needs to be appended to the list, in this case when
            // lowestId is not 0.
            populateTimeline(findLowestId());
            // returns true ONLY if more data is actually being loaded; false otherwise.
            return true;
         }
      });
      // create an ArrayList data source
      mTweets = new ArrayList<>();
      // construct an adapter from the data source
      mAdapter = new TweetsArrayAdapter(this, mTweets);
      // connect the adapter to the ListView
      mListView.setAdapter(mAdapter);
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
            Intent intent = ComposeActivity.newIntent(this, mUser);
            startActivityForResult(intent, REQUEST_CODE);
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
         mAdapter.insert(tweet, 0);
      }
   }

   // lowestId == 0 means this is a fresh new feed of 25 tweets
   // lowestId != 0 means to fetch the next 25 tweets beyond the current list of tweets in the timeline
   private void populateTimeline(final long lowestId) {
      // retrieve a feed of 25 tweets for the home timeline from twitter.com
      mClient.getHomeTimeline(lowestId, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            Log.d("NGUYEN", response.toString());
            // clear adapter and database on a fresh new feed of tweets
            if (lowestId == 0) {
               Tweet.deleteAll();
               mAdapter.clear();
            }
            // create tweet objects from JSON feed from twitter.com
            List<Tweet> tweets = Tweet.fromJSONArray(response);
            Log.d("NGUYEN", "fetched " + tweets.size() + " tweets from twitter.com");
            // save tweet objects to local database via ActiveAndroid
            Tweet.saveAll(tweets);
            /*
            // test to load tweet feed from local database instead of from twitter.com
            List<Tweet> tweets = Tweet.getAll();
            Log.d("NGUYEN", "fetched " + tweets.size() + " tweets from the database");
            */
            // with a load-more feed (endless scroll), just add the feed to the current list of feed
            mAdapter.addAll(tweets);
            // signal swipe refresh has finished
            mSwipeContainer.setRefreshing(false);
         }

         @Override
         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("NGUYEN", errorResponse.toString());
         }
      });
   }

   // this method finds the new lowest id, for subsequent fetches beyond the current 25 tweets
   private long findLowestId() {
      long lowest = ((Tweet)mAdapter.getItem(0)).uid;
      for (int i = 1; i < mAdapter.getCount(); i++)
         if (lowest > ((Tweet)mAdapter.getItem(i)).uid)
            lowest = ((Tweet)mAdapter.getItem(i)).uid;
      return lowest;
   }

   private void getUserCredentials() {
      mClient.getUserCredentials(new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            mUser = User.fromJsonObject(response);
            Log.d("NGUYEN", mUser.toString());
         }
      });
   }

   private Boolean isNetworkAvailable() {
      ConnectivityManager connectivityManager
            = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
   }
}
