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
import com.musicworkout.model.Song;

public class SongAdapter extends BaseAdapter {
	
	protected LayoutInflater inflater;
	protected List<Song> songs;
	
	public SongAdapter(Context context, List<Song> songs) {
		inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		this.songs = (songs != null) ? songs : new LinkedList<Song>();
	}

	public int getCount() {
		return songs.size();
	}

	public Song getItem(int position) {
		return songs.get(position);
	}

	public long getItemId(int position) {
		return songs.get(position).getId();
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
		
		Song song = songs.get(position);
		holder.name.setText(song.getName());
		
		return convertView;
	}
	
	public void setSongs(List<Song> songs) {
		this.songs = (songs != null) ? songs : new LinkedList<Song>();
	}
	
	class ViewHolder {
		TextView name;
	}

}
