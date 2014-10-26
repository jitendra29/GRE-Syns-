package org.aakashlabs.gresyns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    private final Context context;  

    private static final String DATABASE_NAME = "gredict";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE1 ="CREATE TABLE IF NOT EXISTS wordindex (wid INTEGER(4), word VARCHAR(256), gremeaning VARCHAR(1024), isFav TINYINT(1));";
    private static final String DATABASE_CREATE2 ="CREATE TABLE IF NOT EXISTS lists (wid INTEGER(4), sid INTEGER(8), isDefault TINYINT(1));";
    private static final String DATABASE_CREATE3 ="CREATE TABLE IF NOT EXISTS gloss (sid INTEGER(8), meaning VARCHAR(1024), pos INTEGER(1));";
    private static final String DATABASE_CREATE4 ="CREATE  VIEW v1 AS SELECT sid, COUNT(sid) as count FROM lists GROUP BY sid;";
    private static final String DATABASE_CREATE5 ="CREATE  VIEW v2 AS SELECT sid,meaning FROM gloss where sid IN (SELECT sid FROM v1 WHERE count>1);";

  
    public DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }
	

		@Override
		public void onCreate(SQLiteDatabase db) 
		{

			db.execSQL(DATABASE_CREATE1);
			db.execSQL(DATABASE_CREATE2);
			db.execSQL(DATABASE_CREATE3);

			BufferedReader br = null;
			String sCurrentLine;
			
			//Populate word index table
			try 
			{				
				Resources res=context.getResources();
				InputStream is = res.openRawResource(R.raw.wordindex);
				br = new BufferedReader(new InputStreamReader(is));
				
				while ((sCurrentLine = br.readLine()) != null) {
					db.execSQL(sCurrentLine);
				}

			}
			
			catch
			(Exception e)
			{
				e.printStackTrace();
			}
			
			finally
			{
				try
				{
					if (br != null)br.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}				
			}
		
			//Populate lists table
			try 
			{				

				Resources res=context.getResources();
				InputStream is = res.openRawResource(R.raw.list);
				br = new BufferedReader(new InputStreamReader(is));
				
				while ((sCurrentLine = br.readLine()) != null) {
					db.execSQL(sCurrentLine);
				}
			}
			
			catch
			(Exception e)
			{
				e.printStackTrace();
			}
			
			finally
			{
				try
				{
					if (br != null)br.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}				
			}
		
			//Populate gloss table
			try 
			{				
				Resources res=context.getResources();
				InputStream is = res.openRawResource(R.raw.gloss);
				br = new BufferedReader(new InputStreamReader(is));
				
				while ((sCurrentLine = br.readLine()) != null) {
					db.execSQL(sCurrentLine);
				}
			}
			
			catch
			(Exception e)
			{
				e.printStackTrace();
			}
			
			finally
			{
				try
				{
					if (br != null)br.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}				
			}

			db.execSQL(DATABASE_CREATE4);
			db.execSQL(DATABASE_CREATE5);

		
		}
			 
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, 
							  int newVersion) 
		{
			
			onCreate(db);
		}
	   

    public Boolean open() throws SQLException 
    {
        db = this.getWritableDatabase();
        return true;
    }

    public void close() 
    {
        db.close();
    }      
    

    public List<String> read(String box)
	{
    	
        List<String> wordList = new ArrayList<String>();
        String query;
        Cursor cursor=null;
    	if(box.equals("HF"))
    	{	
        	query = "SELECT distinct word FROM wordindex WHERE wid<335";
    	}
    	else
    	{
        	query = "SELECT distinct word FROM wordindex WHERE word LIKE '"+box+"%' and wid>334";   		
    	}
		cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String word;
                word=cursor.getString(cursor.getColumnIndexOrThrow("word"));
                wordList.add(word);
            } while (cursor.moveToNext());
        }
        
		cursor.close();
		return wordList;
	}
    
    public List<String[]> getWord(String name){
		// TODO Auto-generated method stub
		//String[] cols=new String []{WORD,MEAN};
		//String sid=String.valueOf(id);
        List<String[]> meanings = new ArrayList<String[]>();
        
    	Cursor c1=db.rawQuery("SELECT DISTINCT (sid) FROM lists WHERE wid = (SELECT wid FROM wordindex where word='"+name+"')",null);
        if (c1.moveToFirst()) {
            do {

            	int sid=c1.getColumnIndex("sid");                  	
            	Cursor c2=db.rawQuery("SELECT pos, meaning FROM gloss WHERE sid = "+c1.getString(sid),null);

            	if (c2.moveToFirst()) {
                    do {
                   	
                		int ipos=c2.getColumnIndex("pos");
                		int imean=c2.getColumnIndex("meaning");
                		String pos=null;
        				if(c2.getInt(ipos)==0)
                			pos="(n)";
                		else if(c2.getInt(ipos)==1)
                			pos="(v)";
                		else if(c2.getInt(ipos)==2)
                			pos="(adj)";
        				String mean[] = {pos,c2.getString(imean)};

                		meanings.add(mean);
                    } while (c2.moveToNext());
                }

            } while (c1.moveToNext());
        }

        
        return meanings;		
	}

    public String getGREMeaning(String name){

    	String meaning=null;
    	Cursor c=db.rawQuery("SELECT gremeaning FROM wordindex where word='"+name+"'",null);
        c.moveToFirst();
        
        int imean=c.getColumnIndex("gremeaning");
        meaning=c.getString(imean);
        
        return meaning;		
	}

    public String getGREMeaningWID(int wid){

    	String meaning=null;
    	Cursor c=db.rawQuery("SELECT gremeaning FROM wordindex where wid='"+wid+"'",null);
        c.moveToFirst();
        
        int imean=c.getColumnIndex("gremeaning");
        meaning=c.getString(imean);
        
        return meaning;		
	}

	public List<ListItem> getList(String word) {
		List<ListItem> l=new ArrayList<ListItem>();
		int groupcount;
		Cursor c=db.rawQuery("SELECT * FROM lists where wid in(SELECT wid from wordindex where word='"+word+"')",null);
		int imean=c.getColumnIndex("sid");
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
		{	Cursor s=db.rawQuery("SELECT distinct wid FROM lists where sid="+c.getString(imean),null);
			int index=s.getColumnIndexOrThrow("wid");
			groupcount=s.getCount();	
			if(groupcount>1){
				ListItem li=new ListItem();
				li.setSID(c.getString(imean));
				for(s.moveToFirst();!s.isAfterLast();s.moveToNext())
				{	
					li.addWID(s.getString(index));	
					
				}
			l.add(li);
			}
		}
	return l;
	}
	
	public List<ListItem> getGroupList() {
		List<ListItem> l=new ArrayList<ListItem>();

		Cursor c=db.rawQuery("SELECT sid,meaning FROM v2",null);
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
		{
			ListItem li=new ListItem();
			li.setSID(c.getString(c.getColumnIndex("sid")));
			li.setGloss(c.getString(c.getColumnIndex("meaning")));
		l.add(li);
		}
		
		/*
		Cursor c=db.rawQuery("SELECT sid FROM  `v` WHERE  `count` >1",null);
		int imean=c.getColumnIndex("sid");
		for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
		{
			Cursor s=db.rawQuery("SELECT sid,meaning FROM gloss where sid="+c.getString(imean),null);
			s.moveToFirst();
			ListItem li=new ListItem();
			li.setSID(s.getString(s.getColumnIndex("sid")));
			li.setGloss(s.getString(s.getColumnIndex("meaning")));
		l.add(li);
		}
		*/
	return l;
	}
	
	public String getGloss(String sid)
	{
		Cursor c=db.rawQuery("select meaning from gloss where sid="+sid,null);
		int i=c.getColumnIndex("meaning");
		if(c.moveToFirst())
		{
			return c.getString(i);
		}
		return "null";
	}
	public String getwd(String wid)
	{
		Cursor c=db.rawQuery("select word from wordindex where wid="+wid,null);
		int i=c.getColumnIndex("word");	
		if(c.moveToFirst())
		{
			return c.getString(i);
		}
		return "null";
	}
	
	public List<String> getGroupWords(String sid)
	{
        List<String> wordList = new ArrayList<String>();
        String query;
        Cursor cursor=null;
        query = "SELECT wid,isDefault FROM lists WHERE sid="+sid;
		cursor = db.rawQuery(query, null);
		int iwid=cursor.getColumnIndexOrThrow("wid");
		int defid=cursor.getColumnIndexOrThrow("isDefault");
		if (cursor.moveToFirst()) {
            do {
            	Cursor c=db.rawQuery("SELECT word FROM wordindex WHERE wid="+cursor.getString(iwid),null);
            	c.moveToFirst();
                String word;
                word=c.getString(c.getColumnIndexOrThrow("word"));
                
                wordList.add(word+":"+cursor.getString(defid));
            } while (cursor.moveToNext());
        }
        
		cursor.close();
		return wordList;
	}
	
	public String addToGroup(String sid, String word)
	{
		String wid=null;
        Cursor cursor=null;
        String query = "SELECT wid FROM wordindex WHERE word='"+word+"'";
		cursor = db.rawQuery(query, null);
		String result=null;
        if (cursor.moveToFirst()) {
            wid=cursor.getString(cursor.getColumnIndexOrThrow("wid"));
            
			cursor.close();
			
			query = "SELECT * FROM lists WHERE wid="+wid+" AND sid="+sid;
			cursor = db.rawQuery(query, null);
			if(cursor.moveToFirst())
			{
				result="Word already in the group.";
			}
			else
			{
				query = "INSERT INTO lists VALUES("+wid+", "+sid+",0);";
				try{
					db.execSQL(query);
					result="Added successfully";
				}
				catch(Exception e)
				{
					result="Could not add the word.";
				}
			}
        }
        
        return result;
	}
	
	public String newGroup(String meaning, String pos, String word)
	{
		if(pos.equals("Noun"))
			pos="0";
		else if(pos.equals("Verb"))
			pos="1";
		else if(pos.equals("Adjective"))
			pos="2";
		
		String wid;
		int max;
		String query = "SELECT max(sid) FROM lists;";
		Cursor cursor = db.rawQuery(query, null);
		String result=null;
        cursor.moveToFirst();
        max=Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("max(sid)")));
        cursor.close();
        
        query = "SELECT wid FROM wordindex WHERE word='"+word+"'";
		cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
        wid=cursor.getString(cursor.getColumnIndexOrThrow("wid"));        
		cursor.close();
		
		try{
	        query = "INSERT INTO lists VALUES ("+wid+", "+(max+1)+",0);";
			db.execSQL(query);
	        query = "INSERT INTO gloss VALUES ("+(max+1)+", '"+meaning+"',"+pos+");";
			db.execSQL(query);
			
			db.execSQL("DROP VIEW v1;");
			db.execSQL("CREATE  VIEW v1 AS SELECT sid, isDefault, COUNT(sid) as count FROM lists GROUP BY sid;");
			db.execSQL("DROP VIEW v2;");
			db.execSQL("CREATE  VIEW v2 AS SELECT sid,meaning FROM gloss where sid IN (SELECT sid FROM v1 WHERE count>1);");

			result="Created successfully";
		}
		catch(Exception e)
		{
			result="Could not create group.";
		}
		
		return result;
	}
	
	public List<String> search(String text,String box)
	{
    	
        List<String> wordList = new ArrayList<String>();
        String query;
        Cursor cursor=null;
    	if(box.equals("HF"))
    	{	
        	query = "SELECT distinct word FROM wordindex WHERE wid<335 and word LIKE '"+text+"%'";
    	}
    	else
    	{
        	query = "SELECT distinct word FROM wordindex WHERE word LIKE '"+box+"%' and wid>334 and word LIKE'"+text+"%'";   		
    	}
		cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String word;
                word=cursor.getString(cursor.getColumnIndexOrThrow("word"));
                // Adding word to list
                //	Toast.makeText(getApplicationContext(), word, Toast.LENGTH_LONG).show();
                wordList.add(word);
            } while (cursor.moveToNext());
        }
        
		cursor.close();
		return wordList;
	}	
