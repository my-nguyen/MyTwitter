package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;

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
   // one central List<Tweet> to store all tweets from the 3 Timeline (Home, Mentions and User),
   // each with an identifier (timeline) to distinguish from each other
   protected List<Tweet> mTweets;
   protected TweetArrayAdapter mAdapter;
   // private TweetRecyclerViewAdapter mAdapter;

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
      // create an ArrayList data source
      mTweets = new ArrayList<>();
      // construct an adapter from the data source
      mAdapter = new TweetArrayAdapter(getActivity(), mTweets);
      // mAdapter = new TweetRecyclerViewAdapter(getActivity(), mTweets);
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
      // connect the adapter to the ListView
      mListView.setAdapter(mAdapter);
      // set up OnItemClickListener
      mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Tweet tweet = mTweets.get(position);
            DetailFragment detailFragment = DetailFragment.newInstance(tweet);
            detailFragment.show(getFragmentManager(), "DETAIL_FRAGMENT");
         }
      });
      // set up onScrollListener for ListView
      mListView.setOnScrollListener(new ListViewScrollListener() {
         @Override
         public boolean onLoadMore(int page, int totalItemsCount) {
            // triggered only when new data needs to be appended to the list, in this case when
            // lowestId is not 0.
            Log.d("NGUYEN", "onLoadMore()");
            fillTimeline(findLowestId(mTweets));
            // true only if more data is actually being loaded; false otherwise
            return true;
         }
      });
      /*
      // set up onScrollListener for RecyclerView
      mListView.addOnScrollListener(new RecyclerViewScrollListener(mLayoutManager) {
         @Override
         public void onLoadMore(int page, int totalItemsCount) {
            // triggered only when new data needs to be appended to the list, in this case when
            // lowestId is not 0.
            fillTimeline(findLowestId());
         }
      });
      */
      // populate timeline upon startup
      fillTimeline(0);
      return view;
   }

   abstract protected void populateTimeline(final long maxId);

   abstract protected String timelineId();

   private void fillTimeline(long maxId) {
      if (!isNetworkAvailable() || !isOnline())
         loadFromDatabase();
      else
         populateTimeline(maxId);
   }

   protected void onSuccessTimeline(long maxId, JSONArray response) {
      // clear adapter and database on a fresh new feed of tweets
      if (maxId == 0) {
         Tweet.deleteAll(timelineId());
         for (Tweet tweet : mTweets)
            if (tweet.timeline.equals(timelineId()))
               mTweets.remove(tweet);
         mAdapter.notifyDataSetChanged();
      }
      /*
      int count = mAdapter.getItemCount();
      if (maxId == 0) {
         Tweet.deleteAll();
         if (count > 0) {
            mTweets.clear();
            mAdapter.notifyItemRangeRemoved(0, count);
         }
      }
      */
      // create tweet objects (and save them to local database) from JSON feed from twitter.com
      List<Tweet> tweets = Tweet.fromJSONArray(response, timelineId());
      Log.d("NGUYEN", "fetched " + tweets.size() + " tweets from twitter.com");
      // with a load-more feed (endless scroll), just add the feed to the current list of feed
      mTweets.addAll(tweets);
      mAdapter.notifyDataSetChanged();
      /*
      count = mAdapter.getItemCount();
      mTweets.addAll(tweets);
      mAdapter.notifyItemRangeInserted(count, tweets.size());
      */
      // signal swipe refresh has finished
      mSwipeContainer.setRefreshing(false);
   }

   private void loadFromDatabase() {
      Log.d("NGUYEN", "NO NETWORK CONNECTION.");
      mAdapter.clear();
      /*
      int count = mAdapter.getItemCount();
      if (count > 0) {
         mTweets.clear();
         mAdapter.notifyItemRangeRemoved(0, count);
      }
      */
      // load tweet feed from local database instead of from twitter.com
      List<Tweet> tweets = Tweet.getAll(timelineId());
      Log.d("NGUYEN", "fetched " + tweets.size() + " tweets from the database");
      mAdapter.addAll(tweets);
      /*
      count = mAdapter.getItemCount();
      mTweets.addAll(tweets);
      mAdapter.notifyItemRangeInserted(count, tweets.size());
      */
      // signal swipe refresh has finished
      mSwipeContainer.setRefreshing(false);
   }

   // this method finds the new lowest id, for subsequent fetches beyond the current 25 tweets
   private long findLowestId(List<Tweet> tweets) {
      long lowest = tweets.get(0).id;
      for (int i = 1; i < tweets.size(); i++)
         if (lowest > tweets.get(i).id && tweets.get(i).timeline.equals(timelineId()))
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
