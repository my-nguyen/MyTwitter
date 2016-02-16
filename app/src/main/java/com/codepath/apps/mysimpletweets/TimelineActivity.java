package com.codepath.apps.mysimpletweets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity {
   private TwitterClient      mClient;
   private List<Tweet>        mTweets;
   private TweetsArrayAdapter mAdapter;
   private ListView           mListView;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_timeline);
      // find the ListView
      mListView = (ListView)findViewById(R.id.tweets_view);
      // create an ArrayList data source
      mTweets = new ArrayList<>();
      // construct an adapter from the data source
      mAdapter = new TweetsArrayAdapter(this, mTweets);
      // connect the adapter to the ListView
      mListView.setAdapter(mAdapter);
      // get the singleton client
      mClient = TwitterApplication.getRestClient();
      // populate the timeline
      populateTimeline();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      return super.onOptionsItemSelected(item);
   }

   // send an API request to get the timeline json;
   // fill the listview by creating the tweet objects from the json
   private void populateTimeline() {
      mClient.getHomeTimeline(new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            Log.d("NGUYEN", response.toString());
            List<Tweet> tweets = Tweet.fromJSONArray(response);
            mAdapter.addAll(tweets);
         }

         @Override
         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("NGUYEN", errorResponse.toString());
         }
      });
   }
}
