package com.codepath.apps.mysimpletweets;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 2/16/2016.
 */
// this class parses the JSON and stores the data, as well as encapsulates state or display logic
@Parcel(analyze={Tweet.class})
@Table(name = "Tweets")
public class Tweet extends Model {
   @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
   public long id;
   @Column(name = "text")
   public String text;
   @Column(name = "created_at")
   public String createdAt;
   @Column(name = "retweet_count")
   public int retweetCount;
   @Column(name = "favorite_count")
   public int favoriteCount;
   @Column(name = "display_url")
   public String displayUrl;
   @Column(name = "media_url")
   public String mediaUrl;
   @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
   public User user;

   // default constructor required for ActiveAndroid model
   public Tweet() {
      super();
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("<").append(text).append("> <").append(id).append("> <").append(createdAt)
            .append("> <").append(retweetCount).append("> <").append(favoriteCount)
            .append("> <").append(displayUrl).append("> <").append(mediaUrl)
            .append(">\n").append(user);
      return builder.toString();
   }

   public static List<Tweet> getAll() {
      // return new Select().from(Tweet.class).orderBy("created_at DESC").limit(100).execute();
      return new Select().from(Tweet.class).orderBy("remote_id DESC").limit(100).execute();
   }

   public static void deleteAll() {
      User.deleteAll();
      new Delete().from(Tweet.class).execute();
   }

   // this method deserializes a JSON object into a Tweet object and saves it into local database
   public static Tweet fromJSONObject(JSONObject jsonObject) {
      Tweet tweet = new Tweet();
      try {
         tweet.id = jsonObject.getLong("id");
         tweet.createdAt = jsonObject.getString("created_at");
         tweet.retweetCount = jsonObject.getInt("retweet_count");
         tweet.favoriteCount = jsonObject.getInt("favorite_count");
         JSONArray urlJsonArray = jsonObject.getJSONObject("entities").optJSONArray("urls");
         String text = jsonObject.getString("text");
         if (urlJsonArray == null || urlJsonArray.optJSONObject(0) == null)
            tweet.displayUrl = null;
         else {
            tweet.displayUrl = urlJsonArray.optJSONObject(0).getString("display_url");
            String url = urlJsonArray.optJSONObject(0).getString("url");
            text = text.replace(url, tweet.displayUrl);
         }
         JSONArray mediaJsonArray = jsonObject.getJSONObject("entities").optJSONArray("media");
         if (mediaJsonArray == null) {
            tweet.mediaUrl = null;
            tweet.text = text;
         }
         else {
            tweet.mediaUrl = mediaJsonArray.getJSONObject(0).getString("media_url");
            String url = mediaJsonArray.optJSONObject(0).getString("url");
            String shortenedUrl = "https://t.co/u...";
            /*
            if (text.contains(shortenedUrl))
               tweet.text = text.replace(shortenedUrl, "").trim();
            else if (text.contains(url))
               tweet.text = text.replace(url, "").trim();
               */
            // the code to replace URL above is buggy. so for now just copy the text
            // sample text that will break code:
            // "RT @FOXSports: 'Boy in plastic bag' Messi jersey receives real, signed version. foxs.pt/1TB5y05 (Pic: @UNICEFargentina) https://t.c…"
            tweet.text = text;
         }
         tweet.user = User.findOrCreateFromJSONObject(jsonObject.getJSONObject("user"));
         tweet.save();
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
            Tweet tweet = fromJSONObject(jsonObject);
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
