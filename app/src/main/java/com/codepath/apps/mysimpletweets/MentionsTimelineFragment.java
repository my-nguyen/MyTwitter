package com.codepath.apps.mysimpletweets;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

import java.util.List;

/**
 * Created by My on 2/25/2016.
 */
public class MentionsTimelineFragment extends TweetListFragment {
   @Override
   protected void populateTimeline(final long maxId) {
      mClient.getMentionsTimeline(maxId, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            onSuccessTimeline(maxId, response);
         }
      });
   }

   @Override
   protected String timelineId() {
      return "MENTIONS";
   }
}
