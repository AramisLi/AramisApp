package ara.learn.ipc.usecontentp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.aramis.aramisapp.R;

/**
 * Created by Aramis
 * Date:2018/12/11
 * Description: 使用ContentProvider实现IPC
 */
public class ContentPActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filecache);

        Uri uri = Uri.parse("content://ara.learn.ipc.usecontentp.BookProvider");
        Cursor cursor;
        cursor = getContentResolver().query(uri, null, null, null, null, null);
        cursor = getContentResolver().query(uri, null, null, null, null, null);
        cursor = getContentResolver().query(uri, null, null, null, null, null);
        if (cursor != null) {
            cursor.close();
        }

        Button btn_action = findViewById(R.id.btn_action);
        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ContentPActivity.this, ContentPSecondActivity.class);
//                startActivity(intent);
            }
        });
    }
}
