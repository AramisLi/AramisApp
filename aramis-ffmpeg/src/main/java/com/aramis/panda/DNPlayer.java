package com.aramis.panda;

import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Aramis
 * Date:2018/11/13
 * Description:
 */
public class DNPlayer implements SurfaceHolder.Callback {
    static {
        System.loadLibrary("native-lib");
    }

    private String dataSource;
    private SurfaceHolder holder;
    private OnPreparedListener onPreparedListener;
    private OnErrorListener onErrorListener;

    /**
     * 让使用者设置播放的文件或者直播地址
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void start() {
        native_start();
    }

    public void stop() {
        native_stop();
    }

    public void release() {
        holder.removeCallback(this);
        native_release();
    }

    /**
     * 准备好播放视频
     */
    public void prepare() {
        native_prepare(this.dataSource);
    }

    //当发生错误是，c++调用此函数
    public void onError(int errorCode) {
        if (onErrorListener != null) {
            onErrorListener.onError(errorCode);
        }
    }

    /**
     * 准备好了
     */
    public void onPrepared() {
        if (onPreparedListener != null) {
            onPreparedListener.onPrepared();
        }
    }

    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        this.onPreparedListener = onPreparedListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        if (null != holder) {
            holder.removeCallback(this);
            holder = null;
        }
        holder = surfaceView.getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    //横竖屏切换/按home键/退出应用 会调用这个函数
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        native_setSurface(holder.getSurface());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    interface OnPreparedListener {
        void onPrepared();
    }

    interface OnErrorListener {
        void onError(int errorCode);
    }

    //Native
    native void native_prepare(String dataSource);

    native void native_start();

    native void native_setSurface(Surface surface);

    native void native_stop();

    native void native_release();

}
















