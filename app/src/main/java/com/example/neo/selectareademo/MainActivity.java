package com.example.neo.selectareademo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;
    private SelectAreaPopupWindow popupWindow;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 500:
                    popupWindow.dismiss();

                    Bundle bundle = msg.getData();
                    if(bundle != null){
                        Map<String,String> maps1 = new HashMap<>();
                        maps1.put("province",bundle.getString("provice"));
                        maps1.put("city",bundle.getString("city"));
                        maps1.put("area",bundle.getString("district"));

                        //获取到地址
                        textView.setText(bundle.getString("provice")+bundle.getString("city")+bundle.getString("district"));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow = new SelectAreaPopupWindow(MainActivity.this, mHandler);
                popupWindow.showAtLocation(MainActivity.this.findViewById(R.id.button), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
    }
}
