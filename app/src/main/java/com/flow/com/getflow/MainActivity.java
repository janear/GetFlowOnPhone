package com.flow.com.getflow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity  implements View.OnClickListener {


    private static final String DYNAMICACTION="com.janear.dynamic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService = (Button) findViewById(R.id.start_service);
        stopService = (Button) findViewById(R.id.stop_service);
        interval=(TextView)findViewById(R.id.time_interval);
        startService.setOnClickListener(this);
        stopService.setOnClickListener(this);
        mListView =(ListView)findViewById(R.id.listView);


//        CustomService myService = new CustomService();
//        myService.setInterval(Long.parseLong(getString(R.id.time_interval)));
//        myService.setPackage_name(getString(R.id.package_name));

    }

    BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DYNAMICACTION)) {
                ArrayList<String> _arrayList = intent.getExtras().getStringArrayList("trafficstr");
                ArrayAdapter listAdapter = new ArrayAdapter<String>(context, R.layout.item,R.id.textview_adapter, _arrayList);
                mListView.setAdapter(listAdapter);
                Log.d("Janear", "接受消息");
            }
        }
    };

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(dynamicReceiver);
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

    private Button startService;

    private Button stopService;

    private TextView interval;

    private TextView package_name;

    private ListView mListView;

    private TextView ListAdapter;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:
                Log.e("janear", "....");
                Intent startIntent = new Intent(this, CustomService.class);
                startService(startIntent);
                // 注册自定义动态广播消息
                IntentFilter filter_dynamic = new IntentFilter();
                filter_dynamic.addAction(DYNAMICACTION);
                registerReceiver(dynamicReceiver, filter_dynamic);
                break;
            case R.id.stop_service:
                Intent stopIntent = new Intent(this, CustomService.class);
                stopService(stopIntent);
                break;
            default:
                break;
        }
    }

}
