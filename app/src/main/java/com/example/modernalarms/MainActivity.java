package com.example.modernalarms;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    ArrayList<Alarm> alarmList;
    AlarmListAdapter adapter;

    Alarm thisAlarm;
    final Handler handler = new Handler();

    static public MediaPlayer mp;

    Timer timer;
    TimerTask timerTask;

    DatabaseHelper helper;


    //  static public boolean bStopMediaPlayer = false;
    //
    // see https://guides.codepath.com/android/Populating-a-ListView-with-a-CursorAdapter
    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        //    GradientDrawable gradientDrawable = new GradientDrawable();
        //    gradientDrawable.setStroke(10, getResources().getColor(R.color.black));
        //     lv.setBackground(gradientDrawable);



        ListView mListView = findViewById(R.id.alarmList);
        alarmList = new ArrayList<>();
        adapter = new AlarmListAdapter(this, R.layout.adapter_view_layout, alarmList);
        mListView.setAdapter(adapter);


        helper = new DatabaseHelper(this);

        mListView.setOnItemClickListener((adapterView, view, position, l) -> {

            //thisAlarm = alarmList.get(position);
            //     Toast.makeText(this, String.format("Hello - %s",thisAlarm.getDescription()) , Toast.LENGTH_SHORT).show();

            // extract the id field at this position
            View thisChild = ((ViewGroup) view).getChildAt(0);
            String s = (String) ((TextView) thisChild).getText();
            long lId = Long.parseLong(s);
            TextView v = (TextView) findViewById(R.id.description);
            thisAlarm = null;
            for (int i = 0; i < alarmList.size(); i++) {
                if (alarmList.get(i).getId() == lId) {
                    thisAlarm = alarmList.get(i);
                    break;
                }
            }

            if (thisAlarm == null) {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            } else {
                v.setText(thisAlarm.getDescription());
            }

        });

        loadData();


        LayoutInflater inflater = LayoutInflater.from(this);
        View secondLayout = inflater.inflate(R.layout.adapter_view_layout, null);
        View tv = secondLayout.findViewById(R.id.textView_description);
        tv.setVisibility(View.GONE);
        ViewGroup parentView = (ViewGroup) tv.getParent();
        parentView.invalidate();
        parentView.requestLayout();


        startTimer();

    }


    public void loadData() {
        helper.readAll(alarmList, this);
        if (alarmList.isEmpty()) {

            Alarm s = new Alarm(1, Long.MAX_VALUE, "Free Sheba", "bach");
            alarmList.add(s);
            s = new Alarm(2, Long.MAX_VALUE, "Check air frier", "airfryer");
            alarmList.add(s);
            s = new Alarm(3, Long.MAX_VALUE, "Check stove", "sheba");
            alarmList.add(s);
            s = new Alarm(4, Long.MAX_VALUE, "Turn off boiler", "showers");
            alarmList.add(s);
            s = new Alarm(5, Long.MAX_VALUE, "Kettle", "");
            alarmList.add(s);
            s = new Alarm(6, Long.MAX_VALUE, "Pool", "");
            alarmList.add(s);
        }
    }


    public void actionButton(View v) {
        String viewID = getResources().getResourceName(v.getId());

        String[] explode = viewID.split("_");

        String thisKey = explode[1];
        switch (thisKey) {
            case "s": {
                TextView vDesc = (TextView) findViewById(R.id.description);
                if (thisAlarm != null) {
                    thisAlarm.setDescription(vDesc.getText().toString());
                } else {
                    Toast.makeText(this, "Null alarm", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case "n": {
                int now = (int) System.currentTimeMillis();
                long never = now + 1000 * 60 * 60 * 24;
                Alarm s = new Alarm(never, Long.MAX_VALUE, "New alarm", "blockbuster");
                alarmList.add(s);
                break;
            }
            case "x": {
                //      bStopMediaPlayer = true;
                thisAlarm.setStart(Long.MAX_VALUE);
                if (mp != null) {
                    mp.stop();
                    mp.release();
                    mp = null;
                }
                break;
            }
            case "5":
            case "10":
            case "30":
            case "60": {
                if (thisAlarm == null) {
                    Toast.makeText(this, "Select an alarm first", Toast.LENGTH_SHORT).show();
                } else {
                    int key_mins = Integer.parseInt(thisKey);
                    int now = (int) System.currentTimeMillis();
                    //          Alarm s = new Alarm(now, now, now + 1000 * 60 * key_mins, String.format("New alarm %d mins", key_mins));
                    thisAlarm.setStart(now + (long) 1000 * 60 * key_mins);
                    refreshDataset();
                }
                break;
            }
        }

        refreshDataset();
    }

    public void persistData() {
        helper.writeAll(alarmList, this);
    }

    public void onDestroy() {
        stoptimertask();

        persistData();

        adapter.clear();

        super.onDestroy();
    }

    public void refreshDataset() {
        // Need to sort the list lowest at the top
        int c = alarmList.size();
        for (int s = 0; s < c - 2; s++) {
            for (int i = s; i < c - 1; i++) {
                Alarm tempAlarm = alarmList.get(i);
                Alarm tempAlarm2 = alarmList.get(i + 1);
                if (tempAlarm2.getStart() < tempAlarm.getStart()) {
                    alarmList.set(i, tempAlarm2);
                    alarmList.set(i + 1, tempAlarm);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();

        //onResume we start our timer so it can start when the app comes from the background
        //  startTimer();
    }

    public void startTimer() {
        final int period = 60;
        timer = new Timer();

        initializeTimerTask();

        timer.schedule(timerTask, 5, 1000 * period); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {

                handler.post(() -> refreshDataset());
            }
        };

    }

}











