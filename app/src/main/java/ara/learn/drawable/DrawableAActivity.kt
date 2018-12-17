package ara.learn.drawable

import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.ScaleDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.aramis.aramisapp.R
import kotlinx.android.synthetic.main.activity_drawable_a.*

/**
 *Created by Aramis
 *Date:2018/12/17
 *Description:
 */
class DrawableAActivity : AppCompatActivity() {
    private var imageLevel = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawable_a)
        title="Drawable"

        test_level.setOnClickListener {
            test_level.setImageLevel(++imageLevel)
        }

        val transitionDrawable =
                ContextCompat.getDrawable(this, R.drawable.drawable_transition_test) as TransitionDrawable
        test_transition.background = transitionDrawable
        test_transition.setOnClickListener {
            //            transitionDrawable.startTransition(1000)
            transitionDrawable.reverseTransition(1000)
        }


        val scaleDrawable = test_scale.background as ScaleDrawable
        scaleDrawable.level = 1


        val clipDrawable1 = test_clip1.drawable as ClipDrawable
        clipDrawable1.level = 2000
        val clipDrawable2 = test_clip2.drawable as ClipDrawable
        clipDrawable2.level = 5000
        val clipDrawable3 = test_clip3.drawable as ClipDrawable
        clipDrawable3.level = 8000

    }
}