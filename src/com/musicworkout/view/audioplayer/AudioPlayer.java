package com.musicworkout.view.audioplayer;

import java.io.FileDescriptor;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.musicworkout.R;
import com.musicworkout.model.Song;
import com.musicworkout.storage.Preferences;
import com.musicworkout.util.ShuffleMode;

public class AudioPlayer extends LinearLayout implements OnClickListener, OnSeekBarChangeListener {
	
	public static final String TAG = AudioPlayer.class.getSimpleName();
	
	public static final int STATE_PLAYING = 0;
	public static final int STATE_PAUSED = 1;
	public static final int STATE_STOPPED = 2;
	
	protected TextView name;
	protected ImageView albumImage;
	protected SeekBar duration;
	protected ImageView previous;
	protected ImageView play;
	protected ImageView next;
	protected ImageView mute;
	protected SeekBar volume;
	
	protected List<Song> songs;
	protected Song currentSong;
	
	protected MediaPlayer player;
	protected AudioManager audioManager;
	
	protected int state;
	protected int minVolume;
	protected int maxVolume;
	protected int currentVolume;
	protected int mutedVolume;
	protected boolean looping;
	protected boolean muted;
	
	public AudioPlayer(Context context) {
		this(context, null);
	}

	public AudioPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		initAudio();
		setState(STATE_STOPPED);
	}
	
	protected void initAudio() {
		audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
		
		maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		minVolume = 0;
		volume.setMax(maxVolume);
		setVolume((int) (maxVolume / 3));
		
		player = new MediaPlayer();
		player.setLooping(false);
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mediaPlayer) {
				if (looping && state == STATE_PLAYING)
					nextSong();
				else
					setState(STATE_STOPPED);
			}
		});
		player.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				loadSongDuration();
				mp.start();
				setState(STATE_PLAYING);
			}
		});
	}
	
	protected void init() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		addView(inflater.inflate(R.layout.audio_player, null), 
			new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		name = (TextView) findViewById(R.id.name);
		albumImage = (ImageView) findViewById(R.id.albumImage);
		duration = (SeekBar) findViewById(R.id.duration);
		duration.setOnSeekBarChangeListener(this);
		
		previous = (ImageView) findViewById(R.id.previous);
		previous.setOnClickListener(this);
		play = (ImageView) findViewById(R.id.play);
		play.setOnClickListener(this);
		next = (ImageView) findViewById(R.id.next);
		next.setOnClickListener(this);
		
		mute = (ImageView) findViewById(R.id.mute);
		mute.setOnClickListener(this);
		volume = (SeekBar) findViewById(R.id.volume);
		volume.setOnSeekBarChangeListener(this);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (seekBar.getId() == R.id.volume) {
			setVolume(progress);
		} else if (seekBar.getId() == R.id.duration) {
			if (fromUser)
	            player.seekTo(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}
	
	public void setSong(Song song) {
		if (song == null)
			return;
		currentSong = song;
		setState(STATE_STOPPED);
		loadSongInfo();
	}
	
	protected void loadSongInfo() {
		int index = songs.indexOf(currentSong) + 1;
		name.setText(index + ". " + currentSong.getName());
		loadAlbumart();
	}
	
	public void loadAlbumart() {
	    try {
        	Cursor albumCursor = ((Activity) getContext()).managedQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, 
        			null, MediaStore.Audio.Albums._ID+ "=" + currentSong.getAlbumId(), null, null);
    		String albumUri = "";
    		if (albumCursor.moveToFirst())
    			albumUri = albumCursor.getString(albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
    		albumCursor.close();

            Uri uri = Uri.parse(albumUri);
            ParcelFileDescriptor pfd = getContext().getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                Bitmap bm = BitmapFactory.decodeFileDescriptor(fd);
                albumImage.setImageBitmap(bm);
            }
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	albumImage.setImageResource(R.drawable.no_album);
	    }
	}
	
	protected void loadSongDuration() {
		int totalDuration = player.getDuration();
		duration.setMax(totalDuration);
		duration.setProgress(0);
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
		} else
			Log.d(TAG, "Fail load previous song: playlist is empty");
	}
	
	public void playSong() {
		if (state == STATE_PAUSED) {
			player.start();
			setState(STATE_PLAYING);
		} else {
			try {
				Uri soundUri = Uri.parse(currentSong.getUri());
				try {
					player.setDataSource(getContext(), soundUri);
				} catch (IllegalStateException e) {
					e.printStackTrace();
					player.reset();
					player.setDataSource(getContext(), soundUri);
				}
				player.prepareAsync();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		setState(STATE_STOPPED);
	    	}
		}
	}
	
	public void pauseSong() {
		if (player != null && player.isPlaying())
			player.pause();
		setState(STATE_PAUSED);
	}
	
	public void stopSong() {
		player.stop();
		setState(STATE_STOPPED);
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
		int shuffleMode = Preferences.getInstance(getContext()).getInt(Preferences.KEY_SHUFFLE_MODE, Preferences.DEFAULT_SHUFFLE_MODE);
		ShuffleMode.shuffle(songs, shuffleMode);
		if (state == STATE_PLAYING)
			stopSong();
		if (songs != null && !songs.isEmpty())
			setSong(songs.get(0));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.previous:
			previousSong();
			break;
		case R.id.play:
			if (state == STATE_PLAYING)
				pauseSong();
			else
				playSong();
			break;
		case R.id.next:
			nextSong();
			break;
		case R.id.mute:
			setMuted(!muted);	
			if (muted) {
				mutedVolume = currentVolume;
				volume.setProgress(0);
			} else {
				volume.setProgress(mutedVolume);
			}
				
		}
	}
	
	public void onVisible() {
		initAudio();
	}
	
	public void onInvisible() {
		pauseSong();
	}
	
	public void onDestroy() {
		player.release();
		player = null;
		setState(STATE_STOPPED);
	}
	
	public int getState() {
		return state;
	}
	
	public void setLooping(boolean looping) {
		this.looping = looping;
 	}
	
	public boolean isMuted() {
		return muted;
	}
	
	public void setMuted(boolean muted) {
		this.muted = muted;
		mute.setImageResource(muted ? R.drawable.volume_off : R.drawable.volume_on);
	}
	
	protected void setState(int state) {
		this.state = state;
		switch (state) {
		case STATE_STOPPED:
			handler.removeCallbacks(playbackRunnable);
			play.setImageResource(R.drawable.play);
			duration.setProgress(0);
			break;
		case STATE_PLAYING:
			handler.sendEmptyMessage(0); 
			play.setImageResource(R.drawable.pause);
			break;
		case STATE_PAUSED:
			handler.removeCallbacks(playbackRunnable);
			play.setImageResource(R.drawable.play);
			break;
		}
	}
	
	public void setVolume(int value) {
		currentVolume = value;
		volume.setProgress(value);
		if (value == 0)
			setMuted(true);
		else
			setMuted(false);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
	}
	
	protected void updateDuration() {
		duration.setProgress(player.getCurrentPosition());
		if (state == STATE_PLAYING)
			handler.postDelayed(playbackRunnable, 1000);
	}
	
	Runnable playbackRunnable = new Runnable() {
		@Override
		public void run() {
			handler.sendEmptyMessage(0); 
		}
	};
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) { 
            updateDuration();
	    }
	};

}
