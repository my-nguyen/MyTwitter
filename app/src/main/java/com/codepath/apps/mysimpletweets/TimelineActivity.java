package com.codepath.apps.mysimpletweets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TimelineActivity extends AppCompatActivity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_timeline);
   }

   /*
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.timeline, menu);
      return true;
   }

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
   */

   /*
   @Override
   public void onFinishComposeFragment(Tweet tweet) {
      // add the Tweet at the very first position in the adapter
      // mAdapter.insert(tweet, 0);
      mTweets.add(0, tweet);
      mAdapter.notifyItemInserted(0);
   }
   */
}
