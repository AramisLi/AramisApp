package ara.learn.ipc.binderpool;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Aramis
 * Date:2018/12/12
 * Description:
 */
public class BinderPoolService extends Service {

    private static final String TAG = "BinderPoolService";
    private int pid=Process.myPid();

    private Binder mBinderPool = new BinderPool.BinderPoolImpl();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinderPool;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "BinderPoolService 启动了 pid:"+pid);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "BinderPoolService 销毁了 pid:"+pid);
    }
}
