package com.musicworkout.model;

public class Playlist {
	
	protected int id;
	protected String name;
	protected boolean selected;
	
	public Playlist() {}
	
	public Playlist(int id, String name, boolean selected) {
		this.id = id;
		this.name = name;
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
