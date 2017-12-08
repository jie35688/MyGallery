package com.jey.mygallery;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by jie on 2017/12/8.
 */

public class MyPagerAdapter extends PagerAdapter {

    private List<View> mDatas;

    public MyPagerAdapter(List<View> datas) {
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mDatas.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view=mDatas.get(position);
        container.addView(view,position);
        return view;
    }
}
