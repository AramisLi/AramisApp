package hello.com.aramis.opengl.december.filer;

import android.content.Context;
import android.opengl.GLES20;

import hello.com.aramis.opengl.december.utils.OpenGLUtils;

/**
 * Created by Aramis
 * Date:2018/12/6
 * Description:
 */
public class AbstractFrameFileter extends AbstractFilter {

    //fbo id
    protected int[] mFrameBuffers;
    //fbo 纹理id
    protected int[] mFrameBufferTextures;

    public AbstractFrameFileter(Context context, int vertexShaderId, int fragmentShaderId) {
        super(context, vertexShaderId, fragmentShaderId);
    }

    @Override
    public void onReady(int width, int height) {
        super.onReady(width, height);
        if (mFrameBuffers != null) {
            destroyFrameBuffers();
        }
        //1. 创建FBO（缓存）
        mFrameBuffers = new int[1];
        GLES20.glGenFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);
        //2. 创建属于fbo的纹理
        mFrameBufferTextures = new int[1];
//        GLES20.glGenTextures(mFrameBufferTextures.length, mFrameBufferTextures, 0);
        //3. 配置纹理
        OpenGLUtils.glGenTextures(mFrameBufferTextures);

        //4. 让fbo与创建的纹理发生关系
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mOutputWidth, mOutputHeight,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        //5. 让fbo与纹理绑定起来，后续的操作就是在操作fbo与这个纹理上
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);
        //6. 解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);


    }

    public void destroyFrameBuffers() {
        //删除fbo的纹理
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(1, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }
        //删除fbo
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
    }

    @Override
    public void release() {
        super.release();
        destroyFrameBuffers();
    }
}
