package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;

import java.util.List;

/**
 * Created by My on 2/24/2016.
 */
public class TweetListFragment extends Fragment {
   protected TwitterClient mClient;
   protected List<Tweet> mTweets;
   protected TweetRecyclerViewAdapter mAdapter;
   protected User mCurrentUser = null;
   protected SwipeRefreshLayout mSwipeContainer;
   protected RecyclerView mListView;

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return super.onCreateView(inflater, container, savedInstanceState);
   }

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }
}
