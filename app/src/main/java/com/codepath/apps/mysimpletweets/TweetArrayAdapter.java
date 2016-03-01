package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by My on 2/16/2016.
 */
// this class takes the Tweet objects and turns them into Views to be displayed in the ListView
public class TweetArrayAdapter extends ArrayAdapter<Tweet> {
   static class ViewHolder {
      @Bind(R.id.profile_image)
      ImageView profileImage;
      @Bind(R.id.name)
      TextView name;
      @Bind(R.id.screen_name)
      TextView screenName;
      @Bind(R.id.time_ago)
      TextView timeAgo;
      @Bind(R.id.text)
      LinkifiedTextView text;
      @Bind(R.id.reply)
      ImageButton reply;
      /*
      @Bind(R.id.retweet_count)
      TextView retweetCount;
      @Bind(R.id.favorite_count)
      TextView favoriteCount;
      */

      public ViewHolder(View view) {
         ButterKnife.bind(this, view);
      }
   }

   public TweetArrayAdapter(Context context, List<Tweet> tweets) {
      // super(context, android.R.layout.simple_list_item_1, tweets);
      super(context, 0, tweets);
   }

   // TO DO: transform this into a View/Holder pattern, which is recommended for any ArrayAdapter
   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      // get the tweet
      final Tweet tweet = getItem(position);
      final ViewHolder holder;
      // per Nathan's instruction, use setTag() to store username, although that username turns out
      // to be incorrect; the correct username is from tweet.user.screenName without having to be
      // stored and retrieved from setTag() and getTag()
      final String username;
      // find or inflate the template
      if (convertView == null) {
         convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
         holder = new ViewHolder(convertView);
         convertView.setTag(holder);
         username = tweet.user.screenName;
         holder.profileImage.setTag(username);
      }
      else {
         holder = (ViewHolder) convertView.getTag();
         username = (String) holder.profileImage.getTag();
      }
      // populate data into the subviews
      // clear out the old image for a recycled view
      holder.profileImage.setImageResource(android.R.color.transparent);
      Picasso.with(getContext()).load(tweet.user.profileImageUrl).into(holder.profileImage);
      holder.profileImage.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent intent = ProfileActivity.newIntent(getContext(), tweet.user.screenName);
            getContext().startActivity(intent);
         }
      });
      holder.name.setText(tweet.user.name);
      holder.screenName.setText("@" + tweet.user.screenName);
      holder.timeAgo.setText(Utils.abbreviate(Utils.getRelativeTimeAgo(tweet.createdAt)));
      holder.text.setText(tweet.text);
      holder.reply.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            ReplyFragment replyFragment = ReplyFragment.newInstance(tweet);
            replyFragment.show(((FragmentActivity)getContext()).getSupportFragmentManager(), "REPLY_FRAGMENT");
         }
      });
      /*
      if (tweet.retweetCount > 0)
         holder.retweetCount.setText(Integer.toString(tweet.retweetCount));
      if (tweet.favoriteCount > 0)
         holder.favoriteCount.setText(Integer.toString(tweet.favoriteCount));
         */
      // return the view to be inserted into the list
      return convertView;
   }
}
