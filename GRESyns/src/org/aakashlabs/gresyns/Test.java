package org.aakashlabs.gresyns;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Test extends Activity {

	int coption, selectedId;
	int score;
	RadioButton optionsView[];	
	TextView questionNoView, questionView;
	Button next;
	RadioGroup testOptions, testType;
	int type,count;
	String question, options[];
	int defaultTextColor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		optionsView = new RadioButton[4];
		questionNoView = (TextView)findViewById(R.id.questionNo);
		questionView = (TextView)findViewById(R.id.question);
		optionsView[0]=(RadioButton)findViewById(R.id.option1);
		optionsView[1]=(RadioButton)findViewById(R.id.option2);
		optionsView[2]=(RadioButton)findViewById(R.id.option3);
		optionsView[3]=(RadioButton)findViewById(R.id.option4);
		next=(Button)findViewById(R.id.test_next);
		testOptions = (RadioGroup) findViewById(R.id.testOptions);
		testType = (RadioGroup) findViewById(R.id.testType);
		defaultTextColor = optionsView[0].getCurrentTextColor();
		
		//initialization
		if(savedInstanceState == null)
		{
			coption = 0;
			score = 0;
			type = 1;
	    	count = 1;
	    	next.setTag("1");
	    	options = new String[4];
	    	newQuestion();
		}

    	//Restore saved instance state
		else
		{
			score = savedInstanceState.getInt("score");
			count = savedInstanceState.getInt("count");
			type = savedInstanceState.getInt("type");
			coption = savedInstanceState.getInt("coption");
			selectedId = savedInstanceState.getInt("selectedId");
			question = savedInstanceState.getString("question");
			options = savedInstanceState.getStringArray("options");
			questionView.setText(question);
			next.setTag(savedInstanceState.getString("button_tag"));
			if(next.getTag().toString().equals("2"))
			{
				next.setVisibility(0);
				next.setText("Answer");
			}
			
			else if(next.getTag().toString().equals("3"))
			{
				next.setVisibility(0);
				next.setText("Next");

				//disable answer testType
		        for(int i = 0; i < testType.getChildCount(); i++){
		            ((RadioButton)testType.getChildAt(i)).setEnabled(false);
		            
		        }
		        
				//disable answer optionsView
		        for(int i=0; i<4; i++)
				{
					optionsView[i].setTextColor(Color.GRAY);
					optionsView[i].setEnabled(false);
				}
				
				RadioButton selected = (RadioButton) findViewById(selectedId);
				selected.setTextColor(Color.RED);

				optionsView[coption].setTextColor(Color.GREEN);
				
			}
		}

		questionNoView.setText("Question "+count+":");
		
		next.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if(next.getTag().toString().equals("2"))
				{
					
					next.setText("Next");
					next.setTag("3");
					
					//disable answer testType
			        for(int i = 0; i < testType.getChildCount(); i++){
			            ((RadioButton)testType.getChildAt(i)).setEnabled(false);
			        }
			        
					//disable answer optionsView
					for(int i=0; i<4; i++)
					{
						optionsView[i].setTextColor(Color.GRAY);
						optionsView[i].setEnabled(false);
					}
					
					selectedId = testOptions.getCheckedRadioButtonId();	
					RadioButton selected = (RadioButton) findViewById(selectedId);
					selected.setTextColor(Color.RED);

					optionsView[coption].setTextColor(Color.GREEN);
				}
				
				else if(next.getTag().toString().equals("3"))
				{				
					
										
					int selectedId = testOptions.getCheckedRadioButtonId();
					
					RadioButton selected = (RadioButton) findViewById(selectedId);
					if(selected.getText().equals(optionsView[coption].getText()))
					{
						score++;
					}
		
					if(count==10)
					{
						questionNoView.setVisibility(8);
						questionView.setVisibility(8);
						testType.setVisibility(8);
						testOptions.setVisibility(8);
						finish();
						Intent scoreIntent=new Intent(getApplicationContext(), TestScore.class);
						scoreIntent.putExtra("score", score);
						startActivity(scoreIntent);
					}
					else
					{
						testOptions.clearCheck();
						next.setVisibility(8);
						next.setTag("1");
						next.setText("Answer");

						//enable answer testType
				        for(int i = 0; i < testType.getChildCount(); i++){
				            ((RadioButton)testType.getChildAt(i)).setEnabled(true);
				        }
				        
						//enable answer optionsView
						for(int i=0; i<4; i++)
						{
							optionsView[i].setTextColor(defaultTextColor);
							optionsView[i].setEnabled(true);
						}
				    	count++;
				    	questionNoView.setText("Question "+count+":");
						newQuestion();
					}
				}
			}			
			
		});

	}

	public void newQuestion()
	{

    	int cType = type;

    	if(cType==1)
    		cType = (int) ( Math.random() + 2.5);
    	
    	if(cType==2)
    		word();
		
    	if(cType==3)
    		meaning();

	}
	
	public void word()
	{

		int wid, awid;


		DBManager db = new DBManager(this);       
    	db.open();
    	int maxWid = db.getWordCount();

		wid=(int) (Math.random()*maxWid); //get max count since user may add words

		question = db.getwd(""+wid);
		questionView.setText(question);
		
		for(int i = 0; i<4; i++)
		{
			do
			{
				awid=(int) (Math.random()*3762);
			}
			while(awid==wid);
			
			options[i] = db.getGREMeaningWID(awid);
			optionsView[i].setText(options[i]);
		}
		
		coption = (int) (Math.random()*4);
		optionsView[coption].setText(db.getGREMeaningWID(wid));

		db.close();
		
	}

	public void meaning()
	{

		int wid, awid;


		DBManager db = new DBManager(this);       
    	db.open();
    	
		wid=(int) (Math.random()*3762); //get max count since user may add words
		
		question = db.getGREMeaningWID(wid);
		questionView.setText(question);
		
		for(int i = 0; i<4; i++)
		{
			do
			{
				awid=(int) (Math.random()*3762);
			}
			while(awid==wid);
			
			options[i] = db.getwd(""+awid);
			optionsView[i].setText(options[i]);
		}
		
		coption = (int) (Math.random()*4);
		optionsView[coption].setText(db.getwd(""+wid));

		db.close();
	}

	
	public void onTypeRadioButtonClicked(View view) {
		int selectedId = testType.getCheckedRadioButtonId();		
		RadioButton selected = (RadioButton) findViewById(selectedId);
		

		if(selected.getText().equals("Random"))
		{			
			type=1;
		}
		else if(selected.getText().equals("Words"))
		{
			type=2;
		}
		else if(selected.getText().equals("Meanings"))
		{
			type=3;
		}

		testOptions.clearCheck();
		next.setVisibility(8);
		newQuestion();

	}	
	
	public void onOptionRadioButtonClicked(View view) {
		next.setTag("2");
		next.setVisibility(0);
	}	
	
	protected void onSaveInstanceState(Bundle outState) {
		  super.onSaveInstanceState(outState);
		  outState.putInt("score", score);
		  outState.putInt("count", count);
		  outState.putInt("type", type);
		  outState.putInt("coption", coption);
		  outState.putInt("selectedId", selectedId);
		  outState.putString("question", question);
		  outState.putStringArray("options", options);
		  outState.putString("button_tag",next.getTag().toString());
		}

}
