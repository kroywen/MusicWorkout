package com.musicworkout.screen;

import java.util.List;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.musicworkout.R;
import com.musicworkout.database.DatabaseHelper;
import com.musicworkout.model.Playlist;
import com.musicworkout.model.Song;
import com.musicworkout.model.Workout;
import com.musicworkout.model.Workout.OnStateChangedListener;
import com.musicworkout.model.Workout.OnTypeChangedListener;
import com.musicworkout.service.MusicService;
import com.musicworkout.storage.Preferences;

public class WorkoutScreen extends BaseScreen implements OnClickListener, OnTypeChangedListener, OnStateChangedListener, ServiceConnection {
	
	public static final int DEVICE_ADMIN_REQUEST_CODE = 0;
	
	public static final int COMMAND_UPDATE = 0;
	
	protected TextView totalTime;
	protected TextView currentType;
	protected TextView currentTime;
	protected TextView timer;
	protected TextView currentSet;
	protected Button startWorkout;
	
	protected int id;
	protected Workout workout;
//	protected static DevicePolicyManager dpm;
//	protected ComponentName devAdminReceiver;
	
	protected MusicService musicService;
	protected boolean musicIsBound;
	protected MediaPlayer mediaPlayer;
	protected boolean stopped; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workout_screen);
//		dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
//		devAdminReceiver = new ComponentName(this, MyAdminReceiver.class);
		doBindService();
		initBell();
		initializeViews();
		getIntentData();
		populateFields();
	}
	
	protected void loadSongs() {
		String playlistName = preferences.getString(Preferences.KEY_PLAYLIST, null);
		if (playlistName == null)
			return;
		if (playlistName.equals(Preferences.DEFAULT_PLAYLIST)) {
			loadDefaultPlaylist(musicService);
		} else {
			Playlist playlist = dbManager.getSelectedPlaylist();
			if (playlist != null) {
				List<Song> songs = dbManager.getSongs(playlist.getId());
				musicService.setSongs(songs);
			}
		}
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_ID)) {
			int id = intent.getIntExtra(DatabaseHelper.FIELD_ID, 0);
			workout = dbManager.getWorkoutById(id);
			workout.setOnTypeChangedListener(this);
			workout.setOnStateChangedListener(this);
		}
	}
	
	protected void initializeViews() {
		totalTime = (TextView) findViewById(R.id.totalTime);
		currentType = (TextView) findViewById(R.id.currentType);
		currentTime = (TextView) findViewById(R.id.currentTime);
		timer = (TextView) findViewById(R.id.timer);
		currentSet = (TextView) findViewById(R.id.currentSet);
		
		startWorkout = (Button) findViewById(R.id.startWorkout);
		startWorkout.setOnClickListener(this);
	}
	
	protected void populateFields() {
		if (workout != null) {
			setTitle(workout.getName());
			totalTime.setText(workout.getTotalTime());
			populateTimerFields();
			updateStartButton();
		}
	}
	
	protected void populateTimerFields() {
		String currentTypeStr = workout.getCurrentType().equals(Workout.TYPE_WORK) ? getString(R.string.work_for) : getString(R.string.rest_for); 
		currentType.setText(currentTypeStr);
		
		String currentTimeStr = workout.getCurrentType().equals(Workout.TYPE_WORK) ? workout.getWorkoutDuration() : workout.getRestDuration();
		currentTime.setText(currentTimeStr);
		
		timer.setText(workout.getCurrentTime());
		
		String sets = String.format(getString(R.string.sets_format), workout.getCurrentSet(), workout.getRepetitions());
		if (workout.getCurrentSet() > workout.getRepetitions())
			sets = getString(R.string.all);
		currentSet.setText(sets);
	}
	
	/*
	protected void lockScreen() {
		if (!dpm.isAdminActive(devAdminReceiver)) {
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, devAdminReceiver);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable admin permissions.");
			startActivityForResult(intent, DEVICE_ADMIN_REQUEST_CODE);
		} else {
			dpm.lockNow();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == DEVICE_ADMIN_REQUEST_CODE) {
			dpm.lockNow();
		}
	}
	*/

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.startWorkout) {
			switch (workout.getState()) {
			case Workout.STATE_STOP:
				workout.setState(Workout.STATE_START);
				break;
			case Workout.STATE_RESUME:
				workout.setState(Workout.STATE_PAUSE);
				break;
			case Workout.STATE_PAUSE:
				workout.setState(Workout.STATE_RESUME);
				break;
			}
		}
	}
	
	protected void updateStartButton() {
		switch (workout.getState()) {
		case Workout.STATE_START:
		case Workout.STATE_STOP:
//			startWorkout.setText(R.string.start_workout);
//			startWorkout.setTextColor(Color.rgb(32, 32, 32));
			startWorkout.setBackgroundResource(R.drawable.btn_start);
			break;
		case Workout.STATE_RESUME:
//			startWorkout.setText(R.string.pause_workout);
//			startWorkout.setTextColor(Color.rgb(30, 20, 8));
			startWorkout.setBackgroundResource(R.drawable.btn_pause);
			break;
		case Workout.STATE_PAUSE:
//			startWorkout.setText(R.string.resume_workout);
//			startWorkout.setTextColor(Color.rgb(255, 255, 255));
			startWorkout.setBackgroundResource(R.drawable.btn_resume);
			break;
		}
	}
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.lock_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.lock:
	            lockScreen();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	*/
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(updateTime);
		doUnbindService();
		mediaPlayer.release();
	}
	
	@Override
	public void onBackPressed() {
		handler.removeCallbacks(updateTime);
		super.onBackPressed();
	}
	
