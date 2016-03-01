package com.codepath.apps.mysimpletweets;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReplyFragment extends DialogFragment {
   @Bind(R.id.profile_image)
   ImageView profileImage;
   @Bind(R.id.cancel_button)
   ImageButton cancelButton;
   @Bind(R.id.down_arrow)
   ImageView downArrow;
   @Bind(R.id.caption)
   TextView caption;
   @Bind(R.id.text)
   EditText text;
   @Bind(R.id.text_count)
   TextView textCount;
   @Bind(R.id.tweet_button)
   Button tweetButton;

   public interface ReplyFragmentListener {
      void onFinishReplyFragment(Tweet tweet);
   }

   // empty constructor required by DialogFragment
   public ReplyFragment() {
   }

   public static ReplyFragment newInstance(Tweet tweet) {
      ReplyFragment fragment = new ReplyFragment();
      Bundle args = new Bundle();
      args.putParcelable("TWEET", Parcels.wrap(tweet));
      fragment.setArguments(args);
      return fragment;
   }

   @Nullable
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_reply, container);
      ButterKnife.bind(this, view);
      return view;
   }

   @Override
   public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

      final Tweet tweet = (Tweet)Parcels.unwrap(getArguments().getParcelable("TWEET"));
      // for getting user credentials and posting new status
      final TwitterClient client = TwitterApplication.getRestClient();

      // populate data into the subviews
      profileImage.setImageResource(android.R.color.transparent);
      client.getAuthenticatingUserInfo(new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            // since TwitterClient.getAuthenticatingUserInfo() is asynchronous, currentUser contains value
            // only having been inside onSuccess()
            User currentUser = User.fromJSONObject(response);
            Glide.with(getActivity()).load(currentUser.profileImageUrl).into(profileImage);
         }
      });
      cancelButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            dismiss();
         }
      });
      Glide.with(getActivity()).load(R.drawable.ic_down_arrow).into(downArrow);
      caption.setText("In reply to " + tweet.user.name);
      List<String> screenNames = extractScreenNames(tweet.text, tweet.user.screenName);
      StringBuilder builder = new StringBuilder();
      for (String screenName : screenNames)
         builder.append(screenName).append(" ");
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
               client.postStatusUpdate(status, Long.toString(tweet.id), new JsonHttpResponseHandler() {
                  @Override
                  public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                     // compose and save a Tweet from the JSONObject
                     Tweet tweet = Tweet.fromJSONObject(response, "HOME");
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

   @Override
   public void onDestroyView() {
      super.onDestroyView();
      ButterKnife.unbind(this);
   }

   // 2 bugs: (1) trailing period; (2) repeated @screenName
   private List<String> extractScreenNames(String text, String replyToScreenName) {
      List<String> screenNames = new ArrayList<>();
      screenNames.add("@" + replyToScreenName);
      String[] tokens = text.split(" ");
      for (String token : tokens)
         if (token.charAt(0) == '@')
            screenNames.add(token);
      return screenNames;
   }
}
