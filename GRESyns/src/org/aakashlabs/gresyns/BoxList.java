package org.aakashlabs.gresyns;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BoxList extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_box_list);
	    ListView listview = (ListView) findViewById(R.id.box_list);

		final String boxlist[] = {"HF","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

		try{		
			listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, boxlist));
			}
		
		catch(Exception e)
		{
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();				
		}
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {
					Intent listIntent=new Intent(getApplicationContext(), Box.class);
					listIntent.putExtra("box",boxlist[position]);
					startActivity(listIntent);
				  }
				});
	}

}
