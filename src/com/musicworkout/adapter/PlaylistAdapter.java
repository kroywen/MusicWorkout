package com.musicworkout.adapter;

import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.musicworkout.R;
import com.musicworkout.model.Playlist;

public class PlaylistAdapter extends BaseAdapter {
	
	protected LayoutInflater inflater;
	protected List<Playlist> playlists;
	
	public PlaylistAdapter(Context context, List<Playlist> playlists) {
		inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		this.playlists = (playlists != null) ? playlists : new LinkedList<Playlist>();
	}

	public int getCount() {
		return playlists.size();
	}

	public Playlist getItem(int position) {
		return playlists.get(position);
	}

	public long getItemId(int position) {
		return playlists.get(position).getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.workout_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final Playlist playlist = playlists.get(position);
		holder.name.setText(playlist.getName());
		
		return convertView;
	}
	
	public void setPlaylists(List<Playlist> playlists) {
		this.playlists = (playlists != null) ? playlists : new LinkedList<Playlist>();
	}
	
	class ViewHolder {
		TextView name;
	}

}
