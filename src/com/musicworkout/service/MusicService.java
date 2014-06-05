package com.musicworkout.service;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import com.musicworkout.model.Song;
import com.musicworkout.storage.Preferences;
import com.musicworkout.util.ShuffleMode;

public class MusicService extends Service {
	
	public static final int STATE_PLAYING = 0;
	public static final int STATE_PAUSED = 1;
	public static final int STATE_STOPPED = 2;
	
	protected MediaPlayer player;
	protected AudioManager audioManager;
	protected int initialVolume;
	protected float currentVolume;
	protected float adjust;
	
	protected List<Song> songs;
	protected Song currentSong;
	protected boolean looping;
	protected int state;

	public class MusicBinder extends Binder {
		public MusicService getService() {
            return MusicService.this;
        }
    }
	
	@Override
    public void onCreate() {
		initAudio();
    }
	
	protected void initAudio() {
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 7, 0);
		
		player = new MediaPlayer();
		setLooping(true);
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				if (looping && state == STATE_PLAYING) {
					nextSong();
				} else
					setState(STATE_STOPPED);
			}
		});
		player.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.start();
				setState(STATE_PLAYING);
			}
		});
	}

    @Override
    public void onDestroy() {
        stopSong();
        player.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new MusicBinder();
    
    public void stopSong() {
    	if (player.isPlaying())
    		player.stop();
    	setState(STATE_STOPPED);
    }
    
    public void pauseSong() {
    	if (player.isPlaying())
    		player.pause();
    	setState(STATE_PAUSED);
    }
    
    public void resumeSong() {
    	if (!player.isPlaying())
    		player.start();
    	setState(STATE_PLAYING);
    }
    
    public void setSong(Song song) {
		if (song == null)
			return;
		currentSong = song;
	}
    
    public void playSong() {
		if (state == STATE_PAUSED) {
			player.start();
			setState(STATE_PLAYING);
		} else {
			try {
				Uri soundUri = Uri.parse(currentSong.getUri());
				try {
					player.setDataSource(this, soundUri);
				} catch (IllegalStateException e) {
					e.printStackTrace();
					player.reset();
					player.setDataSource(this, soundUri);
				}
				player.prepareAsync();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		setState(STATE_STOPPED);
	    	}
		}
	}
    
    public void previousSong() {
 		if (songs != null) {
			int currentIndex = songs.indexOf(currentSong);
			if (currentIndex == -1)
				return;
			if (currentIndex == 0)
				currentIndex = songs.size()-1;
			else
				currentIndex--;

			stopSong();
			setSong(songs.get(currentIndex));
			playSong();
		}
	}
    
    public void nextSong() {
		if (songs != null) {
			int currentIndex = songs.indexOf(currentSong);
			if (currentIndex == -1)
				return;
			if (currentIndex == songs.size()-1)
				currentIndex = 0;
			else
				currentIndex++;

			stopSong();
			setSong(songs.get(currentIndex));
			playSong();
		}
		
	}
	
	public void setSongs(List<Song> songs) {
		this.songs = songs;
		int shuffleMode = Preferences.getInstance(this).getInt(Preferences.KEY_SHUFFLE_MODE, Preferences.DEFAULT_SHUFFLE_MODE);
		ShuffleMode.shuffle(songs, shuffleMode);
		if (songs != null)
			setSong(songs.get(0));
	}
    
    protected void setState(int state) {
    	this.state = state;
    }
    
    public void rememberInitialVolume(int volumeInterval) {
    	initialVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    	if (volumeInterval == 0)
    		return;
    	adjust = initialVolume / volumeInterval;
    	currentVolume = initialVolume;
    }
    
    public void setInitialVolume() {
    	audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, initialVolume, 0);
    }
    
    public void quietVolume() {
    	currentVolume -= adjust;
    	audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) currentVolume, 0);
    }
    
    public void setLooping(boolean looping) {
		this.looping = looping;
 	}

}
