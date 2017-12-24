package com.example.user.hw_ch6;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

/**
 * Created by User on 2017/12/10.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private List<View> views;
    private Context context;
    public View ccp;
    public ViewPagerAdapter(List<View> views, Context context){
        this.views=views;
        this.context=context;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ( view == object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        //super.setPrimaryItem(container, position, object);
    }
}
