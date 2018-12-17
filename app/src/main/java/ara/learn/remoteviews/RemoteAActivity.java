package ara.learn.remoteviews;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.aramis.aramisapp.R;

/**
 * Created by Aramis
 * Date:2018/12/17
 * Description:
 */
public class RemoteAActivity extends AppCompatActivity {

    private LinearLayout mRemoteViewsContent;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            RemoteViews remoteViews = intent.getParcelableExtra("EXTRA_REMOTE_VIEWS");
            if (remoteViews != null) {
                updateUI(remoteViews);
            }
        }
    };

    private void updateUI(RemoteViews remoteViews) {
        View view = remoteViews.apply(this, mRemoteViewsContent);
        mRemoteViewsContent.addView(view);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_a);

        initView();
    }

    private void initView() {
        mRemoteViewsContent = findViewById(R.id.activity_remote_a_root);
        IntentFilter filter = new IntentFilter("REMOTE_ACTION");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
