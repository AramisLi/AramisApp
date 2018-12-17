package ara.learn.ipc.filecache;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aramis.aramisapp.R;
import com.aramis.aramisapp.config.Configs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import ara.learn.ipc.User;


/**
 * Created by Aramis
 * Date:2018/12/10
 * Description:
 */
public class FileCacheFirstActivity extends AppCompatActivity {
    private TextView text_filecache;
    private Button btn_action;
    private Handler handler;

    @Override
    protected void onCreate(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filecache);
        text_filecache = findViewById(R.id.text_filecache);

        handler = new Handler(getMainLooper(),new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Log.e("======","写完了 handleMessage");
                Toast.makeText(FileCacheFirstActivity.this, "写入完成了", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(FileCacheFirstActivity.this, FileCacheSecondActivity.class));
                return false;
            }
        });

        btn_action = findViewById(R.id.btn_action);
        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistToFile();
            }
        });

        setTitle("将User数据写入到缓存文件");
    }

    private void persistToFile() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                User user = new User(1, "helloworld", false);
                String dirPath = Configs.INSTANCE.getBasePath() + File.separator + "ipc";
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File cachedFile = new File(dirPath + File.separator + "cache.txt");
                ObjectOutputStream objectOutputStream = null;

                try {
                    objectOutputStream = new ObjectOutputStream(new FileOutputStream(cachedFile));
                    objectOutputStream.writeObject(user);
                    objectOutputStream.close();
                    handler.sendEmptyMessage(0);
                    Log.e("======","写完了");
                } catch (IOException  e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
}
