package com.example.tweeterExample;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener, TextWatcher, OnSharedPreferenceChangeListener{
	private static final String TAG="StatusActivity";
	EditText editText;
	Button updateButton;
	TextView textCount;
	Twitter twitter;
	SharedPreferences prefs;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);
		
		editText=(EditText)findViewById(R.id.editText);
		updateButton=(Button)findViewById(R.id.buttonUpdate);	
		updateButton.setOnClickListener(this);
		
		textCount=(TextView) findViewById(R.id.textCount);
		textCount.setText(Integer.toString(140));
		textCount.setTextColor(Color.GREEN);
		editText.addTextChangedListener(this);
		
		prefs=PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
	}
	
	@SuppressWarnings("deprecation")
	private Twitter getTwitter()
	{
		if(twitter==null)
		{
			String username, password, apiRoot;
			username=prefs.getString("username", "student");
			password=prefs.getString("password", "password");
			apiRoot=prefs.getString("apiRoot", "http://yamba.marakana.com/api");
			
			twitter=new Twitter(username,password);
			twitter.setAPIRootUrl("http://yamba.marakana.com/api");
		}
		return twitter;
	}
	
	//Funciones que emplean multihilo
	class PostToTwitter extends AsyncTask<String, Integer, String>{
		@Override
		protected String doInBackground(String... statuses) { // 
			try 
			{
				winterwell.jtwitter.Status status = twitter.updateStatus(statuses[0]);
				return status.text;
			} 
			catch (TwitterException e) 
			{
				Log.e(TAG, e.toString());
				e.printStackTrace();
				return "Failed to post"; }
			}
		
		@Override
		protected void onProgressUpdate(Integer...values)
		{
			super.onProgressUpdate(values);
			//No hace nada en este caso
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
		}
	}

	//Funcionamiento del boton
	public void onClick(View v)
	{
		try{
			getTwitter().setStatus(editText.getText().toString());
			Log.d(TAG,twitter.getStatus().toString());
		}
		catch(TwitterException e){
			Log.d(TAG, "Twitter setStatus failed:"+e);
		}
	}
	
	//Metodos del TextWatcher
	public void afterTextChanged(Editable statusText)
	{
		int count=140-statusText.length();
		textCount.setText(Integer.toString(count));
		textCount.setTextColor(Color.GREEN);
		if(count<130)
			textCount.setTextColor(Color.YELLOW);
		if(count<120)
			textCount.setTextColor(Color.RED);
	}
	
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
		//No hace nada aun
	}
	
	public void onTextChanged(CharSequence s, int start, int count, int before)
	{
		//No hace nada
	}
	
	//Crear Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater=getMenuInflater();
		inflater.inflate(R.menu.menu,menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class));
		break;
		}
		return true;
	}

	//Actualizacion de las preferencias
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		twitter=null;
		
	}
}
