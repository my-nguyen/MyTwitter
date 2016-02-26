package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by My on 2/16/2016.
 */
// this class takes the Tweet objects and turns them into Views to be displayed in the ListView
   public class TweetArrayAdapter extends ArrayAdapter<Tweet> {
   static class ViewHolder {
      @Bind(R.id.profile_image)  ImageView         profileImage;
      @Bind(R.id.name)           TextView          name;
      @Bind(R.id.screen_name)    TextView          screenName;
      @Bind(R.id.time_ago)       TextView          timeAgo;
      @Bind(R.id.text)           LinkifiedTextView text;
      @Bind(R.id.retweet_count)  TextView          retweetCount;
      @Bind(R.id.favorite_count) TextView          favoriteCount;

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
      Tweet tweet = getItem(position);
      ViewHolder holder;
      // find or inflate the template
      if (convertView == null) {
         convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
         holder = new ViewHolder(convertView);
         convertView.setTag(holder);
      }
      else
         holder = (ViewHolder)convertView.getTag();
      // populate data into the subviews
      // clear out the old image for a recycled view
      holder.profileImage.setImageResource(android.R.color.transparent);
      Glide.with(getContext()).load(tweet.user.profileImageUrl).into(holder.profileImage);
      holder.name.setText(tweet.user.name);
      holder.screenName.setText("@" + tweet.user.screenName);
      holder.timeAgo.setText(Utils.abbreviate(Utils.getRelativeTimeAgo(tweet.createdAt)));
      holder.text.setText(tweet.text);
      if (tweet.retweetCount > 0)
         holder.retweetCount.setText(Integer.toString(tweet.retweetCount));
      if (tweet.favoriteCount > 0)
         holder.favoriteCount.setText(Integer.toString(tweet.favoriteCount));
      // return the view to be inserted into the list
      return convertView;
   }
}
