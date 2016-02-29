package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by My on 2/28/2016.
 */
abstract public class FollowActivity extends AppCompatActivity {
   protected FollowArrayAdapter mAdapter;

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

   abstract void populateTimeline(String screenName);
}
