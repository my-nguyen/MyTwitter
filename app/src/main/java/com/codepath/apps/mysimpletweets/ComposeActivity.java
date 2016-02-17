package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.Date;

public class ComposeActivity extends AppCompatActivity {
   private TwitterClient   mClient;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_compose);

      final User user = (User)getIntent().getSerializableExtra("USER_IN");
      mClient = TwitterApplication.getRestClient();

      ImageView profileImage = (ImageView)findViewById(R.id.profile_image);
      TextView name = (TextView)findViewById(R.id.name);
      TextView screenName = (TextView)findViewById(R.id.screen_name);
      final TextView text = (TextView)findViewById(R.id.text);
      Button tweetButton = (Button)findViewById(R.id.tweet_button);

      // populate data into the subviews
      profileImage.setImageResource(android.R.color.transparent);
      Picasso.with(this).load(user.getProfileImageUrl()).into(profileImage);
      name.setText(user.getName());
      screenName.setText("@" + user.getScreenName());
      tweetButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            final String status = text.getText().toString();
            if (!TextUtils.isEmpty(status)) {
               Log.d("NGUYEN", "tweeting status: " + status);
               mClient.postStatus(status, new JsonHttpResponseHandler() {
                  @Override
                  public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                     Tweet tweet = new Tweet();
                     tweet.text = status;
                     tweet.user = user;
                     tweet.createdAt = new Date().toString();
                     Intent data = new Intent();
                     data.putExtra("TWEET_OUT", tweet);
                     setResult(RESULT_OK, data);
                     finish();
                  }
               });
            }
         }
      });
   }

   public static Intent newIntent(Context context, User user) {
      Intent intent = new Intent(context, ComposeActivity.class);
      intent.putExtra("USER_IN", user);
      return intent;
   }
}
