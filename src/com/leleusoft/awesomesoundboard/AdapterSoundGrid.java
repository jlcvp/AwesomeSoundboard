package com.leleusoft.awesomesoundboard;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterSoundGrid extends BaseAdapter {
	
	ArrayList<GridItem> items;
	LayoutInflater mInflater;
	
	

	public AdapterSoundGrid(ArrayList<GridItem> items, Context context) {		
		this.items = items;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).id; 
	}
	
	public String getItemPath(int position)
	{
		return "mario_soundboard/"+items.get(position).getSoundUri();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GridItem item = items.get(position);
		
		convertView = mInflater.inflate(R.layout.item_layout, null ); //second parameter isn't really necessary
		
		TextView tv = (TextView)convertView.findViewById(R.id.sound_name);		
		tv.setText(item.getName());		
		
		return convertView;
		
	}

}
