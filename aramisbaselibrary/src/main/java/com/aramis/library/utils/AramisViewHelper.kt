package com.aramis.library.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import kotlin.math.*

/**
 *Created by Aramis
 *Date:2018/8/27
 *Description:
 */
object AramisViewHelper {

    private var PentaclePointArray: Array<PointF>? = null
    private var pentacleParams = floatArrayOf(0f, 0f, 0f)

    /**
     * 画五角星
     * @param cx 圆心x
     * @param cy 圆心y
     * @param r 五角星外接圆半径
     */
    fun drawPentacle(canvas: Canvas?, cx: Float, cy: Float, r: Float, paint: Paint, cache: Boolean) {

        val result = if (cache && PentaclePointArray != null && pentacleParams[0] == cx && pentacleParams[1] == cy && pentacleParams[2] == r) {
            PentaclePointArray!!
        } else {
            getPentaclePointsArray(cx, cy, r, cache)
        }
        val path = Path()
        result.forEachIndexed { index, p ->
            when (index) {
                0 -> path.moveTo(p.x, p.y)
                else -> path.lineTo(p.x, p.y)
            }
        }
        path.close()
        canvas?.drawPath(path, paint)
        path.reset()
    }

    private fun getPentaclePointsArray(cx: Float, cy: Float, r: Float, cache: Boolean): Array<PointF> {
//        logE("计算 计算 计算")
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

        val result = arrayOf(pointA, pointF, pointB, pointG, pointC, pointH, pointD, pointI, pointE, pointJ)
        if (cache) {
            PentaclePointArray = result
            pentacleParams[0] = cx
            pentacleParams[1] = cy
            pentacleParams[2] = r
        }
        return result
    }

    fun drawPole(canvas: Canvas?, startX: Float, startY: Float, endX: Float, endY: Float,
                 width: Float, paint: Paint) {
        drawPole(canvas, PointF(startX, startY), PointF(endX, endY), width, paint)
    }

    /**
     * 画圆棒
     */
    fun drawPole(canvas: Canvas?, startPoint1: PointF, endPoint1: PointF,
                 width: Float, paint: Paint) {
        val sPoint = if (startPoint1.x > endPoint1.x) endPoint1 else startPoint1
        val ePoint = if (startPoint1.x > endPoint1.x) startPoint1 else endPoint1

        val r = width / 2f
        val path = Path()
        val length = getPointDistance(sPoint, ePoint)
        val alpha = getLineAlphaRadian(sPoint, ePoint)


        val oc = r + sPoint.x / cos(alpha)
        val ox = oc * cos(alpha)
        val oy = oc * sin(alpha) + sPoint.y - sPoint.x * tan(alpha)
        val o1 = PointF(ox, oy)
        val o2 = PointF(o1.x + (length - r * 2) * cos(alpha), o1.y + (length - r * 2) * sin(alpha))

        val beta = Math.toRadians(90.0).toFloat() - alpha
        val arrayAB = getDiameterPoints(o1, r, beta)
        val arrayCD = getDiameterPoints(o2, r, beta)

        canvas?.drawCircle(o1.x, o1.y, r, paint)
        canvas?.drawCircle(o2.x, o2.y, r, paint)

        path.moveTo(arrayAB[0].x, arrayAB[0].y)
        path.lineTo(arrayAB[1].x, arrayAB[1].y)
        path.lineTo(arrayCD[1].x, arrayCD[1].y)
        path.lineTo(arrayCD[0].x, arrayCD[0].y)
        path.close()

        canvas?.drawPath(path, paint)
    }

    /**
     * 两点间距离
     */
    fun getPointDistance(pointA: PointF, pointB: PointF): Float {
        val a = pointA.x - pointB.x
        val b = pointA.y - pointB.y
        return sqrt(a * a + b * b)
    }

    /**
     * 直径与x轴的夹角(弧度)
     */
    fun getLineAlphaRadian(pointA: PointF, pointB: PointF): Float {
        val a = pointA.x - pointB.x
        val b = pointA.y - pointB.y
        return atan(b / a)
    }


    /**
     * 获取任意直径交圆的两点坐标
     * @param slope 直径与x轴的夹角(弧度)
     */
    fun getDiameterPoints(cx: Float, cy: Float, r: Float, alpha: Float): Array<PointF> {
        val pointA = PointF(cx + r * cos(alpha), cy - r * sin(alpha))
        val pointB = PointF(cx - r * cos(alpha), cy + r * sin(alpha))
        return arrayOf(pointA, pointB)
    }

    fun getDiameterPoints(c: PointF, r: Float, alpha: Float): Array<PointF> {
        return getDiameterPoints(c.x, c.y, r, alpha)
    }

    fun getDiameterPoints(c: PointF, r: Float, linePointA: PointF, linePointB: PointF): Array<PointF> {
        val alpha = getLineAlphaRadian(linePointA, linePointB)
        return getDiameterPoints(c.x, c.y, r, alpha)
    }
}