package org.aakashlabs.gresyns;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TestScore extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_score);
		
		TextView score = (TextView)findViewById(R.id.score);
		Intent intent = getIntent();
		final int scores = intent.getIntExtra("score", 0);
		score.setText(String.valueOf(scores));
		
		Button testAgain=(Button)findViewById(R.id.test_again);

testAgain.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				Intent testIntent=new Intent(getApplicationContext(), Test.class);
				startActivity(testIntent);

			}			
			
		});

	}

}
