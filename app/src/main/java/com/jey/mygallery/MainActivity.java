package com.jey.mygallery;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private int pagerWidth;
    private ImageView mImageView;
    private ViewPager mViewPager;
    private List<View> mList = new ArrayList<>();
    private int group = 1;
    private List<CheckBox> mBoxList = new ArrayList<>();
    private MyPagerAdapter myPagerAdapter;
    private List<String> mSystemPhotoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检测权限申请权限
            int checkSelfPermission = this.checkSelfPermission(WRITE_EXTERNAL_STORAGE);
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                //requestPermissions执行弹出请求授权对话框
                this.requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        123);
                return;
            }else {
                init();
            }
        }else {
            init();
        }

    }

    private void init() {
        mImageView = findViewById(R.id.iv);
        mViewPager = findViewById(R.id.viewpager);
        mSystemPhotoList = getSystemPhotoList(this);
        int size = mSystemPhotoList.size();//现在上限是500张，进入界面需要加载一段时间，张数越少时间越短，建议200张就行了，可以在这段时间加个加载圈
        if (size > 500) {
            size = 500;
        }
        //for (int i = 0; i <= group * 100 - 1;i++ ) {//跟group相关的都是分组加载的，先别动
        for (int i = 0; i < size ;i++ ) {
            Bitmap bitmap= BitmapFactory.decodeFile(mSystemPhotoList.get(i),getBitmapOption(4)); //将图片大小缩小为原来的1/4,为防止OOM可加大，但是清晰度会降低
            View view = View.inflate(this,R.layout.item_viewpager, null);
            final ImageView imageView = view.findViewById(R.id.item_iv);
            final CheckBox checkBox = view.findViewById(R.id.item_cb);
            mBoxList.add(checkBox);
            imageView.setImageBitmap(bitmap);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkBox.isChecked()){
                        mImageView.setImageDrawable(imageView.getDrawable());
                        for (CheckBox cb : mBoxList) {
                            cb.setChecked(false);
                        }
                        checkBox.setChecked(true);
                    }else {
                        checkBox.setChecked(true);
                    }
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!checkBox.isChecked()){
                        for (CheckBox cb : mBoxList) {
                            cb.setChecked(false);
                        }
                        checkBox.setChecked(true);
                        mImageView.setImageDrawable(imageView.getDrawable());
                    }
                }
            });
            mList.add(view);
        }


        findViewById(R.id.main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mViewPager.dispatchTouchEvent(motionEvent);
            }
        });


        mViewPager.setOffscreenPageLimit(mList.size());
        pagerWidth = (int) (getResources().getDisplayMetrics().widthPixels/5.0f);
        ViewGroup.LayoutParams lp = mViewPager.getLayoutParams();
        if (lp == null){
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }else {
            lp.width = pagerWidth;
        }
        mViewPager.setLayoutParams(lp);
        mViewPager.setPageMargin(-20);
        mViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {

            }
        });
        myPagerAdapter = new MyPagerAdapter(mList);
        mViewPager.setAdapter(myPagerAdapter);
        //根据滑动分布加载的地方，还没写好，先别动
        /*mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 99) {
                    mList.clear();
                    mBoxList.clear();
                    group = group + 1;
                    for (int i = (group - 1) * 100; i <= group * 100 - 1;i++ ) {
                        Bitmap bitmap= BitmapFactory.decodeFile(mSystemPhotoList.get(i),getBitmapOption(4)); //将图片的长和宽缩小味原来的1/4
                        View view = View.inflate(MainActivity.this,R.layout.item_viewpager, null);
                        final ImageView imageView = view.findViewById(R.id.item_iv);
                        final CheckBox checkBox = view.findViewById(R.id.item_cb);
                        mBoxList.add(checkBox);
                        imageView.setImageBitmap(bitmap);
                        checkBox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (checkBox.isChecked()){
                                    mImageView.setImageDrawable(imageView.getDrawable());
                                    for (CheckBox cb : mBoxList) {
                                        cb.setChecked(false);
                                    }
                                    checkBox.setChecked(true);
                                }else {
                                    checkBox.setChecked(true);
                                }
                            }
                        });
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!checkBox.isChecked()){
                                    for (CheckBox cb : mBoxList) {
                                        cb.setChecked(false);
                                    }
                                    checkBox.setChecked(true);
                                    mImageView.setImageDrawable(imageView.getDrawable());
                                }
                            }
                        });
                        mList.add(view);
                    }
                    myPagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
    }

    public static List<String> getSystemPhotoList(Context context) {
        List<String> result = new ArrayList<String>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null || cursor.getCount() <= 0) return null; // 没有图片
        while (cursor.moveToNext()) {
            int index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(index); // 文件地址
            File file = new File(path);
            if (file.exists()) {
                result.add(0,path);
            }
        }

        return result ;
    }

    private BitmapFactory.Options getBitmapOption(int inSampleSize){
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission 允许
                    init();
                } else {
                    // Permission 拒绝
                    Toast.makeText(this,"请开启权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
