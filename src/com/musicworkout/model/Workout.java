package com.musicworkout.model;

import org.joda.time.LocalTime;

public class Workout {
	
	public interface OnStateChangedListener {
		void onStateChanged(int oldState, int newState);
	}
	
	public interface OnTypeChangedListener {
		void onTypeChanged(String oldType, String newType);
	}
	
	public static final int STATE_START = 0;
	public static final int STATE_PAUSE = 1;
	public static final int STATE_RESUME = 2;
	public static final int STATE_STOP = 3;
	
	public static final String TYPE_WORK = "work";
	public static final String TYPE_REST = "rest";
	
	protected int id;
	protected String name;
	protected String workoutDuration;
	protected String restDuration;
	protected int repetitions;
	protected boolean enabled;
	
	protected String currentType;
	protected String currentTime;
	protected int currentSet;
	
	protected int state;
	
	protected OnStateChangedListener onStateChangedListener;
	protected OnTypeChangedListener onTypeChangedListener;
	
	public Workout() {}
	
	public Workout(int id, String name, String workoutDuration, String restDuration, int repetitions, boolean enabled) {
		this(id, name, workoutDuration, restDuration, repetitions, enabled, TYPE_WORK, workoutDuration, 0);
	}
	
	public Workout(int id, String name, String workoutDuration, String restDuration, int repetitions, boolean enabled, 
			String currentType, String currentTime, int currentSet) {
		this.id = id;
		this.name = name;
		this.workoutDuration = workoutDuration;
		this.restDuration = restDuration;
		this.repetitions = repetitions;
		this.enabled = enabled;
		this.currentType = currentType;
		this.currentTime = currentTime;
		this.currentSet = currentSet;
		this.state = STATE_START;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getWorkoutDuration() {
		return workoutDuration;
	}
	
	public void setWorkoutDuration(String workoutDuration) {
		this.workoutDuration = workoutDuration;
	}
	
	public String getRestDuration() {
		return restDuration;
	}
	
	public void setRestDuration(String restDuration) {
		this.restDuration = restDuration;
	}
	
	public int getRepetitions() {
		return repetitions;
	}
	
	public void setRepetitions(int repetitions) {
		this.repetitions = repetitions;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public String getCurrentType() {
		return currentType;
	}
	
	public void setCurrentType(String currentType) {
		this.currentType = currentType;
		if (onTypeChangedListener != null)
			onTypeChangedListener.onTypeChanged(currentType.equals(TYPE_WORK) ? TYPE_REST : TYPE_WORK, currentType);
		
	}
	
	public String getCurrentTime() {
		return currentTime;
	}
	
	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}
	
	public int getCurrentSet() {
		return currentSet;
	}
	
	public void setCurrentSet(int currentSet) {
		this.currentSet = currentSet;
	}
	
	public int getState() {
		return state;
	}
	
	public void setState(int state) {
		int oldState = this.state;
		this.state = state;
		if (onStateChangedListener != null)
			onStateChangedListener.onStateChanged(oldState, state);
	}
	
	public String getTotalTime() {
		LocalTime totalTime = new LocalTime(0, 0, 0);
		String timeStr = "00h:00m:00s";
		try {
		    for (int i=0; i<repetitions; i++) {
			    int workMins = Integer.parseInt(workoutDuration.substring(0, 2));
			    int workSecs = Integer.parseInt(workoutDuration.substring(3));
			    int restMins = Integer.parseInt(restDuration.substring(0, 2));
			    int restSecs = Integer.parseInt(restDuration.substring(3));
			    
			    LocalTime workTime = new LocalTime(0, workMins, workSecs);
			    LocalTime restTime = new LocalTime(0, restMins, restSecs);
			    
			    LocalTime time = workTime.plusSeconds(restTime.getSecondOfMinute())
			    		.plusMinutes(restTime.getMinuteOfHour());
			    
			    totalTime = totalTime.plusSeconds(time.getSecondOfMinute())
			    		.plusMinutes(time.getMinuteOfHour())
			    		.plusHours(time.getHourOfDay());
		    }
		    timeStr = String.format("%02dh:%02dm:%02ds", totalTime.getHourOfDay(), totalTime.getMinuteOfHour(), totalTime.getSecondOfMinute());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeStr;
	}
	
	public void count() {
		if (currentTime.equals("00:00")) {
			if (currentType.equals(TYPE_REST))
				currentSet++;
			if (currentSet > repetitions) {
				setState(STATE_STOP);
			} else {
				setCurrentTime(currentType.equals(TYPE_WORK) ? restDuration : workoutDuration);
				setCurrentType(currentType.equals(TYPE_WORK) ? TYPE_REST : TYPE_WORK);
			}
		} else {
			try {
				int mins = Integer.parseInt(currentTime.substring(0, 2));
			    int secs = Integer.parseInt(currentTime.substring(3));
			    LocalTime time = new LocalTime(0, mins, secs);
			    time = time.minusSeconds(1);
			    setCurrentTime(String.format("%02d:%02d", time.getMinuteOfHour(), time.getSecondOfMinute()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getVolumeInterval() {
		int volumeInterval = 0;
		try {
			int mins = Integer.parseInt(restDuration.substring(0, 2));
		    int secs = Integer.parseInt(restDuration.substring(3));
		    volumeInterval = mins * 60 + secs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return volumeInterval;
	}
	
	public void setOnStateChangedListener(OnStateChangedListener l) {
		this.onStateChangedListener = l;
	}
	
	public void setOnTypeChangedListener(OnTypeChangedListener l) {
		this.onTypeChangedListener = l;
	}

}
