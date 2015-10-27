package net.discoveringpossibilities.attendancesharp.helpers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import net.discoveringpossibilities.attendancesharp.R;

public abstract class ToolbarActivity extends AppCompatActivity {

    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mToolbar.setNavigationIcon(R.drawable.ic_drawer);
    }

    protected abstract int getLayoutResource();

    protected void setActionBarIcon(int navigationIcon) {
    	mToolbar.setNavigationIcon(navigationIcon);
    }
}