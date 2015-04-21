package com.leleusoft.awesomesoundboard;

public class GridItem {
	
	String soundUri;	
	String name;
	long id;
	public GridItem(String soundUri, String name) {
		super();
		this.soundUri = soundUri;
		this.name = name;
	}
	
	
	public String getSoundUri() {
		return soundUri;
	}
	public String getName() {
		return name;
	}
	
	
	
}
