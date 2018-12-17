package hello.com.aramis.opengl.december.views;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

/**
 * Created by Aramis
 * Date:2018/12/5
 * Description:
 */
public class DouyinView extends GLSurfaceView {
    private DouyinRender douyinRender;
    private Speed mSpeed = Speed.MODE_NORMAL;

    public DouyinView(Context context) {
        super(context);
    }

    public DouyinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        douyinRender = new DouyinRender(this);
        setEGLContextClientVersion(2);
        setRenderer(douyinRender);
        //设置为按需渲染
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public void autoFocus() {
        douyinRender.autoFocus();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        douyinRender.onSurfaceDestroyed();
    }

    public void startRecord() {
        float speed = 1.0f;
        switch (mSpeed) {
            case MODE_EXTRA_SLOW:
                speed = 0.3f;
                break;
            case MODE_SLOW:
                speed = 0.5f;
                break;
            case MODE_FAST:
                speed = 1.5f;
                break;
            case MODE_EXTRA_FAST:
                speed = 3.0f;
                break;
        }
        douyinRender.startRecord(speed);
    }

    public void stopRecord() {
        douyinRender.stopRecord();
    }

    public void setSpeed(Speed speed) {
        this.mSpeed = speed;
    }

    public enum Speed {
        MODE_EXTRA_SLOW, MODE_SLOW, MODE_NORMAL, MODE_FAST, MODE_EXTRA_FAST
    }
}
