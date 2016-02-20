package com.codepath.apps.mysimpletweets;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by My on 2/18/2016.
 */
public class DetailFragment extends DialogFragment implements ReplyFragment.ReplyFragmentListener {
   // empty constructor required by DialogFragment
   public DetailFragment() {
   }

   public static DetailFragment newInstance(Tweet tweet, User user) {
      DetailFragment fragment = new DetailFragment();
      Bundle args = new Bundle();
      args.putSerializable("TWEET_IN", tweet);
      args.putSerializable("USER_IN", user);
      fragment.setArguments(args);
      return fragment;
   }

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(R.layout.fragment_detail, container);
   }

   @Override
   public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      // extract Tweet and User objects from bundle
      final Tweet tweet = (Tweet)getArguments().getSerializable("TWEET_IN");
      final User user = (User)getArguments().getSerializable("USER_IN");
      // set up the TitleBar
      getDialog().setTitle("Tweet");
      // set up the title divider
      int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
      View titleDivider = view.findViewById(titleDividerId);
      if (titleDivider != null)
         titleDivider.setBackgroundColor(Color.BLACK);
      // find the subviews to fill with data in the template
      ImageView profileImage = (ImageView)view.findViewById(R.id.profile_image);
      TextView name = (TextView)view.findViewById(R.id.name);
      TextView screenName = (TextView)view.findViewById(R.id.screen_name);
      TextView text = (TextView)view.findViewById(R.id.text);
      TextView dateTime = (TextView)view.findViewById(R.id.date_time);
      ImageButton reply = (ImageButton)view.findViewById(R.id.reply);
      // populate data into the subviews
      name.setText(tweet.user.name);
      screenName.setText("@" + tweet.user.screenName);
      text.setText(tweet.text);
      Picasso.with(getContext()).load(tweet.user.profileImageUrl).into(profileImage);
      // set up the date and time
      SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
      Date date = null;
      try {
         date = format.parse(tweet.createdAt);
      } catch (ParseException e) {
         e.printStackTrace();
      }
      dateTime.setText(DateFormat.getInstance().format(date));
      // set up the reply button
      reply.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            FragmentManager fm = getFragmentManager();
            ArrayList<String> screenNames = extractScreenNames(tweet.text, tweet.user.screenName);
            ReplyFragment replyFragment = ReplyFragment.newInstance(user, tweet.user.name, screenNames);
            replyFragment.setTargetFragment(DetailFragment.this, 300);
            replyFragment.show(getFragmentManager(), "FRAGMENT_REPLY");
         }

         private ArrayList<String> extractScreenNames(String text, String mainScreenName) {
            ArrayList<String> screenNames = new ArrayList<>();
            screenNames.add("@" + mainScreenName);
            String[] tokens = text.split(" ");
            for (String token : tokens)
               if (token.charAt(0) == '@')
                  screenNames.add(token);
            return screenNames;
         }
      });
   }

   @Override
   public void onFinishReplyFragment(Tweet tweet) {
      Log.d("NGUYEN", "DetailFragment received tweet: " + tweet);
   }

   // this method expands the Dialog to occupy full screen
   @Override
   public void onResume() {
      // get existing layout params for the window
      ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
      // assign window properties to fill the parent
      params.width = WindowManager.LayoutParams.MATCH_PARENT;
      params.height = WindowManager.LayoutParams.MATCH_PARENT;
      getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
      // call super onResume after sizing
      super.onResume();
   }
}
