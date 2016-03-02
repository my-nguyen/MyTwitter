package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by My on 2/25/2016.
 */
public class QueryResultActivity extends AppCompatActivity {
   public static Intent newIntent(Context context, String query) {
      Intent intent = new Intent(context, ProfileActivity.class);
      intent.putExtra("QUERY", query);
      return intent;
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_query_result);
      String query = getIntent().getStringExtra("QUERY");
      if (savedInstanceState == null) {
         QueryResultFragment fragment = QueryResultFragment.newInstance(query);
         FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
         ft.replace(R.id.frame_container, fragment);
         ft.commit();
      }
   }
}
