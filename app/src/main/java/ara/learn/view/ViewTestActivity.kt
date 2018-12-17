package ara.learn.view

import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.aramis.aramisapp.R
import kotlinx.android.synthetic.main.activity_view_test.*

/**
 *Created by Aramis
 *Date:2018/12/13
 *Description:
 */
class ViewTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_test)
        title = "View的移动"

        testView.text = "说了对方就爱看的积分拉克丝京东方拉丝机颠覆了看见对方离开静安寺蝶恋蜂狂骄傲地离开那波uerngiuqbrvbfuygb搜；的好is你大V金卡圣诞节是可敬的佛我安静而烦恼东方丽景阿双方均安静地鸥鸟键是否驾驶的飞机阿萨德飞机奥if祭敖包女一搬过去不能跟骨你丶URuubnanunu丶一不嘎巴姑苏电饭锅阿萨德阿速必达挂 "

        //绝对移动。所以只能移动到固定位置
        text_scroll_to.setOnClickListener {
            testView.scrollTo(200, 0)
        }
        //相对移动。所以可以一直向后移动
        text_scroll_by.setOnClickListener {
            testView.scrollBy(200, 0)
        }


        val loadAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_100)
        text_scroll_bujian.setOnClickListener {
            testView.startAnimation(loadAnimation)
        }
        text_scroll_shuxing.setOnClickListener {
            ObjectAnimator.ofFloat(testView, "translationX", 0f, 100f).setDuration(100).start()
        }

        text_scroll_params.setOnClickListener {
            val layoutParams = testView.layoutParams as LinearLayout.LayoutParams
            layoutParams.leftMargin += 100
            testView.layoutParams=layoutParams
        }

    }
}