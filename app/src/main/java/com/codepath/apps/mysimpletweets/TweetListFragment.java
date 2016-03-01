package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 2/24/2016.
 */
abstract public class TweetListFragment extends Fragment {
   protected SwipeRefreshLayout mSwipeContainer;
   protected TwitterClient mClient;
   protected FloatingActionButton mFABCompose;

   protected ListView mListView;
   /*
   protected RecyclerView mListView;
   protected LinearLayoutManager mLayoutManager;
   */

   // creation lifecycle event
   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      // get the singleton client
      mClient = TwitterApplication.getRestClient();
   }

   // inflation logic
   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);
      // set up SwipeRefreshLayout
      mSwipeContainer = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
      // set up Floating Action Button
      mFABCompose = (FloatingActionButton)view.findViewById(R.id.fab_compose);

      // set up ListView
      mListView = (ListView)view.findViewById(R.id.tweet_list);
      /*
      // set up RecyclerView
      mListView = (RecyclerView)view.findViewById(R.id.tweet_list);
      // set up layout manager to position the items
      mLayoutManager = new LinearLayoutManager(getActivity());
      mListView.setLayoutManager(mLayoutManager);
      // add dividers (decorators) between RecyclerView items
      RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(),
            DividerItemDecoration.VERTICAL_LIST);
      mListView.addItemDecoration(itemDecoration);
      */
      // populate timeline upon startup
      fillTimeline(0);
      return view;
   }

   abstract protected void populateTimeline(final long maxId);

   abstract protected void loadFromDatabase();

   protected void fillTimeline(long maxId) {
      if (!isNetworkAvailable() || !isOnline())
         loadFromDatabase();
      else
         populateTimeline(maxId);
   }

   // this method finds the new lowest id, for subsequent fetches beyond the current 25 tweets
   protected long findLowestId(List<Tweet> tweets) {
      long lowest = tweets.get(0).id;
      for (int i = 1; i < tweets.size(); i++)
         if (lowest > tweets.get(i).id)
            lowest = tweets.get(i).id;
      return lowest;
   }

   private Boolean isNetworkAvailable() {
      ConnectivityManager connectivityManager
            = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
      return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
   }

   private boolean isOnline() {
      Runtime runtime = Runtime.getRuntime();
      try {
         Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
         int     exitValue = ipProcess.waitFor();
         return (exitValue == 0);
      } catch (IOException e)          { e.printStackTrace(); }
      catch (InterruptedException e) { e.printStackTrace(); }
      return false;
   }
}
