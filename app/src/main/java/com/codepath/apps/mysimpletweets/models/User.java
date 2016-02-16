package com.codepath.apps.mysimpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by My on 2/16/2016.
 */
public class User {
   private String name;
   private long uid;
   private String screenName;
   private String profileImageUrl;

   public String getName() {
      return name;
   }

   public long getUid() {
      return uid;
   }

   public String getScreenName() {
      return screenName;
   }

   public String getProfileImageUrl() {
      return profileImageUrl;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("<").append(name).append("> <").append(uid).append("> <").append(screenName)
            .append("> <").append(profileImageUrl).append(">");
      return builder.toString();
   }

   public static User fromJson(JSONObject jsonObject) {
      User user = new User();
      try {
         user.name = jsonObject.getString("name");
         user.uid = jsonObject.getLong("id");
         user.screenName = jsonObject.getString("screen_name");
         user.profileImageUrl = jsonObject.getString("profile_image_url");
      } catch (JSONException e) {
         e.printStackTrace();
      }
      return user;
   }
}
