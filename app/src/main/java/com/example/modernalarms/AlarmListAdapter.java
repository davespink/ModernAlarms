package com.example.modernalarms;

import android.content.Context;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


/**
 * Created by User on 3/14/2017.
 * <p>
 * Will need to use this to bring activity to front
 * https://stackoverflow.com/questions/20306350/android-background-activity-bring-itself-to-foreground
 */

public class AlarmListAdapter extends ArrayAdapter<Alarm> {

    //private static final String TAG = "PersonListAdapter";

    private final Context mContext;
    private final int mResource;
    private int lastPosition = -1;


    /**
     * Holds variables in a View
     */
    private static class ViewHolder {

        TextView id;
        //  TextView start;

        TextView description;

        TextView countdown;


    }

    /**
     * Default constructor for the StockListAdapter
     */
    public AlarmListAdapter(Context context, int resource, ArrayList<Alarm> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        //Create the alarm object with the information
        Alarm alarm = new Alarm(getItem(position).getId(), getItem(position).getStart(), getItem(position).getDescription(), getItem(position).getSound());

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();

            holder.id = convertView.findViewById(R.id.textView_id);
            holder.description = convertView.findViewById(R.id.textView_description);;
            holder.countdown = convertView.findViewById(R.id.textView_countdown);

      // do stuff

            result = convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        holder.id.setText(String.format(Locale.ENGLISH, "%d", alarm.getId()));
        holder.description.setText(alarm.getDescription());
        //   holder.countdown.setText(String.format(Locale.ENGLISH,"%d",(alarm.getStop() - alarm.getStart())));

        //     long lStop = alarm.getStop();
        long lNow = System.currentTimeMillis();
        long lCountdown = alarm.getStart() - lNow;
        int countdown = (int) lCountdown / 1000;


        holder.countdown.setText(String.format(Locale.ENGLISH, "%d", Math.round(countdown / 60 + .5)));

        if (alarm.getStart() == Long.MAX_VALUE) {
            convertView.setBackgroundColor(Color.LTGRAY);
            holder.countdown.setText("*");
        } else {
            if (countdown < 0) {
                convertView.setBackgroundColor(Color.RED);

                if (MainActivity.mp == null) {
                    int mpResourceId = mContext.getResources().getIdentifier(alarm.getSound(), "raw", mContext.getPackageName());
                    if (mpResourceId == 0)
                        MainActivity.mp = MediaPlayer.create(mContext, R.raw.blockbuster); //default
                    if (mpResourceId > 0) {
                        MainActivity.mp = MediaPlayer.create(mContext, mpResourceId);
                        MainActivity.mp.start();
                    } else {
                        Toast.makeText(mContext, "failed to find alarm sound", Toast.LENGTH_SHORT);
                    }
                }

            } else convertView.setBackgroundColor(Color.GREEN);
        }


      //  GradientDrawable gradientDrawable = new GradientDrawable();
      //  gradientDrawable.setStroke(10, Color.LTGRAY);
   //     gradientDrawable.setTint(Color.BLUE);

    //    convertView.setBackground(gradientDrawable);

        // View xxx = (View) convertView.findViewById(R.id.lay_id);
        // xxx.setBackgroundColor(Color.BLACK);
        //  xxx.setVisibility(View.GONE);


        return convertView;
    }
}





























