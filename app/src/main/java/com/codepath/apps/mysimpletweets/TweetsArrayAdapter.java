package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by My on 2/16/2016.
 */
// this class takes the Tweet objects and turns them into Views to be displayed in the ListView
   public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {
   public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
      // super(context, android.R.layout.simple_list_item_1, tweets);
      super(context, 0, tweets);
   }

   // TO DO: transform this into a View/Holder pattern, which is recommended for any ArrayAdapter
   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      // get the tweet
      Tweet tweet = getItem(position);
      // find or inflate the template
      if (convertView == null)
         convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
      // find the subviews to fill with data in the template
      ImageView profileImage = (ImageView)convertView.findViewById(R.id.profile_image);
      TextView name = (TextView)convertView.findViewById(R.id.name);
      TextView screenName = (TextView)convertView.findViewById(R.id.screen_name);
      TextView body = (TextView)convertView.findViewById(R.id.body);
      // populate data into the subviews
      name.setText(tweet.getUser().getName());
      screenName.setText("@" + tweet.getUser().getScreenName());
      body.setText(tweet.getBody());
      // clear out the old image for a recycled view
      profileImage.setImageResource(android.R.color.transparent);
      Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(profileImage);
      // return the view to be inserted into the list
      return convertView;
   }
}
