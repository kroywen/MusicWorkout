package com.musicworkout.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "music_workout";
	public static final int DATABASE_VERSION = 1; 
	
	public static final String TABLE_WORKOUTS = "workouts";
	public static final String TABLE_PLAYLISTS = "playlists";
	public static final String TABLE_SONGS = "songs";
	
	public static final String FIELD_ID = "id";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_WORKOUT_DURATION = "workout_duration";
	public static final String FIELD_REST_DURATION = "rest_duration";
	public static final String FIELD_REPETITIONS = "repetitions";
	public static final String FIELD_ENABLED = "enabled";
	public static final String FIELD_SELECTED = "selected";
	public static final String FIELD_PLAYLIST_ID = "playlist_id";
	public static final String FIELD_URI = "uri";
	public static final String FIELD_ALBUM_ID = "album_id";
	public static final String FIELD_SONG_ID = "song_id";
	
	public static final String CREATE_TABLE_WORKOUTS = 
			"create table if not exists " + TABLE_WORKOUTS + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_NAME + " text, " +
			FIELD_WORKOUT_DURATION + " text, " +
			FIELD_REST_DURATION + " text, " +
			FIELD_REPETITIONS + " text, " +
			FIELD_ENABLED + " integer);";
	
	public static final String DROP_TABLE_WORKOUTS = 
			"drop table if exists " + TABLE_WORKOUTS;
	
	public static final String CREATE_TABLE_PLAYLISTS = 
			"create table if not exists " + TABLE_PLAYLISTS + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_NAME + " text, " +
			FIELD_SELECTED + " integer);";
	
	public static final String DROP_TABLE_PLAYLISTS = 
			"drop table if exists " + TABLE_PLAYLISTS;
	
	public static final String CREATE_TABLE_SONGS = 
			"create table if not exists " + TABLE_SONGS + " (" +
			FIELD_ID + " integer primary key autoincrement, " +
			FIELD_SONG_ID + " integer, " +
			FIELD_PLAYLIST_ID + " integer, " +
			FIELD_NAME + " text, " +
			FIELD_URI + " text, " +
			FIELD_ALBUM_ID + " integer);";
	
	public static final String DROP_TABLE_SONGS = 
			"drop table if exists " + TABLE_SONGS;
	
	protected Context context;
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropTables(db);
		onCreate(db);
	}
	
	protected void createTables(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_WORKOUTS);
		db.execSQL(CREATE_TABLE_PLAYLISTS);
		db.execSQL(CREATE_TABLE_SONGS);
	}
	
	protected void dropTables(SQLiteDatabase db) {
		db.execSQL(DROP_TABLE_WORKOUTS);
		db.execSQL(DROP_TABLE_PLAYLISTS);
		db.execSQL(DROP_TABLE_SONGS);
	}
}
