package org.aakashlabs.gresyns;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
  private final Context context;
  private final List<String> values;

  public MySimpleArrayAdapter(Context context, List<String> uwords) {
    super(context, R.layout.rowbutton, uwords);
    this.context = context;
    this.values = uwords;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.rowbutton, parent, false);
    TextView textView = (TextView) rowView.findViewById(R.id.title1);
    textView.setText(values.get(position));
    ImageButton b1=(ImageButton)rowView.findViewById(R.id.delete);
	b1.setTag(values.get(position));
    return rowView;
    
  }
} 