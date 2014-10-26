package org.aakashlabs.gresyns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class GroupList extends Activity {

	public static final String[] from = new String[] { "title" };
    public static final int[] to = new int[] { R.id.title };

    ListView listview;
	public static List<ListItem> savlist=new ArrayList<ListItem>();
	static List<String> data;
	static List<HashMap<String, Object>> fillMaps;


    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	

		setContentView(R.layout.activity_group_list);
		
		listview = (ListView) findViewById(R.id.group_list);
		listview.setFastScrollEnabled(true);
		
		toGroup();
		

		 

	}

	private void toGroup()
	{

		fillMaps = new ArrayList<HashMap<String, Object>>();
		for(ListItem li:MainActivity.list){
			HashMap<String, Object> map = new HashMap<String, Object>();
			String data=li.getGloss();
			String mean[]=data.split(";");
		    map.put("title",mean[0]);

		    fillMaps.add(map);	
		}
		SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.row, from, to);
	    listview.setAdapter(adapter);
					    

	    listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position,
					long id) {
				
				ListItem li=MainActivity.list.get(position);
				
				try{
					
					Intent listIntent=new Intent(getApplicationContext(), GroupDisplay.class);
					listIntent.putExtra("sid",li.getSID());
					listIntent.putExtra("gloss",li.getGloss());
					startActivity(listIntent);
					
				}
				catch(Exception e)
				{e.printStackTrace();}
			}
			
		});
				
	}

}
