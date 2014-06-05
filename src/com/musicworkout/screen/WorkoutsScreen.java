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

import com.musicworkout.R;
import com.musicworkout.adapter.WorkoutAdapter;
import com.musicworkout.database.DatabaseHelper;
import com.musicworkout.model.Workout;

public class WorkoutsScreen extends BaseScreen implements OnClickListener, OnItemClickListener {
	
	public static final int ADD_WORKOUT_REQUEST_CODE = 0;
	public static final int EDIT_WORKOUT_REQUEST_CODE = 1;
	
	protected ListView list;
	protected Button add;
	
	protected WorkoutAdapter adapter;
	protected List<Workout> workouts;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workouts_screen);
		setTitle(R.string.workouts);
		workouts = dbManager.getWorkouts();
		adapter = new WorkoutAdapter(this, workouts);
		initializeViews();
	}
	
	protected void initializeViews() {
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		registerForContextMenu(list);
		
		add = (Button) findViewById(R.id.add);
		add.setOnClickListener(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (resultCode == RESULT_OK && (requestCode == ADD_WORKOUT_REQUEST_CODE || requestCode == EDIT_WORKOUT_REQUEST_CODE)) {
        	workouts = dbManager.getWorkouts();
    		adapter.setWorkouts(workouts);
    		adapter.notifyDataSetChanged();
        }
    }
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.add) {
			Intent intent = new Intent(this, EditWorkoutScreen.class);
			startActivityForResult(intent, ADD_WORKOUT_REQUEST_CODE);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			Workout workout = adapter.getItem(info.position);
			menu.setHeaderTitle(workout.getName());
			String[] menuItems = {getString(R.string.edit), getString(R.string.delete)};
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Workout workout = adapter.getItem(info.position);
		int menuItemIndex = item.getItemId();
		switch (menuItemIndex) {
		case 0:
			Intent intent = new Intent(this, EditWorkoutScreen.class);
			intent.putExtra(DatabaseHelper.FIELD_ID, workout.getId());
			startActivityForResult(intent, EDIT_WORKOUT_REQUEST_CODE);
			break;
		case 1:
			dbManager.deleteWorkout(workout);
			workouts = dbManager.getWorkouts();
			adapter.setWorkouts(workouts);
			adapter.notifyDataSetChanged();
			break;
		}
		return true;
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int workoutId = adapter.getItem(position).getId();
		Intent intent = new Intent(this, WorkoutScreen.class);
		intent.putExtra(DatabaseHelper.FIELD_ID, workoutId);
		startActivity(intent);
	}

}
