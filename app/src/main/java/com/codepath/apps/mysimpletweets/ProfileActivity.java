package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by My on 2/25/2016.
 */
public class ProfileActivity extends AppCompatActivity {
   User user;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_profile);
      TwitterClient client = TwitterApplication.getRestClient();
      client.getUserCredentials(new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            user = User.fromJsonObject(response);
            getSupportActionBar().setTitle("@" + user.screenName);
            populateProfileHeader(user);
         }
      });

      // get the screen name passed in from the activity that launches this (TimelineActivity)
      String screenName = getIntent().getStringExtra("SCREEN_NAME");
      if (savedInstanceState == null) {
         // create the user timeline fragment
         UserTimelineFragment fragment = UserTimelineFragment.newInstance(screenName);
         // display user fragment within this activity dynamically
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
      tagLine.setText(user.tagLine);
      followers.setText(user.followerCount + " Followers");
      following.setText(user.followingCount + " Following");
      Glide.with(this).load(user.profileImageUrl).into(profileImage);
   }
}
