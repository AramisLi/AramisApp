package ara.learn.ipc.useaidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Aramis
 * Date:2018/12/11
 * Description:
 */
public class UseAIDLService extends Service {

    private static final String TAG = "UseAIDLService";

    //CopyOnWriteArrayList在内部实现了线程管理。既线程安全的List
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    //因为跨进程会重新创建对象，利用RemoteCallbackList来移除真正的监听对象
    private RemoteCallbackList<IOnNewBookArrivedListener> mListeners = new RemoteCallbackList<>();

    //原子类型的boolean,保证线程安全
    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);

    private int pid = Process.myPid();


    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListeners.register(listener);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListeners.unregister(listener);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate pid:" + pid);
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "iOS"));

        new Thread(new ServiceWorker()).start();


        ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(100);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, arrayBlockingQueue);
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed.set(true);
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    private void onNewBookArrived(Book book) throws RemoteException {
        //在这里，如果客户端的监听方法是一个耗时操作，那么就会倒是服务端无响应。
        //这里可以使用HandlerThread解决这个问题
        mBookList.add(book);
        int n = mListeners.beginBroadcast();
        for (int i = 0; i < n; i++) {
            IOnNewBookArrivedListener broadcastItem = mListeners.getBroadcastItem(i);
            if (broadcastItem != null) {
                broadcastItem.onNewBookArrived(book);
            }
        }
        mListeners.finishBroadcast();
    }

    private class ServiceWorker implements Runnable {

        @Override
        public void run() {
            while (!mIsServiceDestoryed.get()) {
                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                Book newBook = new Book(bookId, "newBook" + bookId);

                try {
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