public List<ListItem> searchGroup(String text)
	{
		List<ListItem> l=new ArrayList<ListItem>();
        //List<String> wordList = new ArrayList<String>();
        String query;
        Cursor c=null;
    	query = "SELECT * FROM wordindex WHERE word LIKE'"+text+"%'";   		
    	
		c = db.rawQuery(query, null);

		for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
		{
			Cursor s=db.rawQuery("SELECT sid,meaning FROM gloss where sid in(SELECT sid from lists where wid="+c.getString(c.getColumnIndexOrThrow("wid"))+")",null);
			for(s.moveToFirst();!s.isAfterLast();s.moveToNext())
			{
			//s.moveToFirst();
			ListItem li=new ListItem();
			li.setSID(s.getString(s.getColumnIndex("sid")));
			li.setGloss(s.getString(s.getColumnIndex("meaning")));
			l.add(li);
			}

        }
		c.close();
		return l;
	}	
public String getID(String word1)
{
	Cursor c=db.rawQuery("select wid from wordindex where word='"+word1+"'",null);
	int i=c.getColumnIndex("wid");
	if(c.moveToFirst())
	{
		return c.getString(i);
	}
	return "null";
}

public List<String> getFavList()
{
	
    List<String> wordList = new ArrayList<String>();
    String query;
    Cursor cursor=null;
    query = "SELECT distinct word FROM wordindex WHERE isFav=1";
	cursor = db.rawQuery(query, null);

    if (cursor.moveToFirst()) {
        do {
            String word;
            word=cursor.getString(cursor.getColumnIndexOrThrow("word"));
            wordList.add(word);
        } while (cursor.moveToNext());
    }
    
	cursor.close();
	return wordList;
}

