package com.musicworkout.database;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.musicworkout.model.Playlist;
import com.musicworkout.model.Song;
import com.musicworkout.model.Workout;

public class DatabaseManager {
	
	protected static DatabaseManager instance;
	protected DatabaseHelper dbHelper;
	protected Context context;
	protected SQLiteDatabase db;
	
	protected DatabaseManager(Context context) {
		this.context = context;
		dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	public static DatabaseManager getInstance(Context context) {
		if (instance == null)
			instance = new DatabaseManager(context);
		return instance;
	}
	
	public List<Workout> getWorkouts() {
		List<Workout> workouts = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_WORKOUTS, 
					null, null, null, null, null, null);
			if (c.moveToFirst()) {
				workouts = new LinkedList<Workout>();
				do {
					workouts.add(new Workout(
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_WORKOUT_DURATION)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_REST_DURATION)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_REPETITIONS)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ENABLED)) != 0 ? true : false 
					));
				} while (c.moveToNext());
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workouts;
	}
	
	public Workout getWorkoutById(int id) {
		Workout workout = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_WORKOUTS, 
					null, DatabaseHelper.FIELD_ID+"="+id, null, null, null, null);
			if (c.moveToFirst()) {
				workout = new Workout(
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_WORKOUT_DURATION)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_REST_DURATION)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_REPETITIONS)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ENABLED)) != 0 ? true : false
					);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return workout;
	}
	
	public long addWorkout(Workout workout) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_NAME, workout.getName());
		values.put(DatabaseHelper.FIELD_WORKOUT_DURATION, workout.getWorkoutDuration());
		values.put(DatabaseHelper.FIELD_REST_DURATION, workout.getRestDuration());
		values.put(DatabaseHelper.FIELD_REPETITIONS, workout.getRepetitions());
		values.put(DatabaseHelper.FIELD_ENABLED, workout.isEnabled() ? 1 : 0);
		return db.insert(DatabaseHelper.TABLE_WORKOUTS, null, values);
	}
	
	public void updateWorkout(Workout workout) {
		if (workout == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_NAME, workout.getName());
		values.put(DatabaseHelper.FIELD_WORKOUT_DURATION, workout.getWorkoutDuration());
		values.put(DatabaseHelper.FIELD_REST_DURATION, workout.getRestDuration());
		values.put(DatabaseHelper.FIELD_REPETITIONS, workout.getRepetitions());
		values.put(DatabaseHelper.FIELD_ENABLED, workout.isEnabled() ? 1 : 0);
		db.update(DatabaseHelper.TABLE_WORKOUTS, values, DatabaseHelper.FIELD_ID+"="+workout.getId(), null);
	}

	public void deleteWorkout(Workout workout) {
		if (workout == null)
			return;
		db.delete(DatabaseHelper.TABLE_WORKOUTS, DatabaseHelper.FIELD_ID+"="+workout.getId(), null);
	}
	
	public List<Playlist> getPlaylists() {
		List<Playlist> playlists = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_PLAYLISTS, 
					null, null, null, null, null, null);
			if (c.moveToFirst()) {
				playlists = new LinkedList<Playlist>();
				do {
					playlists.add(new Playlist(
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_SELECTED)) != 0 ? true : false 
					));
				} while (c.moveToNext());
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playlists;
	}
	
	public Playlist getPlaylistById(int id) {
		Playlist playlist = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_PLAYLISTS, 
					null, DatabaseHelper.FIELD_ID+"="+id, null, null, null, null);
			if (c.moveToFirst()) {
				playlist =new Playlist(
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_SELECTED)) != 0 ? true : false 
				);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playlist;
	}
	
	public Playlist getSelectedPlaylist() {
		Playlist playlist = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_PLAYLISTS, 
					null, DatabaseHelper.FIELD_SELECTED+"=1", null, null, null, null);
			if (c.moveToFirst()) {
				playlist =new Playlist(
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
					c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
					c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_SELECTED)) != 0 ? true : false 
				);
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return playlist;
	}
	
	public void setSelectedPlaylist(Playlist playlist) {
		if (playlist == null)
			return;
		unselectAllPlaylists();
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_SELECTED, 1);
		db.update(DatabaseHelper.TABLE_PLAYLISTS, values, DatabaseHelper.FIELD_ID+"="+playlist.getId(), null);
	}
	
	protected void unselectAllPlaylists() {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_SELECTED, 0);
		db.update(DatabaseHelper.TABLE_PLAYLISTS, values, null, null);
	}
	
	public void deletePlaylist(Playlist playlist) {
		if (playlist == null)
			return;
		db.delete(DatabaseHelper.TABLE_PLAYLISTS, DatabaseHelper.FIELD_ID+"="+playlist.getId(), null);
		deleteSongs(playlist.getId());
	}
	
	public void updatePlaylist(Playlist playlist) {
		if (playlist == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_NAME, playlist.getName());
		db.update(DatabaseHelper.TABLE_PLAYLISTS, values, DatabaseHelper.FIELD_ID+"="+playlist.getId(), null);
	}
	
	public int addPlaylist(Playlist playlist) {
		if (playlist == null)
			return 0;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_NAME, playlist.getName());
		values.put(DatabaseHelper.FIELD_SELECTED, 0);
		return (int) db.insert(DatabaseHelper.TABLE_PLAYLISTS, null, values);
	}
	
	public List<Song> getSongs(int playlistId) {
		List<Song> songs = null;
		try {
			Cursor c = db.query(DatabaseHelper.TABLE_SONGS, 
					null, DatabaseHelper.FIELD_PLAYLIST_ID+"="+playlistId, null, null, null, null);
			if (c.moveToFirst()) {
				songs = new LinkedList<Song>();
				do {
					songs.add(new Song(
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ID)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_SONG_ID)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_PLAYLIST_ID)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_NAME)),
						c.getString(c.getColumnIndex(DatabaseHelper.FIELD_URI)),
						c.getInt(c.getColumnIndex(DatabaseHelper.FIELD_ALBUM_ID))
					));
				} while (c.moveToNext());
			}
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return songs;
	}
	
	public void deleteSongs(int playlistId) {
		db.delete(DatabaseHelper.TABLE_SONGS, DatabaseHelper.FIELD_PLAYLIST_ID+"="+playlistId, null);
	}
	
	public void deleteSong(Song song) {
		if (song == null)
			return;
		db.delete(DatabaseHelper.TABLE_SONGS, DatabaseHelper.FIELD_ID+"="+song.getId(), null);
	}
	
	public void addSongs(List<Song> songs, int playlistId) {
		if (songs == null)
			return;
		for (Song song : songs)
			addSong(song, playlistId);
	}
	
	public void addSong(Song song, int playlistId) {
		if (song == null)
			return;
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.FIELD_SONG_ID, song.getSongId());
		values.put(DatabaseHelper.FIELD_PLAYLIST_ID, playlistId);
		values.put(DatabaseHelper.FIELD_NAME, song.getName());
		values.put(DatabaseHelper.FIELD_URI, song.getUri());
		values.put(DatabaseHelper.FIELD_ALBUM_ID, song.getAlbumId());
		db.insert(DatabaseHelper.TABLE_SONGS, null, values);
	}

}