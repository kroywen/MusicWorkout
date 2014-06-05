package com.musicworkout.model;

public class Song {
	
	protected int id;
	protected int songId;
	protected int playlistId;
	protected String name;
	protected String uri;
	protected int albumId;
	
	public Song() {}
	
	public Song(int id, int songId, int playlistId, String name, String uri, int albumId) {
		this.id = id;
		this.songId = songId;
		this.playlistId = playlistId;
		this.name = name;
		this.uri = uri;
		this.albumId = albumId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getSongId() {
		return songId;
	}

	public void setSongId(int songId) {
		this.songId = songId;
	}

	public int getPlaylistId() {
		return playlistId;
	}

	public void setPlaylistId(int playlistId) {
		this.playlistId = playlistId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public int getAlbumId() {
		return albumId;
	}

	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
