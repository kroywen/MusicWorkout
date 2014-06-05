package com.musicworkout.screen;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.musicworkout.R;
import com.musicworkout.model.Playlist;
import com.musicworkout.model.Song;
import com.musicworkout.storage.Preferences;
import com.musicworkout.view.audioplayer.AudioPlayer;

public class MusicScreen extends BaseScreen implements OnClickListener {
	
	public static final int SELECT_PLAYLIST_REQUEST_CODE = 0;
	
	protected Button playlistBtn;
	protected Spinner shuffleMode;
	protected Spinner volumeMode;
	
	protected AudioPlayer player;
	
	protected String[] shuffleModeItems;
	protected String[] volumeModeItems;
	
	protected Playlist playlist;
	protected List<Song> songs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_screen);
		setTitle(R.string.music_settings);

		shuffleModeItems = getResources().getStringArray(R.array.shuffle_mode_items);
		volumeModeItems = getResources().getStringArray(R.array.volume_mode_items);
		
		initializeViews();
		loadSongs();
	}
	
	protected void loadSongs() {
		String playlistName = preferences.getString(Preferences.KEY_PLAYLIST, Preferences.DEFAULT_PLAYLIST);
		playlistBtn.setText(playlistName);
		if (playlistName.equals(Preferences.DEFAULT_PLAYLIST)) {
			loadDefaultPlaylist(player);
		} else {
			playlist = dbManager.getSelectedPlaylist();
			if (playlist != null) {
				playlistBtn.setText(playlist.getName());
				songs = dbManager.getSongs(playlist.getId());
				player.setSongs(songs);
			}
		}
	}
	
	protected void initializeViews() {
		playlistBtn = (Button) findViewById(R.id.playlist);
		String playlistTitle = (playlist != null) ? playlist.getName() : getString(R.string.select);
		playlistBtn.setText(playlistTitle);
		playlistBtn.setOnClickListener(this);
	
		shuffleMode = (Spinner) findViewById(R.id.shuffleMode);
		ArrayAdapter<String> shuffleModeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, shuffleModeItems);
		shuffleModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		shuffleMode.setAdapter(shuffleModeAdapter);
		shuffleMode.setSelection(preferences.getInt(Preferences.KEY_SHUFFLE_MODE, Preferences.DEFAULT_SHUFFLE_MODE));
		shuffleMode.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				preferences.putInt(Preferences.KEY_SHUFFLE_MODE, shuffleMode.getSelectedItemPosition());
				if (playlist != null) {
					songs = dbManager.getSongs(playlist.getId());
					player.setSongs(songs);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		volumeMode = (Spinner) findViewById(R.id.volumeMode);
		ArrayAdapter<String> volumeModeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, volumeModeItems);
		volumeModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		volumeMode.setAdapter(volumeModeAdapter);
		volumeMode.setSelection(preferences.getInt(Preferences.KEY_VOLUME_MODE, Preferences.DEFAULT_VOLUME_MODE));
		volumeMode.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				preferences.putInt(Preferences.KEY_VOLUME_MODE, volumeMode.getSelectedItemPosition());
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		player = (AudioPlayer) findViewById(R.id.player);
		player.setSongs(songs);
		player.setLooping(true);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.playlist) {
			Intent intent = new Intent(this, PlaylistsScreen.class);
			startActivityForResult(intent, SELECT_PLAYLIST_REQUEST_CODE);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == SELECT_PLAYLIST_REQUEST_CODE) {
			loadSongs();
		}
	}
	
	@Override
	public void onResume() {
		super.onPause();
		player.onVisible();
	}
	
	@Override
	protected void onPause() {
		super.onResume();
		player.onInvisible();
	}

}
