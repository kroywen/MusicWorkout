package com.musicworkout.screen;

import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.musicworkout.R;
import com.musicworkout.adapter.SongAdapter;
import com.musicworkout.database.DatabaseHelper;
import com.musicworkout.model.Playlist;
import com.musicworkout.model.Song;

public class EditPlaylistScreen extends BaseScreen implements OnClickListener {
	
	protected EditText name;
	protected ListView list;
	protected Button save;
	protected Button addSong;
	
	protected Playlist playlist;
	protected List<Song> songs;
	protected int id;
	protected SongAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_playlist_screen);
		getIntentData();
		initializeViews();
		populateFields();
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_ID)) {
			id = intent.getIntExtra(DatabaseHelper.FIELD_ID, 0);
			if (id != 0) {
				playlist = dbManager.getPlaylistById(id);
				songs = dbManager.getSongs(id);
			} else {
				playlist = new Playlist();
				songs = new LinkedList<Song>();
			}
		} else {
			playlist = new Playlist();
			songs = new LinkedList<Song>();
		}
		adapter = new SongAdapter(this, songs);
	}
	
	protected void initializeViews() {
		name = (EditText) findViewById(R.id.name);
		
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		registerForContextMenu(list);
		
		save = (Button) findViewById(R.id.save);
		save.setOnClickListener(this);
		
		addSong = (Button) findViewById(R.id.addSong);
		addSong.setOnClickListener(this);
	}
	
	protected void populateFields() {
		if (playlist != null)
			name.setText(playlist.getName());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			savePlaylist();
			break;
		case R.id.addSong:
			showAddSongDialog(getDefaultPlaylist());
			break;
		}
	}
	
	protected void showAddSongDialog(final List<Song> songsList) {
		String[] songsArray = getSongsAsStringArray(songsList);
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(R.string.select_song)
	        .setSingleChoiceItems(songsArray, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	Song song = songsList.get(which);
                	if (id != 0) {
                		dbManager.addSong(song, id);
                		songs = dbManager.getSongs(id);
                	} else {
                		songs.add(song);
                	}
                	adapter.setSongs(songs);
            		adapter.notifyDataSetChanged();
            		dialog.dismiss();
                	
                }
            })
	        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   dialog.dismiss();
	               }
	        });
	    builder.create().show();
	}
	
	protected String[] getSongsAsStringArray(List<Song> songsList) {
		if (songsList == null || songsList.isEmpty())
			return new String[0];
		String[] songsArray = new String[songsList.size()];
		for (int i=0; i<songsList.size(); i++)
			songsArray[i] = songsList.get(i).getName();
		return songsArray;
			
	}
	
	protected boolean isSongInPlaylist(int songId) {
		if (songs != null && songs.isEmpty())
			return false;
		for (int i=0; i<songs.size(); i++) {
			Song song = songs.get(i);
			if (song.getSongId() == songId)
				return true;
		}
		return false;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Song song = adapter.getItem(info.position);
			menu.setHeaderTitle(song.getName());
			String[] menuItems = {getString(R.string.delete)};
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Song song = adapter.getItem(info.position);
		int menuItemIndex = item.getItemId();
		switch (menuItemIndex) {
		case 0:
			deleteSong(song);
			break;
		}
		return true;
	}
	
	protected void deleteSong(Song song) {
		if (id != 0) {
			dbManager.deleteSong(song);
			songs = dbManager.getSongs(id);
		} else {
			songs.remove(song);
		}
		adapter.setSongs(songs);
		adapter.notifyDataSetChanged();
	}
	
	protected void savePlaylist() {
		playlist.setName(name.getText().toString());
		if (id != 0) {
			dbManager.updatePlaylist(playlist);
		} else {
			int playlistId = dbManager.addPlaylist(playlist);
			dbManager.addSongs(songs, playlistId);
		}
		setResult(RESULT_OK);
		finish();
	}

}
