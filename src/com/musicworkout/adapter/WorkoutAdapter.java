package com.musicworkout.adapter;

import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.musicworkout.R;
import com.musicworkout.database.DatabaseManager;
import com.musicworkout.model.Workout;

public class WorkoutAdapter extends BaseAdapter {
	
	protected LayoutInflater inflater;
	protected List<Workout> workouts;
	
	public WorkoutAdapter(Context context, List<Workout> workouts) {
		inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		this.workouts = (workouts != null) ? workouts : new LinkedList<Workout>();
	}

	public int getCount() {
		return workouts.size();
	}

	public Workout getItem(int position) {
		return workouts.get(position);
	}

	public long getItemId(int position) {
		return workouts.get(position).getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.workout_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.enabled = (CheckBox) convertView.findViewById(R.id.enabled);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final Workout workout = workouts.get(position);
		holder.name.setText(workout.getName());
		holder.enabled.setChecked(workout.isEnabled());
		holder.enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				workout.setEnabled(isChecked);
				DatabaseManager.getInstance(buttonView.getContext()).updateWorkout(workout);
			}
		});
		
		return convertView;
	}
	
	public void setWorkouts(List<Workout> workouts) {
		this.workouts = (workouts != null) ? workouts : new LinkedList<Workout>();
	}
	
	class ViewHolder {
		TextView name;
		CheckBox enabled;
	}

}