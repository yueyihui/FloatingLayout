package com.github.lyue.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.lyue.mylibrary.MyView;
import com.github.lyue.mylibrary.MyViewManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // default layout
        MyViewManager.loadMyView(
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                , (ViewGroup) findViewById(R.id.content)
                , true)
                .setMoveBounds((ViewGroup) findViewById(R.id.content))
                .getMyView()
                .findViewById(R.id.my_fab)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "fab click");
            }
        });

        // in this activity's layout
        MyViewManager.findMyView(findViewById(R.id.my_view_cc)).
                setMoveBounds((ViewGroup) findViewById(R.id.content)).
                getMyView().
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v(TAG, "yellow click");
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
