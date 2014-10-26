package org.aakashlabs.gresyns;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class DisplayActivity extends Activity {
	public static String word;
	public int id;
	TextView wordview;
	TextView greview;
	ImageButton fav;
	Boolean isFav=false;
	int max;
	int min;
	String data[]={};
	int i,l;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);

			Intent intent=getIntent();
		
		
		word=intent.getStringExtra("ID");
        String caller=intent.getStringExtra("caller");
        min=intent.getIntExtra("minwid",-1);
		

		wordview=(TextView)findViewById(R.id.word);
		greview=(TextView)findViewById(R.id.gremeaning);
		final DBManager db=new DBManager(this);
		db.open();
		id=Integer.parseInt(db.getID(word));
		db.close();
		
		if(caller.equals("Box"))
		{
			data=Box.data.toArray(data);
			i=Box.data.indexOf(word);
		}

		else if(caller.equals("FavouritesList"))
		{
			data=FavouritesList.data.toArray(data);
			i=FavouritesList.data.indexOf(word);
		}

		l=data.length;
		
		Button b=(Button)findViewById(R.id.bview);
		ImageButton b1=(ImageButton)findViewById(R.id.next1);
		ImageButton b2=(ImageButton)findViewById(R.id.prev1);
		fav=(ImageButton)findViewById(R.id.fav);
		if (savedInstanceState != null) {
		word=savedInstanceState.getString("word");
		}
		setMeaning(word);

		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(getApplicationContext(), ViewWordList.class);
				//i.putExtra("word",word);
				startActivity(i);
			}
		});
		
		fav.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isFav)
				{
					fav.setImageResource(R.drawable.ic_star);
					isFav=false;
				}
				
				else
				{
					fav.setImageResource(R.drawable.ic_star_marked);
					isFav=true;
				}

				db.open();
				db.toggleFav(word,isFav);
				db.close();
			}
		});

b1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				i=(++i)%l;
				word=data[i];
				
				setMeaning(word);
			}
		});
b2.setOnClickListener(new View.OnClickListener() {
	
	@Override
	public void onClick(View v) {

		if(i==0)
		{
			i=l;
		}
		
		i=(--i)%l;
		word=data[i];

		setMeaning(word);
	}
});
		
	}
public void setMeaning(String word)
{	
	final ListView listview = (ListView) findViewById(R.id.meanings);
	DBManager db=new DBManager(this);
	db.open();
	isFav=db.isFav(word);
	if(isFav)
	{
		fav.setImageResource(R.drawable.ic_star_marked);
	}
	
	else
	{
		fav.setImageResource(R.drawable.ic_star);
	}		
	setTitle(word);
	wordview.setText(word);
	String gremeaning = db.getGREMeaning(word);
	greview.setText(gremeaning);
	
    List<String[]> data = db.getWord(word);
    db.close();
    
    List<String> meaning = new ArrayList<String>();
    Iterator<String[]> itr = data.iterator();
	String[] item;

    while(itr.hasNext())
    {
    	item=itr.next();
		item[1]=item[1].replace("; "+'\"',";\n"+'\"');
    	meaning.add(item[0]+"  "+item[1]);
    	
    }
    
	listview.setAdapter(new ArrayAdapter<String>(DisplayActivity.this,android.R.layout.simple_list_item_1, meaning));
	listview.postDelayed(new Runnable() {
		public void run() {
		Helper.setListViewHeightBasedOnChildren(listview);
		}
		}, 400);

}

@Override
protected void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);
	outState.putString("word",word);
	
}
}
