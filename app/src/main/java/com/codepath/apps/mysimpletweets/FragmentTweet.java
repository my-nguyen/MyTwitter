package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by My on 2/18/2016.
 */
public class FragmentTweet extends DialogFragment {
   // empty constructor required by DialogFragment
   public FragmentTweet() {
   }

   public static FragmentTweet newInstance(Tweet tweet) {
      FragmentTweet fragment = new FragmentTweet();
      Bundle args = new Bundle();
      args.putSerializable("TWEET_IN", tweet);
      fragment.setArguments(args);
      return fragment;
   }

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(R.layout.fragment_tweet, container);
   }

   @Override
   public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      // extract Tweet object from bundle
      Tweet tweet = (Tweet)getArguments().getSerializable("TWEET_IN");
      // find the subviews to fill with data in the template
      ImageView profileImage = (ImageView)view.findViewById(R.id.profile_image);
      TextView name = (TextView)view.findViewById(R.id.name);
      TextView screenName = (TextView)view.findViewById(R.id.screen_name);
      TextView text = (TextView)view.findViewById(R.id.text);
      TextView dateTime = (TextView)view.findViewById(R.id.date_time);
      // populate data into the subviews
      name.setText(tweet.user.name);
      screenName.setText("@" + tweet.user.screenName);
      text.setText(tweet.text);
      Picasso.with(getContext()).load(tweet.user.profileImageUrl).into(profileImage);
      String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
      SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
      sf.setLenient(true);
      Date date = null;
      try {
         date = sf.parse(tweet.createdAt);
      } catch (ParseException e) {
         e.printStackTrace();
      }
      dateTime.setText(date.toString());
   }

   @Override
   public void onResume() {
      // Get existing layout params for the window
      ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
      // Assign window properties to fill the parent
      params.width = WindowManager.LayoutParams.MATCH_PARENT;
      params.height = WindowManager.LayoutParams.MATCH_PARENT;
      getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
      // Call super onResume after sizing
      super.onResume();
   }
}
