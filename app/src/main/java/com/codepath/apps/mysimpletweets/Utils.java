package com.codepath.apps.mysimpletweets;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by My on 2/23/2016.
 */
public class Utils {

   // this method returns an abbreviated "time ago" string in the format of 20m, 2h, etc.
   public static String abbreviate(String relativeTime) {
      // split string into tokens
      String[] tokens = relativeTime.toString().split(" ");
      if (tokens.length == 1) {
         // for some reason DateUtils.getRelativeTimeSpanString() keeps returning "Yesterday"
         // instead of "17 hours ago"
         Log.d("NGUYEN", "relativeTime: " + relativeTime);
         return tokens[0];
      }
      else
         // return number appended with "m" or "h", e.g. 20m, 2h
         return tokens[0] + tokens[1].charAt(0);
   }

   // this method is supplied by Nathan Esquizi. it takes a raw Json date string in the format of
   // "Tue Feb 16 23:22:46 +0000 2016" and returns that time in the "ago" format: 3 minutes ago,
   // 53 seconds ago, etc.
   public static String getRelativeTimeAgo(String rawJsonDate) {
      String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
      SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
      sf.setLenient(true);

      String relativeDate = "";
      try {
         Date date = sf.parse(rawJsonDate);
         long dateMillis = date.getTime();
         relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
               System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
      } catch (ParseException e) {
         e.printStackTrace();
      }

      return relativeDate;
   }

   public static void myLog(String json) {
      for (int i = 0; i < json.length(); i += 4000)
         if (i + 4000 < json.length())
            Log.d("NGUYEN", json.substring(i, i+4000));
         else
            Log.d("NGUYEN", json.substring(i, json.length()));
   }
}
