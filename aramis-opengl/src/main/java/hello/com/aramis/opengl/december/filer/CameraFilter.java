package hello.com.aramis.opengl.december.filer;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import hello.com.aramis.opengl.R;

/**
 * Created by Aramis
 * Date:2018/12/5
 * Description:
 */
public class CameraFilter extends AbstractFrameFileter {

    private float[] matrix;

    public CameraFilter(Context context) {
        super(context, R.raw.camera_vertex, R.raw.camera_frag);
    }

    @Override
    protected void initCoordinate() {
        super.initCoordinate();
        //修改纹理坐标 让其镜像
        mGLTextureBuffer.clear();
        float[] TEXTURE = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };
        mGLTextureBuffer.put(TEXTURE);
    }

    @Override
    public int onDrawFrame(int texture) {
        //1、设置窗口大小
        //画画的时候 你的画布可以看成 10x10，也可以看成5x5 等等
        //设置画布的大小，然后画画的时候， 画布越大，你画上去的图像就会显得越小
        // x与y 就是从画布的哪个位置开始画
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);


        //不调用的话就是默认的操作glsurfaceview中的纹理了。显示到屏幕上了
        //这里我们还只是把它画到fbo中(缓存)
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);

        //使用着色器程序
        GLES20.glUseProgram(mGLProgramId);

        // 怎么画？ 其实就是传值
        //2：xy两个数据 float的类型
        //1、将顶点数据传入，确定形状
        mGLVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mGLVertexBuffer);
        //传了数据之后 激活
        GLES20.glEnableVertexAttribArray(vPosition);

        //2、将纹理坐标传入，采样坐标
        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
        GLES20.glEnableVertexAttribArray(vCoord);

        //变换矩阵
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, matrix, 0);

        //片元 vTexture 绑定图像数据到采样器
        //激活图层
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        // 图像数据
        // 正常：GLES20.GL_TEXTURE_2D
        // surfaceTexure的纹理需要
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);
        //传递参数 0：需要和纹理层GL_TEXTURE0对应
        GLES20.glUniform1i(vTexture, 0);

        //参数传完了 通知opengl 画画 从第0点开始 共4个点
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        //返回fbo里面的纹理id
        return mFrameBufferTextures[0];
    }

    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }

}
