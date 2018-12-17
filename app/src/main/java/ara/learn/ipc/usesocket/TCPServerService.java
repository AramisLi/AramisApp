package ara.learn.ipc.usesocket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.print.PrintAttributes;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by Aramis
 * Date:2018/12/12
 * Description:
 */
public class TCPServerService extends Service {
    private boolean mIsServiceDestoryed = false;

    private String[] mDefinedMessages = new String[]{
            "你好啊，哈哈", "你叫什么名字啊？", "你是男生还是女生啊？", "你多大啦？", "今天北京好冷", "奶茶真好喝"
    };

    @Override
    public void onCreate() {
        new Thread(new TcpServer()).start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mIsServiceDestoryed = true;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class TcpServer implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket = null;

            try {
                //监听本地8688端口
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                System.err.print("establish tcp server failed,port 8688");
                e.printStackTrace();
                return;
            }

            while (!mIsServiceDestoryed) {
                try {
                    final Socket client = serverSocket.accept();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            responseClient(client);
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void responseClient(Socket client) {
        //用于接受客户端消息
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
            out.print("欢迎来到聊天室");
            while (!mIsServiceDestoryed) {
                String str = in.readLine();
                System.out.println("msg from client:" + str);
                if (str == null) {
                    break;
                }
                int i = new Random().nextInt(mDefinedMessages.length);
                String msg = mDefinedMessages[i];
                out.println("send:" + msg);
                System.out.println("send:" + msg);
            }
            System.out.println("server quit.");
            out.close();
            in.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
