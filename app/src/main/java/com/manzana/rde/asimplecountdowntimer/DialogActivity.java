package com.manzana.rde.asimplecountdowntimer;

/**
 * Created by Rodolfo on 28/04/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.ref.WeakReference;


public class DialogActivity extends AppCompatActivity {
    Uri notification = null;
    public static final int A_DIALOG = 1;
    private Ringtone r;
    private static class MyHandler extends Handler {
        private WeakReference<DialogActivity> myClassWeakReference = null;
        MyHandler( DialogActivity aDialogActivity){
            super();
            myClassWeakReference = new WeakReference<DialogActivity>(aDialogActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == A_DIALOG) {
                if (myClassWeakReference != null) {
                    // Toast.makeText(myClassWeakReference.get().getApplicationContext(), "Time is up!!!", Toast.LENGTH_LONG).show();
                    myClassWeakReference.get().playRing();
                    AlertDialog.Builder builder = new AlertDialog.Builder(myClassWeakReference.get());
                    builder.setMessage("Time is up!!!");
                    //builder.setTitle("Confirmation Dialog");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do something after confirm
                            myClassWeakReference.get().stopRing();
                            myClassWeakReference.get().finish();
                        }
                    });

                    //builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    //    public void onClick(DialogInterface dialog, int which) {
                    //        dialog.cancel();
                    //    }
                    //});

                    builder.create().show();


                }
            }

        }



    }

    MyHandler ahandler = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        SharedPreferences sharedPreferences = getSharedPreferences("my_prefs", Activity.MODE_PRIVATE);
        String chosenRingtone = sharedPreferences.getString("chosenRingtone", "");
        if (!chosenRingtone.isEmpty()) {
            notification = Uri.parse(chosenRingtone);
        }


        r = RingtoneManager.getRingtone(getApplicationContext(), notification);


        ahandler = new MyHandler(this);
        ahandler.sendMessage(ahandler.obtainMessage(A_DIALOG)  );
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dialog, menu);
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

    public void playRing() {
        if ( notification != null)
            r.play();
    }

    public void stopRing() {
        if ( notification != null)
            r.stop();
    }

}


