package com.codepath.apps.mysimpletweets;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by My on 2/18/2016.
 */
public class DetailFragment extends DialogFragment implements ReplyFragment.ReplyFragmentListener {
   // find the subviews to fill with data in the template
   @Bind(R.id.profile_image)
   ImageView profileImage;
   @Bind(R.id.name)
   TextView name;
   @Bind(R.id.screen_name)
   TextView screenName;
   @Bind(R.id.text)
   TextView text;
   @Bind(R.id.image)
   ImageView image;
   @Bind(R.id.date_time)
   TextView dateTime;
   @Bind(R.id.retweet_count)
   TextView retweetCount;
   @Bind(R.id.retweet_count_label)
   TextView retweetCountLabel;
   @Bind(R.id.favorite_count)
   TextView favoriteCount;
   @Bind(R.id.favorite_count_label)
   TextView favoriteCountLabel;
   @Bind(R.id.separator2)
   View separator;
   @Bind(R.id.reply)
   ImageButton reply;
   @Bind(R.id.retweet)
   ImageButton retweet;

   // empty constructor required by DialogFragment
   public DetailFragment() {
   }

   public static DetailFragment newInstance(Tweet tweet) {
      DetailFragment fragment = new DetailFragment();
      Bundle args = new Bundle();
      args.putParcelable("TWEET", Parcels.wrap(tweet));
      fragment.setArguments(args);
      return fragment;
   }

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_detail_image, container);
      ButterKnife.bind(this, view);
      return view;
   }

   @Override
   public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      // extract Tweet and User objects from bundle
      final Tweet tweet = (Tweet)Parcels.unwrap(getArguments().getParcelable("TWEET"));
      // set up the TitleBar
      getDialog().setTitle("Tweet");
      // set up the title divider
      int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
      View titleDivider = view.findViewById(titleDividerId);
      if (titleDivider != null)
         titleDivider.setBackgroundColor(Color.BLACK);
      // populate data into the subviews
      Glide.with(getContext()).load(tweet.user.profileImageUrl).into(profileImage);
      name.setText(tweet.user.name);
      screenName.setText("@" + tweet.user.screenName);
      text.setText(Html.fromHtml(formatText(tweet.text)));
      if (tweet.mediaUrl != null) {
         int widthPixels = getContext().getResources().getDisplayMetrics().widthPixels;
         Glide.with(getContext()).load(tweet.mediaUrl).into(image);
      }
      // set up the date and time
      SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
      Date date = null;
      try {
         date = format.parse(tweet.createdAt);
      } catch (ParseException e) {
         e.printStackTrace();
      }
      dateTime.setText(DateFormat.getInstance().format(date));
      if (tweet.retweetCount == 0 && tweet.favoriteCount == 0)
         separator.setVisibility(View.INVISIBLE);
      else {
         if (tweet.retweetCount > 0) {
            retweetCount.setText("" + tweet.retweetCount);
            retweetCountLabel.setText("RETWEETS");
         }
         if (tweet.favoriteCount > 0) {
            favoriteCount.setText("" + tweet.favoriteCount);
            favoriteCountLabel.setText("LIKES");
         }
      }
      // set up the reply button
      reply.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            ReplyFragment replyFragment = ReplyFragment.newInstance(tweet);
            replyFragment.setTargetFragment(DetailFragment.this, 300);
            replyFragment.show(getFragmentManager(), "REPLY_FRAGMENT");
         }
      });
      // set up the retweet button
      retweet.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Log.d("NGUYEN", "DetailFragment.setOnClickListener.onClick");
            TwitterClient client = TwitterApplication.getRestClient();
            client.postStatusRetweet(tweet.id, new JsonHttpResponseHandler() {
               @Override
               public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                  Utils.myLog(response.toString());
                  // retweetCount.setText("" + (tweet.retweetCount + 1));
                  // retweet.setColorFilter(Color.GREEN);
               }
            });
         }
      });
   }

   @Override
   public void onFinishReplyFragment(Tweet tweet) {
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

   @Override
   public void onDestroyView() {
      super.onDestroyView();
      ButterKnife.unbind(this);
   }

   // tried many times to find a matching color, but this was the closest i came.
   private String formatBlue(String string) {
      StringBuilder builder = new StringBuilder();
      builder.append("<font color='#339966'>").append(string).append("</font>");
      return builder.toString();
   }

   // this method is broken, as in the case of a string containing \n\n@something (because
   // String#split(" ") considers "\n\n" as " ") thus leading to a bug. but in the interest of time
   // I need to use this for now and move on to implement other features of the app.
   private String formatText(String text) {
      StringBuilder builder = new StringBuilder();
      String[] tokens = text.split(" ");
      for (String token : tokens) {
         builder.append(" ");
         if (token.charAt(0) == '#')
            builder.append(formatBlue(token));
         else if (token.charAt(0) == '@')
            builder.append(formatBlue(token));
         else
            builder.append(token);
      }
      return builder.toString();
   }
}
