package ara.learn.remoteviews

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.widget.RemoteViews
import com.aramis.aramisapp.R
import kotlinx.android.synthetic.main.activity_remote_view.*

/**
 *Created by Aramis
 *Date:2018/12/14
 *Description:
 */
class RemoteViewsActivity : AppCompatActivity() {
    private var channelId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote_view)

        text_send_notification.setOnClickListener {
            channelId++
            sendNotification()
        }

        text_send_appwidget.setOnClickListener {
            val intent=Intent()
            intent.action=MyAppWidgetProvider.CLICK_ACTION
            sendBroadcast(intent)
        }
    }

    private fun sendNotification() {
        val builder = NotificationCompat.Builder(this, channelId.toString())
        builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setTicker("我是大帅哥")
                .setContentText("我怎么这么好看")
                .setContentTitle("Title")
                .setDefaults(Notification.DEFAULT_SOUND)

        if (channelId % 2 == 0) {
            val remoteView = RemoteViews(packageName, R.layout.notifi_remote)
            //要更新remoteView中view的信息，findViewById不能用，必须使用RemoteViews提供的各种set方法，因为RemoteViews是跨进程的
            remoteView.setTextViewText(R.id.text_text1,"我是text1")
            builder.setCustomContentView(remoteView)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(channelId, builder.build())
    }
}