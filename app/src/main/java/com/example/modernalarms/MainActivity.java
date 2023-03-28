package com.example.modernalarms;



import androidx.appcompat.app.AppCompatActivity;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.TextView;

import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    ArrayList<Alarm> alarmList;
    AlarmListAdapter adapter;

    Alarm thisAlarm;

    final Handler handler = new Handler();


    Timer timer;
    TimerTask timerTask;


    //  static public boolean bStopMediaPlayer = false;
    //
    // see https://guides.codepath.com/android/Populating-a-ListView-with-a-CursorAdapter
    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        ListView mListView = findViewById(R.id.alarmList);
        alarmList = new ArrayList<>();

        adapter = new AlarmListAdapter(this, R.layout.adapter_view_layout, alarmList);
        mListView.setAdapter(adapter);

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


        Alarm s = new Alarm(1, Long.MAX_VALUE, "Free Sheba");
        alarmList.add(s);


        s = new Alarm(2, Long.MAX_VALUE, "Check air frier");
        alarmList.add(s);


        s = new Alarm(3, Long.MAX_VALUE, "Turn off boiler");
        alarmList.add(s);


        s = new Alarm(4, Long.MAX_VALUE, "Kettle");
        alarmList.add(s);

        startTimer();

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
                long when = now + 1000 * 60 * 60 * 24;
                Alarm s = new Alarm(now,Long.MAX_VALUE,String.format(Locale.ENGLISH,"New alarm"));
                alarmList.add(s);
                break;
            }
            case "h": {
                //      bStopMediaPlayer = true;
                thisAlarm.setStart(Long.MAX_VALUE);
                break;
            }
            case "5":
            case "10":
            case "30":
            case "60": {
                int key_mins = Integer.parseInt(thisKey);
                int now = (int) System.currentTimeMillis();
                //          Alarm s = new Alarm(now, now, now + 1000 * 60 * key_mins, String.format("New alarm %d mins", key_mins));
                thisAlarm.setStart(now + 1000 * key_mins);
                refreshDataset();
                //   alarmList.add(s);
                break;

            }
        }


        refreshDataset();
    }

    public void onDestroy() {
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
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5, 10000); //
    }

    public static void stoptimertask() {
        //stop the timer, if it's not already null
        // if (timer != null) {
        //    timer.cancel();
        //    timer = null;
        // }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                handler.post(()-> {
                    refreshDataset();
                });
            }
        };


    }


}











