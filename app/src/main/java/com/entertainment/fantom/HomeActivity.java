package com.entertainment.fantom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.FrameLayout;

import com.entertainment.fantom.fragment.HomeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    @BindView(R.id.home_fragment)
    FrameLayout homeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        // Enabling Up / Back navigation
      //  getActionBar().setDisplayHomeAsUpEnabled(true);
        loadHomeFragment();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        ActionBar actionBar = getActionBar();

        Log.d(TAG, "action bar, " + actionBar);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);

    }

    private void loadHomeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment =  new HomeFragment();
        String tag = fragment.getClass().getName();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.home_fragment, fragment);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

         Log.d(TAG, "count , " + count + " fragment name: ");
        if (count == 1 ) {
          // super.onBackPressed();
           finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }
}
