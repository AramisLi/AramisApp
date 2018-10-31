package hello.com.aramis.opengl.d2

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

/**
 *Created by Aramis
 *Date:2018/10/31
 *Description:
 */
class Bullet(val gameView: MySurfaceView, val bitmap: Bitmap, val bitmaps: Array<Bitmap>,
             var x: Float, var y: Float, val vx: Float, val vy: Float) {

    private var t = 0f
    private val timeSpan = 0.5f
    private var size = 0
    private var explodeFlag = false
    private var mExplosion: Explosion? = null

    init {
        size = bitmap.height
    }

    fun drawSelf(canvas: Canvas, paint: Paint) {
        if (explodeFlag && mExplosion != null) {
            mExplosion?.drawSelf(canvas, paint)
        } else {
            go()
            canvas.drawBitmap(bitmap, x, y, paint)
        }
    }

    fun go() {
        x += vx * t
        y += vy * t + 0.5f * Constant.G * t * t
        if (x >= Constant.EXPLOSION_X || y > -Constant.SCREEN_HEIGHT) {
            mExplosion = Explosion(gameView, bitmaps, x, y)
            explodeFlag = true
            return
        }
        t += timeSpan
    }

}

class Explosion(val gameView: MySurfaceView, private val bitmaps: Array<Bitmap>, val x: Float, val y: Float) {
    private var animIndex = 0
    fun drawSelf(canvas: Canvas, paint: Paint) {
        if (animIndex >= bitmaps.size - 1) {
            return
        }

        canvas.drawBitmap(bitmaps[animIndex], x, y, paint)
        animIndex++
    }
}

class Constant {
    companion object {
        var SCREEN_WIDTH = 0
        var SCREEN_HEIGHT = 0
        const val EXPLOSION_X = 270
        const val G = 1.0f

        fun init(w: Int, h: Int) {
            SCREEN_WIDTH = w
            SCREEN_HEIGHT = h

        }
    }


}