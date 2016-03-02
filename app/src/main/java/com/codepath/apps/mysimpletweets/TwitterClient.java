package com.codepath.apps.mysimpletweets;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = "jP0T1NzCdK3ZHOBLTv2BK3ByW";
	public static final String REST_CONSUMER_SECRET = "FBXOAXVvOyeNrjGpkiXZTlyTTMCWlDUrWkcZ9fOCCNaFdTPXLG";
   public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
	public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("statuses/home_timeline.json");
      // specify the params
      RequestParams params = new RequestParams();
      params.put("count", 25);
      // show all tweets since tweet no. 1; essentially an unfiltered list of the most recent tweets
      params.put("since_id", 1);
      // maxId = 0 means just fetch the first 25 tweets
      // maxId > 0 means fetch more than the first 25 tweets, as in endless scrolling
		if (maxId > 0)
         params.put("max_id", maxId);
      // execute the request
      getClient().get(apiUrl, params, handler);
   }

   public void getMentionsTimeline(long maxId, AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("statuses/mentions_timeline.json");
      RequestParams params = new RequestParams();
      params.put("count", 25);
      if (maxId > 0)
         params.put("max_id", maxId);
      getClient().get(apiUrl, params, handler);
   }

   public void getUserTimeline(String screenName, long maxId, AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("statuses/user_timeline.json");
      RequestParams params = new RequestParams();
      params.put("count", 25);
      params.put("screen_name", screenName);
      if (maxId > 0)
         params.put("max_id", maxId);
      getClient().get(apiUrl, params, handler);
   }

   public void getAuthenticatingUserInfo(AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("account/verify_credentials.json");
      getClient().get(apiUrl, null, handler);
   }

   public void getAnyUserInfo(String screenName, AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("users/show.json");
      RequestParams params = new RequestParams();
      params.put("screen_name", screenName);
      getClient().get(apiUrl, params, handler);
   }

   public void getFriendList(String screenName, AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("friends/list.json");
      RequestParams params = new RequestParams();
      params.put("screen_name", screenName);
      getClient().get(apiUrl, params, handler);
   }

   public void getFollowerList(String screenName, AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("followers/list.json");
      RequestParams params = new RequestParams();
      params.put("screen_name", screenName);
      getClient().get(apiUrl, params, handler);
   }

   public void getSearchTweets(String query, AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("search/tweets.json");
      RequestParams params = new RequestParams();
      params.put("q", query);
      params.put("count", 25);
      getClient().get(apiUrl, params, handler);
   }

   public void postStatusUpdate(String status, String inReplyTo, AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("statuses/update.json");
      RequestParams params = new RequestParams();
      params.put("status", status);
      if (inReplyTo != null)
         params.put("in_reply_to_status_id", inReplyTo);
      getClient().post(apiUrl, params, handler);
   }

   public void postStatusRetweet(long id, AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("statuses/retweet/" + id + ".json");
      getClient().post(apiUrl, null, handler);
   }

   public void postStatusUnretweet(long id, AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("statuses/unretweet/" + id + ".json");
      getClient().post(apiUrl, null, handler);
   }

   public void postFavoriteCreate(long id, AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("favorites/create.json");
      RequestParams params = new RequestParams();
      params.put("id", id);
      getClient().post(apiUrl, params, handler);
   }

   public void postFavoriteDestroy(long id, AsyncHttpResponseHandler handler) {
      String apiUrl = getApiUrl("favorites/destroy.json");
      RequestParams params = new RequestParams();
      params.put("id", id);
      getClient().post(apiUrl, params, handler);
   }
}