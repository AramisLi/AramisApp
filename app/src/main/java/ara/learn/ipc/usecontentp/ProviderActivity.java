package ara.learn.ipc.usecontentp;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aramis.aramisapp.R;

import java.util.ArrayList;
import java.util.List;

import ara.learn.ipc.useaidl.Book;

/**
 * Created by Aramis
 * Date:2018/12/12
 * Description:
 */
public class ProviderActivity extends AppCompatActivity {
    private Handler handler;

    private List<Book> bookList = new ArrayList<>();
    private TextView text_filecache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filecache);

        text_filecache = findViewById(R.id.text_filecache);

        findViewById(R.id.btn_action).setVisibility(View.GONE);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Toast.makeText(ProviderActivity.this, "从ContentProvider获取数据成功", Toast.LENGTH_SHORT).show();
                StringBuilder builder = new StringBuilder();
                for (Book book : bookList) {
                    builder.append("BookId:").append(book.getBookId()).append(",BookName:").append(book.getBookName()).append("\n");
                }
                text_filecache.setText(builder.toString());
                return false;
            }
        });

        new Thread(getHandlerRunnable()).start();
    }

    private Runnable getHandlerRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                Uri bookUri = Uri.parse("content://ara.learn.ipc.usecontentp.BookProvider/book");
                ContentValues contentValues = new ContentValues();
                contentValues.put("_id", 6);
                contentValues.put("name", "程序设计的艺术");
                getContentResolver().insert(bookUri, contentValues);
                Cursor bookCursor = getContentResolver().query(bookUri, new String[]{"_id", "name"}, null, null, null);
                if (bookCursor != null) {
                    bookList.clear();
                    while (bookCursor.moveToNext()) {
                        Book book = new Book(bookCursor.getInt(0), bookCursor.getString(1));
                        bookList.add(book);
                    }
                    bookCursor.close();
                    handler.sendEmptyMessage(0);
                }
            }
        };
    }
}
