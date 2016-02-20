package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
      ImageButton cancelButton = (ImageButton)findViewById(R.id.cancel_button);
      final EditText text = (EditText)findViewById(R.id.text);
      final TextView textCount = (TextView)findViewById(R.id.text_count);
      final Button tweetButton = (Button)findViewById(R.id.tweet_button);

      // populate data into the subviews
      profileImage.setImageResource(android.R.color.transparent);
      Picasso.with(this).load(user.profileImageUrl).into(profileImage);
      name.setText(user.name);
      screenName.setText("@" + user.screenName);
      cancelButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            finish();
         }
      });
      text.addTextChangedListener(new TextWatcher() {
         ColorStateList mNormalColors = null;
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (mNormalColors == null)
               mNormalColors = textCount.getTextColors();
         }

         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
         }

         @Override
         public void afterTextChanged(Editable s) {
            // the number of characters in the EditText
            int count = s.toString().length();
            // update text with the number
            textCount.setText(Integer.toString(140 - count));
            if (count > 140) {
               // change text color to RED
               textCount.setTextColor(Color.RED);
               // disable the "Tweet" button
               tweetButton.setEnabled(false);
            } else {
               // restore color
               textCount.setTextColor(mNormalColors);
               // enable the "Tweet" button
               tweetButton.setEnabled(true);
            }
         }
      });

      tweetButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            final String status = text.getText().toString();
            if (!TextUtils.isEmpty(status)) {
               Log.d("NGUYEN", "tweeting status: " + status);
               mClient.postStatus(status, new JsonHttpResponseHandler() {
                  @Override
                  public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                     // compose a Tweet from the text and User data
                     Tweet tweet = new Tweet();
                     tweet.text = status;
                     tweet.user = user;
                     tweet.createdAt = new Date().toString();
                     // stuff that Tweet in an Intent, to pass back to TimelineActivity
                     Intent data = new Intent();
                     data.putExtra("TWEET_OUT", tweet);
                     setResult(RESULT_OK, data);
                     // dismiss the ComposeActivity screen
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
