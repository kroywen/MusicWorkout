package com.musicworkout.screen;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.musicworkout.R;
import com.musicworkout.adapter.PlaylistAdapter;
import com.musicworkout.database.DatabaseHelper;
import com.musicworkout.model.Playlist;
import com.musicworkout.storage.Preferences;

public class PlaylistsScreen extends BaseScreen implements OnClickListener, OnItemClickListener {
	
	public static final int ADD_PLAYLIST_REQUEST_CODE = 0;
	public static final int EDIT_PLAYLIST_REQUEST_CODE = 1;
	
	protected ListView list;
	protected Button create;
	
	protected PlaylistAdapter adapter;
	protected List<Playlist> playlists;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlists_screen);
		playlists = dbManager.getPlaylists();
		adapter = new PlaylistAdapter(this, playlists);
		initializeViews();
	}
	
	protected void initializeViews() {
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		registerForContextMenu(list);
		
		create = (Button) findViewById(R.id.create);
		create.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.create) {
			Intent intent = new Intent(this, EditPlaylistScreen.class);
			startActivityForResult(intent, ADD_PLAYLIST_REQUEST_CODE);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Playlist playlist = adapter.getItem(info.position);
			menu.setHeaderTitle(playlist.getName());
			String[] menuItems = {getString(R.string.edit), getString(R.string.delete)};
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Playlist playlist = adapter.getItem(info.position);
		int menuItemIndex = item.getItemId();
		switch (menuItemIndex) {
		case 0:
			Intent intent = new Intent(this, EditPlaylistScreen.class);
			intent.putExtra(DatabaseHelper.FIELD_ID, playlist.getId());
			startActivityForResult(intent, EDIT_PLAYLIST_REQUEST_CODE);
			break;
		case 1:
			if (playlist.isSelected() && playlists.size() > 1) {
				Toast.makeText(this, R.string.playlist_is_selected, Toast.LENGTH_SHORT).show();
			} else {
				dbManager.deletePlaylist(playlist);
				playlists = dbManager.getPlaylists();
				adapter.setPlaylists(playlists);
				adapter.notifyDataSetChanged();
				if (playlists == null || playlists.isEmpty()) {
					preferences.putString(Preferences.KEY_PLAYLIST, Preferences.DEFAULT_PLAYLIST);
					setResult(RESULT_OK);
					finish();
				}
			}
			break;
		}
		return true;
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Playlist playlist = adapter.getItem(position);
		dbManager.setSelectedPlaylist(playlist);
		preferences.putString(Preferences.KEY_PLAYLIST, playlist.getName());
		setResult(RESULT_OK);
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == ADD_PLAYLIST_REQUEST_CODE || requestCode == EDIT_PLAYLIST_REQUEST_CODE) {
				playlists = dbManager.getPlaylists();
				adapter.setPlaylists(playlists);
				adapter.notifyDataSetChanged();
			}
		}
	}

}
