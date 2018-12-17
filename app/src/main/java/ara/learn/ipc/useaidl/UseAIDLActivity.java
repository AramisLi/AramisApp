package ara.learn.ipc.useaidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aramis.aramisapp.R;

import java.util.List;

/**
 * Created by Aramis
 * Date:2018/12/11
 * Description:
 */
public class UseAIDLActivity extends AppCompatActivity {
    private static final String TAG = "UseAIDLActivity";
    private TextView text_filecache;

    private Button btn_action;

    private IBookManager iBookManager;

    private int index = 2;

    private IOnNewBookArrivedListener onNewBookArrivedListener;

    private int pid = Process.myPid();


    private Handler newBookArrivedHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            try {
                Log.e(TAG, "onCreate pid:" + pid);
                showBookList(iBookManager.getBookList());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return false;
        }
    });

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBookManager = IBookManager.Stub.asInterface(service);

            try {
                //添加图书
                Book myBook = new Book(3, "来吧");
                iBookManager.addBook(myBook);

                //获取图书列表
                List<Book> list = iBookManager.getBookList();

                showBookList(list);
                iBookManager.registerListener(onNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iBookManager = null;
            toast("onServiceDisconnected");
        }

        @Override
        public void onBindingDied(ComponentName name) {
            iBookManager = null;
            toast("onBindingDied");
        }

        @Override
        public void onNullBinding(ComponentName name) {
            iBookManager = null;
            toast("onNullBinding");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filecache);
        text_filecache = findViewById(R.id.text_filecache);
        btn_action = findViewById(R.id.btn_action);

        btn_action.setText("添加图书");
        btn_action.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (iBookManager != null) {
                    index++;
                    try {
                        iBookManager.addBook(new Book(index, "gogo" + index));
                        showBookList(iBookManager.getBookList());
                        toast("添加图书成功");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        toast("添加图书失败");
                    }
                }
            }
        });

        onNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {

            @Override
            public void onNewBookArrived(Book book) throws RemoteException {
                //这里面的代码是运行在Binder的线程池中的，所以不能直接修改view
//                showBookList(iBookManager.getBookList());
                newBookArrivedHandler.sendEmptyMessage(0);
            }
        };

        Intent intent = new Intent(this, UseAIDLService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }

    //显示图书列表
    private void showBookList(List<Book> list) {
        if (list != null && list.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Book b : list) {
                stringBuilder.append("bookId:").append(b.getBookId()).append(",bookName:").append(b.getBookName()).append("\n");
            }
            text_filecache.setText(stringBuilder.toString());
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (iBookManager != null && iBookManager.asBinder().isBinderAlive()) {
            try {
                iBookManager.unregisterListener(onNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(connection);
        super.onDestroy();

    }
}
