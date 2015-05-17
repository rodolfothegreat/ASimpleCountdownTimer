package com.manzana.rde.asimplecountdowntimer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class TimerMainActivity extends ActionBarActivity {

    Button btn1Min;
    Button btn10Min;
    Button btn1Sec;
    Button btn10Sec;
    Button btn1Hour;
    Button btn10Hour;
    Button btnStart;
    Button btnReset;
    TextView tvTime;
    long initTime = 0;
    long interval = 0;
    boolean amRunning = false;
    IntentFilter filter;

    private MyRequestReceiver receiver = null;
    private String chosenRingtone = "";


    private String convertTimetoStr(long atime) {
        int secs = (int) (atime / 1000);
        int mins = secs / 60;
        int hours = mins / 60;
        // hours = hours % 60;
        mins = mins % 60;
        secs = secs % 60;
        int msecs = (int)atime % 1000;
        int csecs = msecs / 10;
        return String.format("%02d:%02d:%02d", hours, mins, secs);
    }

    private long convertStrToTime(String atime) {
        String bigtime = atime;
        long aresult = 0;
        int apos =  atime.indexOf(':');
        if (apos == -1) {
            return aresult;
        }
        String asuby = bigtime.substring(0, apos);
        aresult = Integer.parseInt(asuby) * 3600;
        bigtime = bigtime.substring(apos + 1);
        apos = bigtime.indexOf(':');
        if (apos == -1) {
            return aresult;
        }
        asuby = bigtime.substring(0, apos);
        aresult = aresult + Integer.parseInt(asuby) * 60;
        bigtime = bigtime.substring(apos + 1);
        aresult = aresult + Integer.parseInt(bigtime);

        return aresult * 1000;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn10Min = (Button) findViewById(R.id.btn10Min);
        btn1Min = (Button) findViewById(R.id.btn1Min);
        btn10Sec = (Button) findViewById(R.id.btn10Sec);
        btn1Sec = (Button) findViewById(R.id.btn1Sec);

        btn10Hour = (Button) findViewById(R.id.btn10Hour);
        btn1Hour = (Button) findViewById(R.id.btn1Hour);

        tvTime = (TextView) findViewById(R.id.tvTime);

        btn10Min.setOnClickListener(handleClick);
        btn1Min.setOnClickListener(handleClick);
        btn10Sec.setOnClickListener(handleClick);
        btn1Sec.setOnClickListener(handleClick);

        btn10Hour.setOnClickListener(handleClick);
        btn1Hour.setOnClickListener(handleClick);

        btnReset = (Button) findViewById(R.id.btnReset);
        btnStart = (Button) findViewById(R.id.btnStart);


        correctWidth(tvTime, 0.81);
    }

    private View.OnClickListener handleClick = new View.OnClickListener(){
        public void onClick(View arg0) {
             int anIndex = 0;
            String atime = tvTime.getText().toString();
            if (atime.length() != 8 )
                return;
            Button btn = (Button)arg0;
        //    tvTime.setText(convertTimetoStr(ltime));
            //Assuming Format is 00:00:00
            if (btn == btn10Hour)
                anIndex = 0;
            if (btn == btn1Hour)
                anIndex = 1;
            if(btn == btn10Min)
                anIndex = 3;
            if(btn == btn1Min)
                anIndex = 4;
            if (btn == btn10Sec)
                anIndex = 6;
            if (btn == btn1Sec)
                anIndex = 7;
            int anumber = 0;
            String adigit = atime.substring(anIndex, anIndex + 1);
            try {
                anumber = Integer.parseInt(adigit);
            }
            catch(NumberFormatException e) {
                return;
            }
            anumber++;
            if(anumber > 9)
                anumber = 0;

            if(anumber > 5 && (anIndex == 6 || anIndex == 3 ) )
                anumber = 0;

            String aresult = atime.substring(0, anIndex) + Integer.toString(anumber) + atime.substring(anIndex + 1);
            tvTime.setText(aresult);



        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timer_main, menu);
        return true;
    }



    public void startChronometer(View view) {
        Resources res = getResources();
        Button abutton = (Button) view;
        String aCaption = abutton.getText().toString();
        if (aCaption.equalsIgnoreCase(res.getString(R.string.Start ))) {
            btnStart.setText(res.getString(R.string.Stop));
           // btnReset.setText(res.getString(R.string.Cancel));

            btn10Min.setEnabled(false);
            btn1Sec.setEnabled(false);
            btn10Sec.setEnabled(false);
            btn1Min.setEnabled(false);
            btn10Hour.setEnabled(false);
            btn1Hour.setEnabled(false);
            String atime = tvTime.getText().toString() ;

            this.interval = convertStrToTime(atime);
            this.initTime = SystemClock.elapsedRealtime();
            Intent msgIntent = new Intent(TimerMainActivity.this, TimerService.class);
            msgIntent.putExtra(TimerService.INTERVAL, this.interval);
            startService(msgIntent);
            this.amRunning = true;
            btnReset.setEnabled(false);
        } else if (aCaption.equalsIgnoreCase(res.getString(R.string.Stop )))  {
            btnStart.setText(res.getString(R.string.Start));
            btn10Min.setEnabled(true);
            btn1Sec.setEnabled(true);
            btn10Sec.setEnabled(true);
            btn1Min.setEnabled(true);
            btn10Hour.setEnabled(true);
            btn1Hour.setEnabled(true);

            stopService(new Intent(this, TimerService.class));
            this.amRunning = false;
            btnReset.setEnabled(true);

            stopService(new Intent(this, TimerService.class));
        } else if (aCaption.equalsIgnoreCase(res.getString(R.string.Resume )))  {
            btnStart.setText(res.getString(R.string.Pause));

            this.interval = convertStrToTime(tvTime.getText().toString());
            this.initTime = SystemClock.elapsedRealtime();
            Intent msgIntent = new Intent(TimerMainActivity.this, TimerService.class);
            msgIntent.putExtra(TimerService.INTERVAL, this.interval);
            startService(msgIntent);
            this.amRunning = true;


        }




    }


    public void stopChronometer(View view) {
        btn10Min.setEnabled(true);
        btn1Sec.setEnabled(true);
        btn10Sec.setEnabled(true);
        btn1Min.setEnabled(true);
        btn10Hour.setEnabled(true);
        btn1Hour.setEnabled(true);
        Resources res = getResources();
        Button abutton = (Button) view;
        String aCaption = abutton.getText().toString();
        btnStart.setText(res.getString(R.string.Start));
        this.amRunning = false;
        initTime = 0;
        interval = 0;
            //abutton.setText(res.getString(R.string.Reset));
        stopService(new Intent(this, TimerService.class));
        Intent msgIntent = new Intent(TimerMainActivity.this, TimerService.class);
        msgIntent.putExtra(TimerService.INTERVAL, 0L);
        startService(msgIntent);

        tvTime.setText("00:00:00");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {


            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");

            if (chosenRingtone.isEmpty())
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
            else
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(chosenRingtone));
            this.startActivityForResult(intent, 5);

            return true;
        }

        if(id == R.id.action_about ) {
            AboutDialog about = new AboutDialog(this);
            about.setTitle("About this app");
            about.show();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                this.chosenRingtone = uri.toString();
            }
            else
            {
                this.chosenRingtone = "";
            }


            SharedPreferences sharedPreferences = this.getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("chosenRingtone", this.chosenRingtone);
            editor.commit();


        }

        if ((this.initTime + this.interval < SystemClock.elapsedRealtime() ) && (amRunning)) {
            amRunning = false;
            this.btnStart.setText("Start");
            this.btnReset.setText("Reset");
            tvTime.setText("00:00:00");
        }
        btn10Min.setEnabled(!amRunning);
        btn1Min.setEnabled(!amRunning);
        btn1Sec.setEnabled(!amRunning);
        btn10Sec.setEnabled(!amRunning);
        btnReset.setEnabled(!amRunning);
        btn10Hour.setEnabled(!amRunning);
        btn1Hour.setEnabled(!amRunning);

    }

    private void getAll() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        this.chosenRingtone = sharedPreferences.getString("chosenRingtone", "");
        this.initTime = sharedPreferences.getLong("initTime", 0);
        this.interval = sharedPreferences.getLong("interval", 0);
        this.amRunning = sharedPreferences.getBoolean("amRunning", false);

        this.btnStart.setText(sharedPreferences.getString("btnStart", "Start"));
        this.btnReset.setText(sharedPreferences.getString("btnReset", "Reset")  );
        if (!amRunning)
            tvTime.setText(sharedPreferences.getString("tvTime","00:00:00" ) );

        if ((this.initTime + this.interval < SystemClock.elapsedRealtime() ) && (amRunning)) {
            amRunning = false;
            this.btnStart.setText("Start");
            this.btnReset.setText("Reset");
            tvTime.setText("00:00:00");
        }
        btn10Min.setEnabled(!amRunning);
        btn1Min.setEnabled(!amRunning);
        btn1Sec.setEnabled(!amRunning);
        btn10Sec.setEnabled(!amRunning);
        btn10Hour.setEnabled(!amRunning);
        btn1Hour.setEnabled(!amRunning);
        this.btnReset.setEnabled(!amRunning);

    }


    private void saveAll() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit().clear();

        editor.putLong("initTime", this.initTime);
        editor.putLong("interval", this.interval);
        editor.putBoolean("amRunning", this.amRunning);
        editor.putString("btnStart", this.btnStart.getText().toString());
        editor.putString("btnReset", this.btnReset.getText().toString());
        editor.putString("chosenRingtone", this.chosenRingtone);
        editor.putString("tvTime", this.tvTime.getText().toString());
        editor.commit();

    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        saveAll();
        try {
            if (receiver != null) {
                unregisterReceiver(receiver);
                receiver = null;
            }
        } catch  (IllegalArgumentException e){

        }catch (Exception e) {
            // TODO: handle exception
        }


    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first


        receiver = new MyRequestReceiver();
        filter = new IntentFilter(TimerService.BROADCAST_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);
        getAll();



    }

    public void correctWidth(TextView textView, double afactor)
    {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float dpHeight = displayMetrics.heightPixels ;
        //float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels ;
        float awidth = dpWidth;
        if (awidth > dpHeight) {
            awidth = awidth / 2;
        }
        int desiredWidth =   (int) (afactor *  awidth);
        Paint paint = new Paint();
        Rect bounds = new Rect();

        paint.setTypeface(textView.getTypeface());
        float textSize = textView.getTextSize();
        paint.setTextSize(textSize);
        String text = textView.getText().toString();
        paint.getTextBounds(text, 0, text.length(), bounds);

        while (bounds.width() > desiredWidth)
        {
            textSize--;
            paint.setTextSize(textSize);
            paint.getTextBounds(text, 0, text.length(), bounds);
        }
        while (bounds.width() < desiredWidth)
        {
            textSize++;
            paint.setTextSize(textSize);
            paint.getTextBounds(text, 0, text.length(), bounds);
        }

        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);


    }



    public class MyRequestReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String updateString = intent.getStringExtra(TimerService.UPDATE_STRING );
            String finalMessage = intent.getStringExtra(TimerService.FINAL_STRING);
            if (updateString != null)
                if (!updateString.equals(tvTime.getText().toString()))
                    tvTime.setText(updateString);

            if (finalMessage != null) {
                amRunning = false;
                btnStart.setText("Start");
                btnReset.setText("Reset");
                tvTime.setText("00:00:00");
                btn10Min.setEnabled(true);
                btn1Sec.setEnabled(true);
                btn10Sec.setEnabled(true);
                btn1Min.setEnabled(true);
                btn10Hour.setEnabled(true);
                btn1Hour.setEnabled(true);
                btnReset.setEnabled(true);
            }



        }


    }

    public class TimeDiscl {
        int secs;
        int mins;
        int hours;

        public TimeDiscl(){

        }

        public void disclose(long msecs) {
            long a_sec = msecs / 1000;
            long a_min = a_sec / 60;
            long a_hour = a_min / 60;

            secs = (int) a_sec % 60;
            mins = (int) a_min % 60;
            hours = (int) a_hour;

        }

        public void disclose(String a_time) {
            disclose(convertStrToTime(a_time));
        }

    }



}
