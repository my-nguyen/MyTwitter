package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 2/28/2016.
 */
public class FollowActivity extends AppCompatActivity {
   FollowArrayAdapter mAdapter;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_follow);

      String screenName = getIntent().getStringExtra("SCREEN_NAME");
      // construct the data source
      ArrayList<User> users = new ArrayList<>();
      // create the adapter to convert the array to views
      mAdapter = new FollowArrayAdapter(this, users);
      // attach the adapter to a ListView
      ListView listView = (ListView)findViewById(R.id.follow_list);
      listView.setAdapter(mAdapter);
      populateTimeline(screenName);
   }

   public static Intent newIntent(Context context, String screenName) {
      Intent intent = new Intent(context, FollowActivity.class);
      intent.putExtra("SCREEN_NAME", screenName);
      return intent;
   }

   private void populateTimeline(String screenName) {
      TwitterClient client = TwitterApplication.getRestClient();
      client.getFriendList(screenName, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
               List<User> users = User.fromJSONArray(response.getJSONArray("users"));
               Log.d("NGUYEN", "getUserTimeline() fetched " + users.size() + " users from twitter.com");
               mAdapter.addAll(users);
            } catch (JSONException e) {
               e.printStackTrace();
            }
         }
      });
   }
}
