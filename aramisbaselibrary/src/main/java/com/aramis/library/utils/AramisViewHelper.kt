package com.aramis.library.utils

import android.graphics.*
import org.w3c.dom.Text
import kotlin.math.*
import android.opengl.ETC1.getHeight
import android.graphics.Paint.FontMetricsInt


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
     * 任意直线与圆的交点坐标
     * @param cx 圆心坐标
     * @param cy 圆心坐标
     * @param r 圆半径
     * @param alpha 直线与x轴的夹角（弧度）
     * @param linePoint 直线上任意一点的坐标
     * @return null=圆与直线无交点
     */
    fun getNormalLineCircleIntersection(cx: Float, cy: Float, r: Float, alpha: Float, linePoint: PointF): Array<PointF>? {
        val ae = (linePoint.y + cy) / tan(alpha)
        val ao = linePoint.x - cx
        val oe = ae - ao
        val ob = oe * sin(alpha)

        return when {
            //没有交点
            ob > r -> null
            // 一个交点(切线)
            ob == r -> {
                arrayOf(PointF(cx - r * tan(alpha) * sin(alpha), cy - r * tan(alpha) * cos(alpha)))
            }
            //两个交点
            else -> {
                val og = ao / sin(alpha)
                val bg = og + ob
                val pb = bg * tan(alpha)
                val bf = sqrt(r * r - ob * ob)
                val pf = pb - bf
                val pointA = PointF(linePoint.x - pf * cos(alpha), pf * sin(alpha) - linePoint.y)
                val a = linePoint.x / cos(alpha) - pf - bf * 2
                val pointB = PointF(a * cos(alpha), linePoint.x * tan(alpha) - linePoint.y)
                arrayOf(pointA, pointB)
            }
        }

    }

    fun drawPoint(canvas: Canvas?, pointF: PointF, paint: Paint, r: Float = 10f) {
        canvas?.drawCircle(pointF.x, pointF.y, r, paint)
    }


    /**
     * 获取任意直径交圆的两点坐标
     * @param alpha 直径与x轴的夹角(弧度)
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

    /**
     * 获取绘制文字居中时的x，y
     */
    fun getDrawTextXY(text: String, rectCenterX: Float, rectCenterY: Float, paint: Paint): FloatArray {
        val x = rectCenterX - paint.measureText(text) / 2
        val fm = paint.fontMetricsInt
        val y = rectCenterY - fm.descent + (fm.bottom - fm.top) / 2
        return floatArrayOf(x, y)
    }

    fun getDrawTextXY(text: String, rectF: RectF, paint: Paint): FloatArray {

        return getDrawTextXY(text, rectF.centerX(), rectF.centerY(), paint)
    }

    fun drawTestRect(canvas: Canvas?, rectF: RectF, paint: Paint) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas?.drawRect(rectF, paint)
        canvas?.drawLine(rectF.left, rectF.top + rectF.height() / 2, rectF.right, rectF.top + rectF.height() / 2, paint)
        canvas?.drawLine(rectF.left + rectF.width() / 2f, rectF.top, rectF.left + rectF.width() / 2f, rectF.bottom, paint)
    }
}