public Boolean isFav(String word)
{
	Cursor c=db.rawQuery("SELECT isFav FROM wordindex WHERe word='"+word+"'",null);
	int i=c.getColumnIndex("isFav");
	if(c.moveToFirst())
	{
		if(c.getString(i).equals("1"))
		{
			return true;
		}
		
		else
		{
			return false;
		}
	}
	return false;
}

public void toggleFav(String word, Boolean isFav)
{
	String fav;
	if(isFav)
	{
		fav="1";
	}
	
	else
	{
		fav="0";
	}
	db.execSQL("UPDATE wordindex SET isFav="+fav+" WHERE word='"+word+"'");


}

public int getWordCount()
{
	Cursor c=db.rawQuery("SELECT MAX(wid) FROM wordindex",null);
	int i=c.getColumnIndex("MAX(wid)");
	if(c.moveToFirst())
	{
		return c.getInt(i);
	}
	
	else
	{
		return 0;
	}
}
public String getBoxCount(String box)
{
	
    //List<String> wordList = new ArrayList<String>();
    String query;
    String query1;
    String result="";
    Cursor cursor=null;
    Cursor cursor1=null;

	if(box.equals("HF"))
	{	
    	return "1@334";
	}
	else
	{	query1 = "SELECT MIN(wid) AS count FROM wordindex WHERE word LIKE '"+box+"%' and wid>334";
    	query = "SELECT MAX(wid) AS count FROM wordindex WHERE word LIKE '"+box+"%' and wid>334";   		
	}
	cursor = db.rawQuery(query, null);
	cursor1 = db.rawQuery(query1, null);

    if (cursor1.moveToFirst()) {
        result+=cursor1.getString(cursor1.getColumnIndexOrThrow("count"));
    }
    if (cursor.moveToFirst()) {
        result+="@"+cursor.getString(cursor.getColumnIndexOrThrow("count"));
    }
    
		cursor.close();
	return result;
}
public String removefromgroup(String item,String sid)
{
	String result;
	String query;
	Log.d("ITEM",item);
	Log.d("MEANING",getGloss(sid));
	String wid=getID(item);
	query="DELETE FROM lists WHERE wid="+wid+" AND sid="+sid;
try{
	db.execSQL(query);
	result=item+" removed successfully";
}
catch(Exception e)
{
	e.printStackTrace();
	result="Could not delete the word.";
}
return result;
}}
