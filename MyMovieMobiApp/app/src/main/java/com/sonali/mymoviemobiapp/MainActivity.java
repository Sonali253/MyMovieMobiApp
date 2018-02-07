package com.sonali.mymoviemobiapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.sonali.mymoviemobiapp.fragment.MovieListFragment;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_FRAGMENT_NAME = "fragment_name";
    public static final String TAG = "@MoviesApp";
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        runFragmentTransaction(R.id.frameMainContainer, MovieListFragment.newInstance());
    }

    public void snack(View rootView, String message) {
        Snackbar snackBar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
        snackBar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.snack));
        snackBar.show();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        removeFragmentFromBackStack();
    }

    private void removeFragmentFromBackStack() {
        if (getSupportFragmentManager()
                .getBackStackEntryAt(getSupportFragmentManager()
                        .getBackStackEntryCount() - 1)
                .getName() != null) {
            if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().
                    getBackStackEntryCount() - 1).getName().equals("MovieListFragment")) {
                finish();
            } else {
                super.onBackPressed();
            }
        } else {
            runFragmentTransaction(R.id.frameMainContainer, MovieListFragment.newInstance());
        }
    }

    public final Fragment runFragmentTransaction(Integer containerId, Fragment fragment) {
        final String backStateName = fragment.getArguments().getString(KEY_FRAGMENT_NAME);
        addToBackStackFragment(containerId, fragment, backStateName);
        return fragment;
    }

    public final void addToBackStackFragment(Integer containerId, Fragment fragment, String backStateName) {
        FragmentTransaction txn = getSupportFragmentManager().beginTransaction();
        txn.replace(containerId, fragment, backStateName);
        txn.addToBackStack(backStateName);
        txn.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        menu.findItem(R.id.action_settings).setVisible(true);
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
