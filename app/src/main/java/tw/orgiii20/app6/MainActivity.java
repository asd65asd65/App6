package tw.orgiii20.app6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView clock;
    private Button btnL;
    private Button btnR;
    private ListView listLap;

    private boolean isRunning= false;//狀態

    private LinkedList<HashMap<String,String>> linkedList_Data;
    private String[] strings_from = {"title","mes"};
    private int[] ints_to = {R.id.itemTitle,R.id.itemMes};
    private SimpleAdapter adapter;
    private int num=0;

    private Timer timer;
    private int intTime=0;
    private UIHandler uiHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clock = findViewById(R.id.clock);
        btnL = findViewById(R.id.btnL);
        btnR = findViewById(R.id.btnR);
        listLap = findViewById(R.id.listLap);

        uiHandler = new UIHandler();
        uiHandler.sendEmptyMessage(0);
        btnL.setText("Reset");
        btnR.setText("Start");

        initListView();
    }

    private void initListView(){
        listLap = findViewById(R.id.listLap);
        linkedList_Data = new LinkedList<>();//data
        adapter = new SimpleAdapter(this, linkedList_Data, R.layout.list_item, strings_from, ints_to);
        listLap.setAdapter(adapter);
    }

    private class MyTask extends TimerTask{
        @Override
        public void run() {
            if (isRunning){
                intTime++;
                uiHandler.sendEmptyMessage(0);
            }
            Log.i("MyTask", "run: "+intTime);
        }
    }

    private class UIHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            clock.setText(""+toClockString(intTime));
        }
    }

    private String toClockString(int time){
        int hs = time%100, ts = time/100, hh = ts/(60*60), mm = ts/60, ss = ts%60;
        //hh : mm : ss . ts_hs

        return hh+" : "+mm+" : "+ss+" . "+hs;
    }


    public void clickL(View view) {
        Log.i("clickL", "isRunning: "+isRunning);
        if (isRunning){
            doLap();
        }else {
            doReset();
        }

    }

    private void doLap(){
        num++;
        HashMap<String,String> dd = new HashMap<>();
        dd.put(strings_from[0],"NO:"+num+" -> "+toClockString(intTime));
        dd.put(strings_from[1],"Content: "+intTime);
        linkedList_Data.addFirst(dd);
        adapter.notifyDataSetChanged();
    }

    private void doReset(){
        intTime=0;
        num=0;
        linkedList_Data.clear();
        adapter.notifyDataSetChanged();
        uiHandler.sendEmptyMessage(0);
    }

    public void clickR(View view) {
        Log.i("clickR", "isRunning: "+isRunning);
        if (isRunning){
            doStop();
        }else {
            doStart();
        }

        isRunning = !isRunning;

        btnL.setText(isRunning? "Lap":"Reset");
        btnR.setText(isRunning? "Stop":"Start");
    }

    private void doStop(){
        timer.cancel();
        timer.purge();
    }

    private void doStart(){
        timer = new Timer();
        timer.schedule(new MyTask(),0,10);
    }

    @Override
    public void finish(){
        super.finish();
        timer.cancel();
        timer.purge();
    }

}
