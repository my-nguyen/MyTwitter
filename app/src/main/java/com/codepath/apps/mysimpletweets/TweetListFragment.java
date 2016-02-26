package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 2/24/2016.
 */
public class TweetListFragment extends Fragment {
   protected TwitterClient mClient;
   protected List<Tweet> mTweets;
   protected TweetRecyclerViewAdapter mAdapter;
   protected SwipeRefreshLayout mSwipeContainer;
   protected RecyclerView mListView;
   protected LinearLayoutManager mLayoutManager;
   protected FloatingActionButton mFABCompose;

   // creation lifecycle event
   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // create an ArrayList data source
      mTweets = new ArrayList<>();
      // get the singleton client
      mClient = TwitterApplication.getRestClient();
      // construct an adapter from the data source
      mAdapter = new TweetRecyclerViewAdapter(getActivity(), mTweets);
   }

   // inflation logic
   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);
      // set up SwipeRefreshLayout
      mSwipeContainer = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
      // set up RecyclerView
      mListView = (RecyclerView)view.findViewById(R.id.tweet_list);
      mFABCompose = (FloatingActionButton)view.findViewById(R.id.fab_compose);
      // connect the adapter to the ListView
      mListView.setAdapter(mAdapter);
      // set up layout manager to position the items
      mLayoutManager = new LinearLayoutManager(getActivity());
      mListView.setLayoutManager(mLayoutManager);
      // add dividers (decorators) between RecyclerView items
      RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(),
            DividerItemDecoration.VERTICAL_LIST);
      mListView.addItemDecoration(itemDecoration);
      return view;
   }
}
