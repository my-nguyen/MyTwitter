package com.codepath.apps.mysimpletweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 2/16/2016.
 */
// this class parses the JSON and stores the data, as well as encapsulates state or display logic
public class Tweet {
   private String body;
   // unique database id for the tweet
   private long uid;
   private User user;
   private String createdAt;

   public String getBody() {
      return body;
   }

   public long getUid() {
      return uid;
   }

   public User getUser() {
      return user;
   }

   public String getCreatedAt() {
      return createdAt;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("<").append(body).append("> <").append(uid).append("> <").append(createdAt)
            .append(">\n     ").append(user);
      return builder.toString();
   }

   // this method deserializes a JSON object into a Tweet object
   public static Tweet fromJSON(JSONObject jsonObject) {
      Tweet tweet = new Tweet();
      try {
         tweet.body = jsonObject.getString("text");
         tweet.uid = jsonObject.getLong("id");
         tweet.createdAt = jsonObject.getString("created_at");
         tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
      } catch (JSONException e) {
         e.printStackTrace();
      }
      return tweet;
   }

   public static List<Tweet> fromJSONArray(JSONArray jsonArray) {
      List<Tweet> tweets = new ArrayList<>();
      for (int i = 0; i < jsonArray.length(); i++) {
         try {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Tweet tweet = fromJSON(jsonObject);
            if (tweet != null)
               tweets.add(tweet);
         } catch (JSONException e) {
            e.printStackTrace();
            // keep deserializing the next JSONObject in the JSONArray even if the deserialization
            // of the current JSONObject fails
            continue;
         }
      }
      return tweets;
   }
}
