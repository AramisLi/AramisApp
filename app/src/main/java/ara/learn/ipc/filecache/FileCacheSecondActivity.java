package ara.learn.ipc.filecache;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.aramis.aramisapp.R;
import com.aramis.aramisapp.config.Configs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import ara.learn.ipc.User;


/**
 * Created by Aramis
 * Date:2018/12/10
 * Description:
 */
public class FileCacheSecondActivity extends AppCompatActivity {

    private TextView text_filecache;

    private Handler handler;

    @Override
    protected void onCreate(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filecache);
        text_filecache = findViewById(R.id.text_filecache);

        handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                User user = (User) msg.obj;
//                String process=
                text_filecache.setText("user.id:" + user.getId() + ",userName:" + user.getUserName());
                return false;
            }
        });

        setTitle("新进程:从缓存文件获取到User对象");

    }

    @Override
    protected void onResume() {
        super.onResume();
        recoverFromFile();
    }

    private void recoverFromFile() {
        new Thread() {
            @Override
            public void run() {
                super.run();

                User user = null;
                String path = Configs.INSTANCE.getBasePath() + File.separator + "ipc" + File.separator + "cache.txt";
                File cachedFile = new File(path);
                if (cachedFile.exists()) {
                    ObjectInputStream objectInputStream = null;

                    try {
                        objectInputStream = new ObjectInputStream(new FileInputStream(cachedFile));
                        user = (User) objectInputStream.readObject();
                        objectInputStream.close();
                        Message message = handler.obtainMessage();
                        message.what = 0;
                        message.obj = user;
                        handler.sendMessage(message);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


}
