package com.codepath.apps.mysimpletweets;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.List;

/**
 * Created by My on 2/16/2016.
 */
@Parcel(analyze={User.class})
@Table(name = "Users")
public class User extends Model {
   @Column(name = "remote_id", unique = true)
   public long id;
   @Column(name = "name")
   public String name;
   @Column(name = "screen_name")
   public String screenName;
   @Column(name = "followers_count")
   public int followersCount;
   @Column(name = "friends_count")
   public int friendsCount;
   @Column(name = "description")
   public String description;
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
      builder.append("<").append(id).append("> <").append(name).append("> <").append(screenName)
            .append("> <").append(followersCount).append("> <").append(friendsCount).append("> <")
            .append(description).append("> <").append(profileImageUrl).append(">");
      return builder.toString();
   }

   public static void deleteAll() {
      new Delete().from(User.class).execute();
   }

   public static User fromJSONObject(JSONObject jsonObject) {
      User user = new User();
      try {
         user.name = jsonObject.getString("name");
         user.id = jsonObject.getLong("id");
         user.screenName = jsonObject.getString("screen_name");
         user.profileImageUrl = jsonObject.getString("profile_image_url");
         user.description = jsonObject.getString("description");
         user.followersCount = jsonObject.getInt("followers_count");
         user.friendsCount = jsonObject.getInt("friends_count");
      } catch (JSONException e) {
         e.printStackTrace();
      }
      return user;
   }

   public static User findOrCreateFromJSONObject(JSONObject jsonObject) {
      long id = 0;
      User user = null;
      try {
         // check whether this User already exists in the database
         id = jsonObject.getLong("id");
         user = new Select().from(User.class).where("remote_id = ?", id).executeSingle();
         if (user == null) {
            // only save this User if it doesn't already exist. otherwise this would generate a
            // unique constraint failure in the Users table, which would lead to a foreign key
            // constraint failure in the associated Tweets table, resulting in the Tweet record not
            // getting saved
            user = User.fromJSONObject(jsonObject);
            user.save();
         }
      } catch (JSONException e) {
         e.printStackTrace();
      }
      return user;
   }
}
