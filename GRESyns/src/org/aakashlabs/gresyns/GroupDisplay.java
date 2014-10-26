package org.aakashlabs.gresyns;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupDisplay extends Activity {
DBManager db;
String sid;
List<String>dwords;
List<String>uwords;
String gloss;
ListView lv;
ListView lv1;
MySimpleArrayAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_display);
		dwords=new ArrayList<String>();
		uwords=new ArrayList<String>();
        setTitle("Group");

		Intent i=getIntent();
		 sid=i.getStringExtra("sid");
		 gloss=i.getStringExtra("gloss");
		 
			db=new DBManager(this);
			db.open();
			
			List<String> words=db.getGroupWords(sid);
			
			db.close();
			Iterator<String> itr = words.iterator();	
			 while(itr.hasNext())
			    {
			    	String item=itr.next();
			    	if(item.split(":")[1].equals("1"))
			    		dwords.add(item.split(":")[0]);
			    	else uwords.add(item.split(":")[0]);
			    		
			    }
			TextView tv=(TextView)findViewById(R.id.Meaning);
			tv.setText(gloss.replace("; "+'\"',";\n"+'\"'));
			 lv=(ListView)findViewById(R.id.group_word_list);
			 lv1=(ListView)findViewById(R.id.ugroup_word_list);
			lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, dwords));
			adapter=new MySimpleArrayAdapter(this,uwords);
			lv1.setAdapter(adapter);
			
			Helper1.setListViewHeightBasedOnChildren(lv);
		Helper1.setListViewHeightBasedOnChildren(lv1);}
	 
	

	public void removeItem(View v)
	{
	String item=(String)v.getTag();	
	String result;
	db.open();
	result=db.removefromgroup(item,sid);
	db.close();
	adapter.notifyDataSetChanged();
	uwords.remove(item);
	lv1.setAdapter(adapter);
	
	//setGroups();
	Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
	}
}
