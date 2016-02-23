package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import org.parceler.Parcels;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ComposeFragment extends DialogFragment {
   private TwitterClient   mClient;
   @Bind(R.id.profile_image)  ImageView   profileImage;
   @Bind(R.id.name)           TextView    name;
   @Bind(R.id.screen_name)    TextView    screenName;
   @Bind(R.id.cancel_button)  ImageButton cancelButton;
   @Bind(R.id.text)           EditText    text;
   @Bind(R.id.text_count)     TextView    textCount;
   @Bind(R.id.tweet_button)   Button      tweetButton;

   // listener interface to pass data back to TimelineActivity
   public interface ComposeFragmentListener {
      void onFinishComposeFragment(Tweet tweet);
   }

   // empty constructor required by DialogFragment
   public ComposeFragment() {
   }

   public static ComposeFragment newInstance(User currentUser) {
      ComposeFragment fragment = new ComposeFragment();
      Bundle args = new Bundle();
      args.putParcelable("CURRENT_USER", Parcels.wrap(currentUser));
      fragment.setArguments(args);
      return fragment;
   }

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_compose, container);
      ButterKnife.bind(this, view);
      return view;
   }

   @Override
   public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);

      final User currentUser = (User)Parcels.unwrap(getArguments().getParcelable("CURRENT_USER"));
      mClient = TwitterApplication.getRestClient();

      // populate data into the subviews
      profileImage.setImageResource(android.R.color.transparent);
      Picasso.with(getContext()).load(currentUser.profileImageUrl).into(profileImage);
      name.setText(currentUser.name);
      screenName.setText("@" + currentUser.screenName);
      cancelButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            dismiss();
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
               mClient.postStatus(status, null, new JsonHttpResponseHandler() {
                  @Override
                  public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                     // compose a Tweet from the text and User data
                     Tweet tweet = new Tweet();
                     tweet.text = status;
                     tweet.user = currentUser;
                     tweet.createdAt = new Date().toString();
                     // call the callback onComposeFragmentFinish() to pass a Tweet back to TimelineActivity
                     ComposeFragmentListener listener = (ComposeFragmentListener) getActivity();
                     listener.onFinishComposeFragment(tweet);
                     // dismiss the ComposeActivity screen
                     dismiss();
                  }
               });
            }
         }
      });
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
}
