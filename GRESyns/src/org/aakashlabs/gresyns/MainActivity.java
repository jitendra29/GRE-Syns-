package org.aakashlabs.gresyns;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	public static List<ListItem> list;
    public static Boolean isLoaded;
    Thread t;
    View progressbar;
    private static ProgressDialog progDailog;
    public Handler progressHandler; 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		progDailog=new ProgressDialog(this);
		progDailog.setTitle("Please Wait");
		progDailog.setMessage("Loading...");
		progDailog.setCancelable(false);
		progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		setContentView(R.layout.activity_main);

		list=new ArrayList<ListItem>();
		isLoaded = false;
		
		ImageButton boxList=(ImageButton)findViewById(R.id.boxList);
		ImageButton groupList=(ImageButton)findViewById(R.id.groupList);
		ImageButton favourite=(ImageButton)findViewById(R.id.favourite);
		ImageButton test=(ImageButton)findViewById(R.id.test);
		ImageButton about=(ImageButton)findViewById(R.id.about);
		ImageButton share=(ImageButton)findViewById(R.id.share);

		boxList.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent wordListIntent = new Intent(getApplicationContext(), BoxList.class);
				startActivity(wordListIntent);
			}
		});

		groupList.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isLoaded)
				{
					progDailog.show();

				}
				
				else
				{
					Intent groupListIntent = new Intent(getApplicationContext(), GroupList.class);
					startActivity(groupListIntent);
				}
			}
		});

		favourite.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent favouriteIntent = new Intent(getApplicationContext(), FavouritesList.class);
				startActivity(favouriteIntent);
			}
		});

		test.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent testIntent = new Intent(getApplicationContext(), Test.class);
				startActivity(testIntent);
			}
		});

		about.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent aboutIntent = new Intent(getApplicationContext(), About.class);
				startActivity(aboutIntent);
			}
		});

		share.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

	    		Intent sendIntent = new Intent();
	    		sendIntent.setAction(Intent.ACTION_SEND);
	    		sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getText(R.string.share_content));
	    		sendIntent.setType("text/plain");
	    		startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_title)));

			}
		});

		//pre-load group list
		progressHandler = new Handler() 
		 {
		     public void handleMessage(Message msg1) 
		     {
		    	 if(progDailog.isShowing())
		         {
		    		 progDailog.dismiss();
					 Intent groupListIntent = new Intent(getApplicationContext(), GroupList.class);
					 startActivity(groupListIntent);
		         }

		         super.handleMessage(msg1);
		     }
		 };
		 
		if(!isLoaded)
		{
			t=new Thread ( new Runnable()
			{
				public void run() {
					
				 	
				    DBManager db;
				    db=new DBManager(getApplicationContext());
					db.open();					
					list = db.getGroupList();
					db.close();
					isLoaded=true;  
					progressHandler.sendEmptyMessage(0);					
			    }
			});
			t.start();
		}
	}


}