//	public static class MyAdminReceiver extends DeviceAdminReceiver {}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) { 
	        if (msg.what == COMMAND_UPDATE)
	            updateWorkout();
	    }
	};
	
	protected void resumeWorkout() {
		handler.post(updateTime);
	}
	
	protected void pauseWorkout() {
	    handler.removeCallbacks(updateTime);
	}
	
	protected void stopWorkout() {
		handler.removeCallbacks(updateTime);
	}
	
	protected void updateWorkout() {
		workout.count();
		populateTimerFields();
		
		int volumeMode = preferences.getInt(Preferences.KEY_VOLUME_MODE, Preferences.DEFAULT_VOLUME_MODE);
		if (volumeMode == Preferences.VOLUME_MODE_QUIET_DURING_REST && workout.getCurrentType().equals(Workout.TYPE_REST))
			musicService.quietVolume();
		
		if (!stopped)
			handler.postDelayed(updateTime, 1000);
	}
	
	Runnable updateTime = new Runnable() {
		@Override
		public void run() {
			handler.sendEmptyMessage(COMMAND_UPDATE);
		}
	};
	
	protected void playBell() {
		if (mediaPlayer != null) {
			mediaPlayer.seekTo(0);
			mediaPlayer.start();
		}
	}
	
	protected void initBell() {
		mediaPlayer = new MediaPlayer();
		try {
			Uri soundUri = Uri.parse("android.resource://com.musicworkout/raw/bell");
			mediaPlayer.setLooping(false);
			mediaPlayer.setDataSource(this, soundUri);
			final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0)
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			mediaPlayer.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTypeChanged(String oldType, String newType) {
		if (workout.getState() != Workout.STATE_RESUME)
			return;
		int volumeMode = preferences.getInt(Preferences.KEY_VOLUME_MODE, Preferences.DEFAULT_VOLUME_MODE);
		if (volumeMode == Preferences.VOLUME_MODE_BELL_AT_REST) {
			if (newType.equals(Workout.TYPE_REST))
				playBell();
		} else if (volumeMode == Preferences.VOLUME_MODE_PAUSE_AT_REST) {
			if (newType.equals(Workout.TYPE_REST))
				musicService.pauseSong();
			else if (newType.equals(Workout.TYPE_WORK))
				musicService.resumeSong();
		} else if (volumeMode == Preferences.VOLUME_MODE_QUIET_DURING_REST) {
			if (newType.equals(Workout.TYPE_REST)) {
				musicService.rememberInitialVolume(workout.getVolumeInterval());
			} else if (newType.equals(Workout.TYPE_WORK)) {
				musicService.setInitialVolume();
			}
		}
	}

	@Override
	public void onStateChanged(int oldState, int newState) {
		updateStartButton();
		if (newState == Workout.STATE_START) {
			workout.setCurrentSet(0);
			workout.setCurrentTime(workout.getWorkoutDuration());
			workout.setCurrentType(Workout.TYPE_WORK);
			musicService.playSong();
			resumeWorkout();
			stopped = false;
			workout.setState(Workout.STATE_RESUME);
		} else if (newState == Workout.STATE_RESUME && oldState == Workout.STATE_PAUSE) {
			int volumeMode = preferences.getInt(Preferences.KEY_VOLUME_MODE, Preferences.DEFAULT_VOLUME_MODE);
			if (workout.getCurrentType().equals(Workout.TYPE_WORK) ||
					workout.getCurrentType().equals(Workout.TYPE_REST) && 
							(volumeMode == Preferences.VOLUME_MODE_BELL_AT_REST || volumeMode == Preferences.VOLUME_MODE_QUIET_DURING_REST))
				musicService.resumeSong();
			resumeWorkout();
			stopped = false;
		} else if (newState == Workout.STATE_STOP) {
			stopWorkout();
			musicService.stopSong();
			stopped = true;
		} else if (newState == Workout.STATE_PAUSE) {
			musicService.pauseSong();
			pauseWorkout();
			stopped = true;
		}
	}

	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
        musicService = ((MusicService.MusicBinder) service).getService();
        musicService.setLooping(true);
        workout.setState(Workout.STATE_STOP);
        loadSongs();
	}

	@Override
	public void onServiceDisconnected(ComponentName className) {
		musicService = null;
	}
	
	void doBindService() {
	    bindService(new Intent(this, MusicService.class), this, Context.BIND_AUTO_CREATE);
	    musicIsBound = true;
	}

	void doUnbindService() {
	    if (musicIsBound) {
	        unbindService(this);
	        musicIsBound = false;
	    }
	}

}