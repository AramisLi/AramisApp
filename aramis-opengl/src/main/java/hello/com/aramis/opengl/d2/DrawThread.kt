package hello.com.aramis.opengl.d2

import android.graphics.Canvas
import com.aramis.library.extentions.logE

/**
 *Created by Aramis
 *Date:2018/10/31
 *Description:
 */
class DrawThread (private val gameView:MySurfaceView): Thread() {
    private val surfaceHolder=gameView.holder
    var flag = true
    override fun run() {
        super.run()
        logE("run run run")
        var canvas:Canvas?
        while (flag){
            canvas=null
            try {
                canvas=this.surfaceHolder.lockCanvas()
                synchronized(this.surfaceHolder){
                    gameView.draw(canvas)
                }
            }finally {
                if (canvas!=null){
                    this.surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }

            Thread.sleep(100)
        }
    }
}