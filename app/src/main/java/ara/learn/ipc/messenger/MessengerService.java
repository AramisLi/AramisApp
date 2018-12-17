package ara.learn.ipc.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Aramis
 * Date:2018/12/11
 * Description:
 */
public class MessengerService extends Service {
    private static final String TAG = "MessengerService";


    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.e(TAG, "获取到了客户端的数据" + msg.getData().getString("msg"));
                    Toast.makeText(MessengerService.this, msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();

                    Messenger toClientMessenger = msg.replyTo;
                    Message replyMessage = Message.obtain(null, 2);
                    Bundle bundle=new Bundle();
                    bundle.putString("reply","嗯，你的消息我已收到");
                    replyMessage.setData(bundle);
                    try {
                        toClientMessenger.send(replyMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
