package com.musicworkout.screen;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.musicworkout.R;
import com.musicworkout.database.DatabaseHelper;
import com.musicworkout.model.Workout;
import com.musicworkout.view.wheel.WheelView;
import com.musicworkout.view.wheel.adapters.NumericWheelAdapter;

public class EditWorkoutScreen extends BaseScreen implements OnClickListener, OnTouchListener {
	
	protected EditText name;
	protected EditText workoutDuration;
	protected EditText restDuration;
	protected EditText repetitions;
	
	protected Button save;
	
	protected Workout workout;
	protected int id;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_workout_screen);
		getIntentData();
		initializeViews();
		populateFields();
	}
	
	protected void getIntentData() {
		Intent intent = getIntent();
		if (intent != null && intent.hasExtra(DatabaseHelper.FIELD_ID)) {
			id = intent.getIntExtra(DatabaseHelper.FIELD_ID, 0);
			if (id != 0)
				workout = dbManager.getWorkoutById(id);
		}
	}
	
	protected void initializeViews() {
		name = (EditText) findViewById(R.id.name);
		
		workoutDuration = (EditText) findViewById(R.id.workout_duration);
		workoutDuration.setOnTouchListener(this);
		
		restDuration = (EditText) findViewById(R.id.rest_duration);
		restDuration.setOnTouchListener(this);
		
		repetitions = (EditText) findViewById(R.id.repetitions);
		repetitions.setOnTouchListener(this);
		
		save = (Button) findViewById(R.id.save);
		save.setOnClickListener(this);
	}
	
	protected void populateFields() {
		if (workout != null) {
			name.setText(workout.getName());
			workoutDuration.setText(workout.getWorkoutDuration());
			restDuration.setText(workout.getRestDuration());
			repetitions.setText(String.valueOf(workout.getRepetitions()));
		}
	}
	
	protected void saveWorkout() {
		if (workout == null)
			workout = new Workout();
		
		workout.setName(name.getText().toString());
		workout.setWorkoutDuration(workoutDuration.getText().toString());
		workout.setRestDuration(restDuration.getText().toString());
		try {
			workout.setRepetitions(Integer.parseInt(repetitions.getText().toString()));
		} catch (Exception e) {
			e.printStackTrace();
			workout.setRepetitions(0);
		}
		if (id != 0)
			dbManager.updateWorkout(workout);
		else
			dbManager.addWorkout(workout);
		setResult(RESULT_OK);
		finish();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			saveWorkout();
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
		switch (v.getId()) {
		case R.id.workout_duration:
			showDurationDialog(R.string.workout_duration, workoutDuration);
			break;
		case R.id.rest_duration:
			showDurationDialog(R.string.rest_duration, restDuration);
			break;
		case R.id.repetitions:
			showRepetitionsDialog();
		default:
			return super.onTouchEvent(event);	
		}
		}
		return true;
	}
	
	protected void showDurationDialog(final int titleResId, final EditText durationView) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.duration_dialog, null);
		
		final WheelView minsWheel = (WheelView) dialogView.findViewById(R.id.minutes);
		NumericWheelAdapter minsAdapter = new NumericWheelAdapter(this, 00, 59, "%02d");
		minsWheel.setViewAdapter(minsAdapter);
		
		final WheelView secsWheel = (WheelView) dialogView.findViewById(R.id.seconds);
		NumericWheelAdapter secsAdapter = new NumericWheelAdapter(this, 00, 59, "%02d");
		secsWheel.setViewAdapter(secsAdapter);
		
		try {
			int min = Integer.parseInt(durationView.getText().toString().substring(0, 2));
			minsWheel.setCurrentItem(min);
			int secs = Integer.parseInt(durationView.getText().toString().substring(3));
			secsWheel.setCurrentItem(secs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
			.setView(dialogView)
			.setTitle(titleResId)
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {	
				public void onClick(DialogInterface dialog, int which) {
					String duration = String.format("%02d:%02d", minsWheel.getCurrentItem(), secsWheel.getCurrentItem());
					durationView.setText(duration);
					dialog.dismiss();
				}
			})
			.create()
			.show();
	}
	
	protected void showRepetitionsDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.repetitions_dialog, null);
		
		final WheelView repetitionsWheel = (WheelView) dialogView.findViewById(R.id.repetitionsWheel);
		NumericWheelAdapter repetitionsAdapter = new NumericWheelAdapter(this, 00, 100);
		repetitionsWheel.setViewAdapter(repetitionsAdapter);
		
		try {
			int sets = Integer.parseInt(repetitions.getText().toString());
			repetitionsWheel.setCurrentItem(sets);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
			.setView(dialogView)
			.setTitle(getString(R.string.repetitions))
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {	
				public void onClick(DialogInterface dialog, int which) {
					repetitions.setText(String.valueOf(repetitionsWheel.getCurrentItem()));
					dialog.dismiss();
				}
			})
			.create()
			.show();
	}

}
