package com.musicworkout.screen;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.musicworkout.R;

public class MainScreen extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        
        initTabs();
    }

    private void initTabs() {
    	setTitle(R.string.workouts);
    	
//    	Resources res = getResources();
    	TabHost tabHost = getTabHost();
    	
    	Intent workoutsIntent = new Intent(this, WorkoutsScreen.class);
    	TabSpec tabSpecCategories = tabHost
    			.newTabSpec(getString(R.string.workouts))
    			.setIndicator(getString(R.string.workouts), null)
//    					res.getDrawable(R.drawable.tab_categories))
    			.setContent(workoutsIntent);
    	
    	Intent musicIntent = new Intent(this, MusicScreen.class);
    	TabSpec tabSpecAlphabetical = tabHost
    			.newTabSpec(getString(R.string.music))
    			.setIndicator(getString(R.string.music), null)
//    					res.getDrawable(R.drawable.tab_alphabetical))
    			.setContent(musicIntent);
    	
    	Intent aboutIntent = new Intent(this, AboutScreen.class);
    	TabSpec tabSpecRandom = tabHost
    			.newTabSpec(getString(R.string.about))
    			.setIndicator(getString(R.string.about), null)
//    					res.getDrawable(R.drawable.tab_random))
    			.setContent(aboutIntent);
    	
    	
    	tabHost.addTab(tabSpecCategories);
    	tabHost.addTab(tabSpecAlphabetical);
    	tabHost.addTab(tabSpecRandom);
    	
    	tabHost.setCurrentTab(0);
    }
}
