package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.mysimpletweets.models.Tweet;

public class TimelineActivity extends AppCompatActivity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_timeline);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.timeline, menu);
      return true;
   }

   /*
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.compose:
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
      */
}
