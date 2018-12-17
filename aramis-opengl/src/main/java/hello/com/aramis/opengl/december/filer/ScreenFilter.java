package hello.com.aramis.opengl.december.filer;

import android.content.Context;

import hello.com.aramis.opengl.R;

/**
 * Created by Aramis
 * Date:2018/12/5
 * Description:
 */
public class ScreenFilter extends AbstractFilter {


    public ScreenFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.base_frag);
    }
}
