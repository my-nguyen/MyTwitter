package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 2/28/2016.
 */
public class FollowingActivity extends FollowActivity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      String screenName = getIntent().getStringExtra("SCREEN_NAME");
      populateTimeline(screenName);
   }

   public static Intent newIntent(Context context, String screenName) {
      Intent intent = new Intent(context, FollowingActivity.class);
      intent.putExtra("SCREEN_NAME", screenName);
      return intent;
   }

   @Override
   void populateTimeline(String screenName) {
      TwitterClient client = TwitterApplication.getRestClient();
      client.getFriendList(screenName, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
               List<User> users = User.fromJSONArray(response.getJSONArray("users"));
               Log.d("NGUYEN", "getFriendList() fetched " + users.size() + " users from twitter.com");
               mAdapter.addAll(users);
            } catch (JSONException e) {
               e.printStackTrace();
            }
         }
      });
   }
}
