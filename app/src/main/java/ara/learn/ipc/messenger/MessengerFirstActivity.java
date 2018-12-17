package ara.learn.ipc.messenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aramis.aramisapp.R;
import com.aramis.library.utils.LogUtils;

/**
 * Created by Aramis
 * Date:2018/12/11
 * Description: 使用Messenger进行进程间数据传递
 */
public class MessengerFirstActivity extends AppCompatActivity {
    private static final String TAG = "MessengerFirstActivity";
    private TextView text_filecache;

    private Messenger messenger;

    private Messenger replymessenger=new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 2:
                    String data=msg.getData().getString("reply");
                    Log.e(TAG, data);
                    text_filecache.setText(data);
                    break;
            }
            return false;
        }
    }));

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);
            Message msg = Message.obtain(null, 1);
            Bundle data = new Bundle();
            data.putString("msg", "hello this is client.");
            msg.setData(data);

            msg.replyTo=replymessenger;
            Log.e(TAG, "onServiceConnected");
            try {
                Log.e(TAG, "发送数据");
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filecache);
        setTitle("Messenger实现");
        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Button btn_action = findViewById(R.id.btn_action);
        btn_action.setVisibility(View.GONE);
//        btn_action.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        text_filecache=findViewById(R.id.text_filecache);
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }
}
