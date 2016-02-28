package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by My on 2/25/2016.
 */
public class ProfileActivity extends AppCompatActivity {
   public static Intent newIntent(Context context, String screenName) {
      Intent intent = new Intent(context, ProfileActivity.class);
      intent.putExtra("SCREEN_NAME", screenName);
      return intent;
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_profile);

      // get the screen name passed in from the activity that launches this (TimelineActivity)
      String screenName = getIntent().getStringExtra("SCREEN_NAME");
      Log.d("NGUYEN", "ProfileActivity screenName: " + screenName);
      TwitterClient client = TwitterApplication.getRestClient();
      if (screenName == null)
         client.getAuthenticatingUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
               User user = User.fromJSONObject(response);
               getSupportActionBar().setTitle("@" + user.screenName);
               populateProfileHeader(user);
            }
         });
      else
         client.getUserInfo(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
               User user = User.fromJSONObject(response);
               getSupportActionBar().setTitle("@" + user.screenName);
               populateProfileHeader(user);
            }
         });

      // load the UserTimelineFragment dynamically (as opposed to loading from XML)
      if (savedInstanceState == null) {
         UserTimelineFragment fragment = UserTimelineFragment.newInstance(screenName);
         FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
         ft.replace(R.id.frame_container, fragment);
         ft.commit();
      }
   }

   private void populateProfileHeader(User user) {
      TextView name = (TextView)findViewById(R.id.name);
      TextView tagLine = (TextView)findViewById(R.id.tag_line);
      TextView followers = (TextView)findViewById(R.id.followers);
      TextView following = (TextView)findViewById(R.id.following);
      ImageView profileImage = (ImageView)findViewById(R.id.profile_image);
      name.setText(user.name);
      tagLine.setText(user.description);
      followers.setText(user.followersCount + " Followers");
      following.setText(user.friendsCount + " Following");
      Glide.with(this).load(user.profileImageUrl).into(profileImage);
   }
}
