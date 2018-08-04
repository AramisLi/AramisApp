package com.ndkdev.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ndkdev.R;
import com.ndkdev.utils.NDKUtilsJ;

/**
 * Created by Aramis
 * Date:2018/8/1
 * Description:
 */
public class HelloJniActivityJ extends AppCompatActivity {
    private TextView text_jni_result;
    public String testStr="我是大帅哥";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_jni);

        setListener();
    }

    private void setListener() {
        Button btn_load_exception = findViewById(R.id.btn_load_exception);
        text_jni_result = findViewById(R.id.text_jni_result);
        btn_load_exception.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String s = NDKUtilsJ.testException();
                    text_jni_result.setText(s);
                } catch (Exception e) {
                    text_jni_result.setText("获取到异常");
                }

            }
        });
    }
}
