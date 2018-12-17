package ara.learn.ipc.usecontentp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.aramis.aramisapp.R;

/**
 * Created by Aramis
 * Date:2018/12/11
 * Description: 使用ContentProvider实现IPC
 */
public class ContentPSecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filecache);

        TextView text_filecache=findViewById(R.id.text_filecache);
        text_filecache.setText("需要权限的Activityl");

        int checked = checkCallingOrSelfPermission("ara.learn.BookProvider");
        if (checked==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "有权限", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "无权限", Toast.LENGTH_SHORT).show();
        }
    }
}
