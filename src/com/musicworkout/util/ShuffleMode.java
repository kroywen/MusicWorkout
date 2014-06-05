package com.musicworkout.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import com.musicworkout.model.Song;

public class ShuffleMode {
	
	public static final int SHUFFLE_MODE_DEFAULT = 0;
	public static final int SHUFFLE_MODE_SONGS = 1;
	public static final int SHUFFLE_MODE_ALBUMS = 2;
	public static final int SHUFFLE_MODE_OFF = 3;
	
	public static List<Song> shuffle(List<Song> songs, int shuffleMode) {
		if (songs == null || songs.isEmpty())
			return songs;
		switch (shuffleMode) {
		case SHUFFLE_MODE_DEFAULT:
			shuffleByDefault(songs);
			break;
		case SHUFFLE_MODE_SONGS:
			shuffleBySongs(songs);
			break;
		case SHUFFLE_MODE_ALBUMS:
			shuffleByAlbums(songs);
			break;
		case SHUFFLE_MODE_OFF:
			break;
		}
		return songs;
	}
	
	public static List<Song> shuffleByDefault(List<Song> songs) {
		Collections.shuffle(songs);
		return songs;
	}
	
	public static List<Song> shuffleBySongs(List<Song> songs) {
		Collections.shuffle(songs); // Shuffle by songs is default
		return songs;
	}
	
	@SuppressLint("UseSparseArrays")
	public static List<Song> shuffleByAlbums(List<Song> songs) {
		HashMap<Integer, List<Song>> map = new HashMap<Integer, List<Song>>();
		for (Song song : songs) {
			Integer key = song.getAlbumId();
			if (!map.containsKey(key))
				map.put(key, new LinkedList<Song>());
			map.get(key).add(song);
		}
		List<Song> tmpSongs = new LinkedList<Song>();
		Iterator<List<Song>> i = map.values().iterator();
		while (i.hasNext())
			tmpSongs.addAll(i.next());
		Collections.shuffle(tmpSongs);
		return songs;
	}

}
