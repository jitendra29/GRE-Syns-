package org.aakashlabs.gresyns;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FavouritesList extends Activity {

	ListView listview;
	static List<String> data;
	static List<HashMap<String, Object>> fillMaps;
	private DBManager db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favourites_list);
		
	    db=new DBManager(getApplicationContext());
	    db.open();
		data=db.getFavList();
		db.close();
		
		listview = (ListView) findViewById(R.id.fav_list);
		
		if(!data.isEmpty())
		{
			listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, data));
			
			listview.setOnItemClickListener(new OnItemClickListener() {
				  @Override
				  public void onItemClick(AdapterView<?> parent, View view,
				    int position, long id) {
					  
						String item=parent.getItemAtPosition(position).toString();
						Intent listIntent=new Intent(getApplicationContext(), DisplayActivity.class);
						listIntent.putExtra("ID",item);
						listIntent.putExtra("caller","FavouritesList");
						startActivity(listIntent);
					  }
					}); 
		}
		
		else
		{
			data.add("No favourites");
			listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, data));
		}
	}


}
