package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.User;
import com.squareup.picasso.Picasso;

public class ComposeActivity extends AppCompatActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_compose);

      User user = (User)getIntent().getSerializableExtra("USER_IN");

      ImageView profileImage = (ImageView)findViewById(R.id.profile_image);
      TextView name = (TextView)findViewById(R.id.name);
      TextView screenName = (TextView)findViewById(R.id.screen_name);
      TextView body = (TextView)findViewById(R.id.text);

      // populate data into the subviews
      profileImage.setImageResource(android.R.color.transparent);
      Picasso.with(this).load(user.getProfileImageUrl()).into(profileImage);
      name.setText(user.getName());
      screenName.setText("@" + user.getScreenName());
   }

   public static Intent newIntent(Context context, User user) {
      Intent intent = new Intent(context, ComposeActivity.class);
      intent.putExtra("USER_IN", user);
      return intent;
   }
}
