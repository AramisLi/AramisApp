package ara.learn.remoteviews;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import com.aramis.aramisapp.R;

/**
 * Created by Aramis
 * Date:2018/12/17
 * Description:
 */
public class RemoteBActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_b);

        initView();

        Button item_broadcast=findViewById(R.id.item_broadcast);
        item_broadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initView() {

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.remote_pass);
        remoteViews.setTextViewText(R.id.text_remote_pass, "我怎么这么好看");
        remoteViews.setImageViewResource(R.id.image_remote_pass, R.drawable.video);
        Intent intent = new Intent(this, RemoteAActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 111, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent openIntent = PendingIntent.getActivity(this, 222, new Intent(this, RemoteAActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.item_holder, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.item_open_activity, openIntent);

        Intent bcIntent=new Intent("REMOTE_ACTION");
        bcIntent.putExtra("EXTRA_REMOTE_VIEWS",remoteViews);
        sendBroadcast(bcIntent);
    }
}
