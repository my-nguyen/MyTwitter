package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

public class TimelineActivity extends AppCompatActivity implements ComposeFragment.Listener {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_timeline);

      // get the viewpager
      ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
      // set the viewpager adapter for the pager
      viewPager.setAdapter(new TweetPagerAdapter(getSupportFragmentManager()));
      // find the pager sliding tabstrip
      PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip)findViewById(R.id.tabs);
      // attach the pager tabstrip to the viewpager
      tabStrip.setViewPager(viewPager);
   }

   // return the order of the fragments in the view pager
   public class TweetPagerAdapter extends FragmentPagerAdapter {
      private String[] tabTitles = { "Home", "Mentions" };

      // Adapter gets the manager to insert or remove fragment from activity
      public TweetPagerAdapter(FragmentManager manager) {
         super(manager);
      }

      // the order and creation of fragments within the pager
      @Override
      public Fragment getItem(int position) {
         if (position == 0)
            return new HomeTimelineFragment();
         else if (position == 1)
            return new MentionsTimelineFragment();
         else
            return null;
      }

      // how many fragments there are to swipe between
      @Override
      public int getCount() {
         return tabTitles.length;
      }

      // return the tab title
      @Override
      public CharSequence getPageTitle(int position) {
         return tabTitles[position];
      }
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
            ComposeFragment dialog = ComposeFragment.newInstance();
            dialog.show(getSupportFragmentManager(), "COMPOSE_FRAGMENT");
            return true;
         case R.id.profile:
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
         default:
            return super.onOptionsItemSelected(item);
      }
   }

   @Override
   public void onFinishComposeFragment(Tweet tweet) {
      Log.d("NGUYEN", "received from ComposeFragment Tweet: " + tweet);
      // HomeTimelineFragment fragment = getSupportFragmentManager().findFragmentById(R.id.);
      // fragment.addNewTweet(tweet);
   }
}
