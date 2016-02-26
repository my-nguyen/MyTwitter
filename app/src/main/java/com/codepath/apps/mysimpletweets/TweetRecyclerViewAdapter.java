package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by My on 2/23/2016.
 */
// create the basic adapter extending from RecyclerView.Adapter
// note that we specify the custom ViewHolder which gives us access to our views
public class TweetRecyclerViewAdapter extends RecyclerView.Adapter<TweetRecyclerViewAdapter.ViewHolder> {
   // provide a direct reference to each of the views within a data item used to cache the views
   // within the item layout for fast access
   public class ViewHolder extends RecyclerView.ViewHolder {
      @Bind(R.id.profile_image)  ImageView         profileImage;
      @Bind(R.id.name)           TextView          name;
      @Bind(R.id.screen_name)    TextView          screenName;
      @Bind(R.id.time_ago)       TextView          timeAgo;
      @Bind(R.id.text)           LinkifiedTextView text;
      // @Bind(R.id.retweet_count)  TextView          retweetCount;
      // @Bind(R.id.favorite_count) TextView          favoriteCount;

      // we also create a constructor that accepts the entire item row and does the view lookups to
      // find each subview
      public ViewHolder(View itemView) {
         // stores the itemView in a public final member variable that can be used to access the
         // context from any ViewHolder instance.
         super(itemView);
         ButterKnife.bind(this, itemView);
         itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // gets item position
               int position = getLayoutPosition();
               Tweet tweet = mTweets.get(position);
               DetailFragment detailFragment = DetailFragment.newInstance(tweet);
               detailFragment.show(((FragmentActivity)mContext).getSupportFragmentManager(), "DETAIL_FRAGMENT");
            }
         });
      }
   }

   private List<Tweet>  mTweets;
   private Context      mContext;

   public TweetRecyclerViewAdapter(Context context, List<Tweet> tweets) {
      mContext = context;
      mTweets = tweets;
   }

   // usually involves inflating a layout from XML and returning the holder
   @Override
   public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      LayoutInflater inflater = LayoutInflater.from(mContext);
      // inflate the custom layout
      View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
      // return a new holder instance
      ViewHolder viewHolder = new ViewHolder(tweetView);
      return viewHolder;
   }

   // involves populating data into the item through holder
   @Override
   public void onBindViewHolder(ViewHolder holder, int position) {
      // get the data model based on position
      Tweet tweet = mTweets.get(position);
      // Set item views based on the data model
      holder.profileImage.setImageResource(android.R.color.transparent);
      Glide.with(mContext).load(tweet.user.profileImageUrl).into(holder.profileImage);
      holder.name.setText(tweet.user.name);
      holder.screenName.setText("@" + tweet.user.screenName);
      holder.timeAgo.setText(Utils.abbreviate(Utils.getRelativeTimeAgo(tweet.createdAt)));
      holder.text.setText(tweet.text);
      /*
      if (tweet.retweetCount > 0)
         holder.retweetCount.setText(Integer.toString(tweet.retweetCount));
      if (tweet.favoriteCount > 0)
         holder.favoriteCount.setText(Integer.toString(tweet.favoriteCount));
         */
   }

   // return the total count of items
   @Override
   public int getItemCount() {
      return mTweets.size();
   }
}
