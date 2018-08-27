package com.aramis.library.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 *Created by Aramis
 *Date:2018/8/27
 *Description:
 */
object AramisViewHelper {

    /**
     * 画五角星
     * @param cx 圆心x
     * @param cy 圆心y
     * @param r 五角星外接圆半径
     */
    fun drawPentacle(canvas: Canvas?, cx: Float, cy: Float, r: Float, paint: Paint) {
        val radians18 = Math.toRadians(18.0)
        val radians54 = Math.toRadians(54.0)
        val radians36 = Math.toRadians(36.0)

        val lo = r * sin(radians18).toFloat()
        val al = r - lo
        val am = r * cos(radians18).toFloat()

        val pointA = PointF(cx, cy - r)
        val pointB = PointF(cx + r * cos(radians18).toFloat(), cy - lo)
        val pointC = PointF(cx + r * cos(radians54).toFloat(), cy + r * sin(radians54).toFloat())
        val pointD = PointF(cx - r * cos(radians54).toFloat(), cy + r * sin(radians54).toFloat())
        val pointE = PointF(cx - r * cos(radians18).toFloat(), cy - lo)

        val pointF = PointF((cx + al * tan(radians18)).toFloat(), cy - lo)
        val pointJ = PointF((cx - al * tan(radians18)).toFloat(), cy - lo)

        val pointG = PointF((cx + am - al * cos(radians36) / cos(radians18)).toFloat(),
                (cy - r + al + al * sin(radians36) / cos(radians18)).toFloat())
        val pointI = PointF((cx - (am - al * cos(radians36) / cos(radians18))).toFloat(),
                (cy - r + al + al * sin(radians36) / cos(radians18)).toFloat())

        val pointH = PointF(cx, (cy + am * tan(radians36) - lo).toFloat())

        val path = Path()
        arrayOf(pointA, pointF, pointB, pointG, pointC, pointH, pointD, pointI, pointE, pointJ).forEachIndexed { index, p ->
            when (index) {
                0 -> path.moveTo(p.x, p.y)
                else -> path.lineTo(p.x, p.y)
            }
        }
        path.close()
        canvas?.drawPath(path, paint)
    }
}