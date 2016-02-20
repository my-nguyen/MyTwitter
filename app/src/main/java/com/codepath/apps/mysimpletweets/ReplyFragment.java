package com.codepath.apps.mysimpletweets;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReplyFragment extends DialogFragment {
   private TwitterClient   mClient;

   public interface ReplyFragmentListener {
      void onFinishReplyFragment(Tweet tweet);
   }

   // empty constructor required by DialogFragment
   public ReplyFragment() {
   }

   public static ReplyFragment newInstance(User user, String name, ArrayList<String> screenNames) {
      ReplyFragment fragment = new ReplyFragment();
      Bundle args = new Bundle();
      args.putSerializable("USER", user);
      args.putString("NAME", name);
      args.putStringArrayList("SCREEN_NAMES", screenNames);
      fragment.setArguments(args);
      return fragment;
   }

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      return inflater.inflate(R.layout.fragment_reply, container);
   }

   @Override
   public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

      final User user = (User)getArguments().getSerializable("USER");
      String name = getArguments().getString("NAME");
      List<String> screenNames = getArguments().getStringArrayList("SCREEN_NAMES");
      mClient = TwitterApplication.getRestClient();

      ImageView profileImage = (ImageView)view.findViewById(R.id.profile_image);
      ImageButton cancelButton = (ImageButton)view.findViewById(R.id.cancel_button);
      ImageView downArrow = (ImageView)view.findViewById(R.id.down_arrow);
      TextView caption = (TextView)view.findViewById(R.id.caption);
      final EditText text = (EditText)view.findViewById(R.id.text);
      final TextView textCount = (TextView)view.findViewById(R.id.text_count);
      final Button tweetButton = (Button)view.findViewById(R.id.tweet_button);

      // populate data into the subviews
      profileImage.setImageResource(android.R.color.transparent);
      Picasso.with(getActivity()).load(user.profileImageUrl).into(profileImage);
      cancelButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            dismiss();
         }
      });
      Picasso.with(getActivity()).load(R.drawable.ic_down_arrow).into(downArrow);
      caption.setText("In reply to " + name);
      StringBuilder builder = new StringBuilder();
      for (String recipient : screenNames)
         builder.append(recipient).append(" ");
      text.setText(builder);
      // put cursor at end of text
      text.setSelection(text.getText().length());
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
            // update text with a count of the remaining characters
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
                     Log.d("NGUYEN", response.toString());
                     // compose and save a Tweet from the JSONObject
                     Tweet tweet = Tweet.fromJSONObject(response);
                     // make a callback on DetailFragment to pass the Tweet object back to the
                     // parent fragment
                     ReplyFragmentListener listener = (ReplyFragmentListener) getTargetFragment();
                     listener.onFinishReplyFragment(tweet);
                     // dismiss the ReplyFragment screen
                     dismiss();
                  }
               });
            }
         }
      });
   }

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
}
