package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.List;

/**
 * Created by My on 2/25/2016.
 */
public class UserTimelineFragment extends TweetListFragment {
   public static UserTimelineFragment newInstance(String screenName) {
      UserTimelineFragment fragment = new UserTimelineFragment();
      Bundle args = new Bundle();
      args.putString("SCREEN_NAME", screenName);
      fragment.setArguments(args);
      return fragment;
   }

   @Override
   protected void populateTimeline(final long maxId) {
      String screenName = getArguments().getString("SCREEN_NAME");
      mClient.getUserTimeline(screenName, maxId, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            onSuccessTimeline(maxId, response);
         }
      });
   }

   @Override
   protected String timelineId() {
      return "USER";
   }
}
