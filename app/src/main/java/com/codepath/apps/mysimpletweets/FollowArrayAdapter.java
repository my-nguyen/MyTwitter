package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by My on 2/28/2016.
 */
public class FollowArrayAdapter extends ArrayAdapter<User> {
   public FollowArrayAdapter(Context context, ArrayList<User> users) {
      super(context, 0, users);
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      // get the data item for this position
      User user = getItem(position);
      // check if an existing view is being reused, otherwise inflate the view
      if (convertView == null)
         convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_follow, parent, false);
      // look up view for data population
      ImageView profileImage = (ImageView)convertView.findViewById(R.id.profile_image);
      TextView name = (TextView)convertView.findViewById(R.id.name);
      TextView screenName = (TextView)convertView.findViewById(R.id.screen_name);
      TextView description = (TextView)convertView.findViewById(R.id.description);
      Picasso.with(getContext()).load(user.profileImageUrl).into(profileImage);
      name.setText(user.name);
      screenName.setText(user.screenName);
      description.setText(user.description);
      // return the completed view to render on screen
      return convertView;
   }
}
