package com.musicworkout.screen;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import com.musicworkout.database.DatabaseManager;
import com.musicworkout.model.Song;
import com.musicworkout.service.MusicService;
import com.musicworkout.storage.Preferences;
import com.musicworkout.view.audioplayer.AudioPlayer;

public class BaseScreen extends Activity {
	
	protected DatabaseManager dbManager;
	protected Preferences preferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbManager = DatabaseManager.getInstance(this);
		preferences = Preferences.getInstance(this);
	}
	
	protected void loadDefaultPlaylist(MusicService service) {
		service.setSongs(getDefaultPlaylist());
	}
	
	protected void loadDefaultPlaylist(AudioPlayer player) {
		player.setSongs(getDefaultPlaylist());
	}
	
	protected List<Song> getDefaultPlaylist() {
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

		String[] projection = {
				MediaStore.Audio.Media._ID,
		        MediaStore.Audio.Media.TITLE,
		        MediaStore.Audio.Media.DATA,
		        MediaStore.Audio.Media.ALBUM_ID
		};

		Cursor cursor = this.managedQuery(
		        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
		        projection,
		        selection,
		        null,
		        null);

		List<Song> songsList = new LinkedList<Song>();
		if (cursor.moveToFirst()) {
		    for (int i=0; i<cursor.getCount(); i++) {
		    	songsList.add(new Song(
		    			0,
		    			cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
			    		0,
			    		cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
			    		cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)),
			    		Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)))
			    	));
			    cursor.moveToNext();
		    }
		}
		return songsList;		
	}

}
