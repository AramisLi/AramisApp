package hello.com.aramis.opengl.december;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import hello.com.aramis.opengl.R;
import hello.com.aramis.opengl.december.utils.CameraHelper;

/**
 * Created by Aramis
 * Date:2018/12/5
 * Description:
 */
public class DecemberOpenGLActivityJ extends AppCompatActivity {
    private final String TAG="==DecemberOpenGLAct";
//    private TextView text_record,text_speed_0,text_speed_0,text_speed_0

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_december_main);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            Log.e(TAG,"无权限");
            ActivityCompat.requestPermissions(this, permissions, 1001);
        } else {
            Log.e(TAG,"有权限");
//            cameraHelper.startPreview()
        }

        initView();
    }

    private void initView() {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
