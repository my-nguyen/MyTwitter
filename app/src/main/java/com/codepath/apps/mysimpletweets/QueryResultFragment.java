package com.codepath.apps.mysimpletweets;

import android.os.Bundle;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by My on 2/25/2016.
 */
public class QueryResultFragment extends TweetListFragment {
   public static QueryResultFragment newInstance(String query) {
      QueryResultFragment fragment = new QueryResultFragment();
      Bundle args = new Bundle();
      args.putString("QUERY", query);
      fragment.setArguments(args);
      return fragment;
   }

   @Override
   protected void populateTimeline(final long maxId) {
      String query = getArguments().getString("QUERY");
      mClient.getSearchTweets(query, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
               JSONArray statusesJsonArray = response.getJSONArray("statuses");
               onSuccessTimeline(maxId, statusesJsonArray);
            } catch (JSONException e) {
               e.printStackTrace();
            }
         }
      });
   }

   @Override
   protected String timelineId() {
      return "QUERY";
   }
}
