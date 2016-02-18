package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by My on 2/16/2016.
 */
@Table(name = "Users")
public class User extends Model implements Serializable {
   @Column(name = "remote_id", unique = true)
   public long   uid;
   @Column(name = "Name")
   public String name;
   @Column(name = "screen_name")
   public String screenName;
   @Column(name = "profile_image_url")
   public String profileImageUrl;

   // default constructor required for ActiveAndroid model
   public User() {
      super();
   }

   // this method is used to return Tweets from User based on the foreign key. it is required so
   // that a "select *" from Tweets table will yield the associated Users data.
   public List<Tweet> tweets() {
      return getMany(Tweet.class, "User");
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("<").append(name).append("> <").append(uid).append("> <").append(screenName)
            .append("> <").append(profileImageUrl).append(">");
      return builder.toString();
   }

   public void safeSave() {
      // check whether this User already exists in the database
      User user = new Select().from(User.class).where("remote_id = ?", uid).executeSingle();
      if (user == null)
         // only save this User if it doesn't already exist. otherwise this would generate a unique
         // constraint failure in the database, which would lead to a foreign key constraint failure
         // in the associated Tweets table, resulting in the Tweet record not getting saved
         super.save();
   }

   public static void deleteAll() {
      new Delete().from(User.class).execute();
   }

   public static User fromJsonObject(JSONObject jsonObject) {
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
