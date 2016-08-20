package com.joker.gankor.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.joker.gankor.R;
import com.joker.gankor.adapter.DailyNewsRecyclerAdapter;
import com.joker.gankor.adapter.GankRecyclerAdapter;
import com.joker.gankor.adapter.HotNewsRecyclerAdapter;
import com.joker.gankor.adapter.MainAdapter;
import com.joker.gankor.model.ZhihuDailyNews;
import com.joker.gankor.model.ZhihuHotNews;
import com.joker.gankor.ui.BaseActivity;
import com.joker.gankor.ui.fragment.GankFragment;
import com.joker.gankor.ui.fragment.ZhihuDailyNewsFragment;
import com.joker.gankor.ui.fragment.ZhihuHotNewsFragment;
import com.joker.gankor.utils.API;
import com.joker.gankor.utils.CacheUtil;
import com.joker.gankor.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GankRecyclerAdapter.TextViewListener,
        GankRecyclerAdapter.ImageViewListener, DailyNewsRecyclerAdapter.OnItemClickListener,
        ZhihuDailyNewsFragment.OnBannerClickListener, HotNewsRecyclerAdapter.OnItemClickListener {

    public MainAdapter mAdapter;
    public GankFragment mGankFragment;
    public ZhihuDailyNewsFragment mDailyNewsFragment;
    public ZhihuHotNewsFragment mHotNewsFragment;
    private Toolbar mTitleToolbar;
    private TabLayout mTitleTabLayout;
    private NavigationView mContentNavigationView;
    private ViewPager mContentViewPager;
    private List<Fragment> mFragments;
    private List<String> mTitles;
    private DrawerLayout mMainDrawerLayout;
    private long firstTime;
    private int mLastItemId;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mTitleToolbar = (Toolbar) findViewById(R.id.tb_title);
        mTitleTabLayout = (TabLayout) findViewById(R.id.tl_title);
        mContentViewPager = (ViewPager) findViewById(R.id.vp_content);
        mContentNavigationView = (NavigationView) findViewById(R.id.nv_content);
        mMainDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main);

//        设置导航栏顶部图片
        View view = mContentNavigationView.getHeaderView(0);
        ImageView header = (ImageView) view.findViewById(R.id.nav_head);
        ImageUtil.getInstance().displayImage(CacheUtil.getInstance(this).getAsString(SplashActivity.IMG),
                header);

//        设置 toolBar
        setSupportActionBar(mTitleToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mMainDrawerLayout,
                mTitleToolbar, R.string.meizhi, R.string.meizhi);
        mMainDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

//        设置 viewPager
        setupViewPager();
    }

    @Override
    protected void initData() {
        setupDrawerContent();
        mContentNavigationView.setCheckedItem(0);
    }

    private void setupDrawerContent() {
        mLastItemId = mContentNavigationView.getMenu().getItem(0).getItemId();
        mContentNavigationView.setNavigationItemSelectedListener(new NavigationView
                .OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mMainDrawerLayout.closeDrawers();
                if (item.getItemId() == R.id.menu_introduce) {
                    startActivity(new Intent(MainActivity.this, AboutMeActivity.class));
                    item.setChecked(false);
                } else {
                    if (item.getItemId() != mLastItemId) {
                        item.setChecked(true);
                        changeFragments(item.getItemId());
                        mLastItemId = item.getItemId();
                    }
                }
                return true;
            }
        });
    }

    public void changeFragments(int itemId) {
        mFragments.clear();
        mTitles.clear();
        switch (itemId) {
            case R.id.nav_knowledge:
//                      知乎界面
                initZhihu();
                break;
            case R.id.nav_beauty:
//                      妹纸界面
                initMeizhi();
                break;
            default:
                break;
        }
    }

    public void init() {
        mFragments = new ArrayList<>();
        mTitles = new ArrayList<>();
        mGankFragment = new GankFragment();
        mGankFragment.setImageListener(this);
        mGankFragment.setTextListener(this);

        mFragments.add(mGankFragment);
        mTitles.add("妹纸");
    }

    private void initMeizhi() {
        mGankFragment = new GankFragment();
        mGankFragment.setImageListener(this);
        mGankFragment.setTextListener(this);
        mFragments.add(mGankFragment);
        mTitles.add("妹纸");

        mAdapter.changeDataList(mTitles, mFragments);
    }

    public void initZhihu() {
        mDailyNewsFragment = new ZhihuDailyNewsFragment();
        mDailyNewsFragment.setOnItemClickListener(this);
        mDailyNewsFragment.setOnBannerClickListener(this);
        mHotNewsFragment = new ZhihuHotNewsFragment();
        mHotNewsFragment.setOnItemClickListener(this);

        mFragments.add(mDailyNewsFragment);
        mTitles.add("知乎日报");

        mFragments.add(mHotNewsFragment);
        mTitles.add("热门消息");

        mAdapter.changeDataList(mTitles, mFragments);
    }

    private void setupViewPager() {
        init();
        mAdapter = new MainAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mContentViewPager.setAdapter(mAdapter);
        mTitleTabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        mTitleTabLayout.setupWithViewPager(mContentViewPager);
    }

    public void hideTabLayout(boolean hide) {
        if (hide) {
            mTitleTabLayout.setVisibility(View.GONE);
        } else {
            mTitleTabLayout.setVisibility(View.VISIBLE);
        }
    }

    /*
    public void setToolbarScroll(boolean scroll) {
        AppBarLayout.LayoutParams paramsTool = (AppBarLayout.LayoutParams) mTitleAppBarLayout.getChildAt
                (0).getLayoutParams();
        AppBarLayout.LayoutParams paramsTab = (AppBarLayout.LayoutParams) mTitleAppBarLayout.getChildAt
                (1).getLayoutParams();
        if (scroll) {
//            toolBar上滑隐藏，下滑立即可见
            paramsTool.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout
                    .LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        } else {
//            toolBar上滑隐藏，下滑不可见
            paramsTool.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
            paramsTab.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout
                    .LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams
                    .SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        }
        mTitleAppBarLayout.getChildAt(0).setLayoutParams(paramsTool);
        mTitleAppBarLayout.getChildAt(1).setLayoutParams(paramsTab);
    }
    */

    public void setToolbarTitle(String title) {
        mTitleToolbar.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if (mContentNavigationView.isShown()) {
            mMainDrawerLayout.closeDrawers();
            return;
        }
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Snackbar sb = Snackbar.make(mContentNavigationView, "再按一次退出", Snackbar.LENGTH_SHORT);
            sb.getView().setBackgroundColor(getResources().getColor(R.color.red_300));
            sb.show();
            firstTime = secondTime;
        } else {
            finish();
        }
    }

    //    知乎日报列表点击事件
    @Override
    public void onZhihuItemClick(View view, ZhihuDailyNews.StoriesBean storiesBean) {
        int[] clickLocation = getClickLocation(view);
        startActivity(ZhihuDetailsActivity.newTopStoriesIntent(this, (API.ZHIHU_NEWS_FOUR + String.valueOf
                (storiesBean.getId())), clickLocation));
        this.overridePendingTransition(0, 0);
    }

    //    知乎日报头条点击事件
    @Override
    public void onBannerClickListener(ZhihuDailyNews.TopStoriesBean topStories) {
        startActivity(ZhihuDetailsActivity.newTopStoriesIntent(this, (API.ZHIHU_NEWS_FOUR + String.valueOf
                (topStories.getId())), null));
    }

    //    知乎日报热门列表点击事件
    @Override
    public void onZhihuItemClick(View view, ZhihuHotNews.RecentBean recentBean) {
        int[] clickLocation = getClickLocation(view);
        startActivity(ZhihuDetailsActivity.newTopStoriesIntent(this, (API.ZHIHU_NEWS_TWO + String.valueOf
                (recentBean.getNewsId())), clickLocation));
        this.overridePendingTransition(0, 0);
    }

    //    Gank 图片点击
    @Override
    public void onGankImageClick(View image, String url, String desc) {
        Intent intent = PictureActivity.newIntent(MainActivity.this, url, desc);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                MainActivity.this, image, PictureActivity.TRANSIT_PIC);
        try {
            ActivityCompat.startActivity(MainActivity.this, intent, optionsCompat.toBundle());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            startActivity(intent);
        }
    }

    //    Gank 文字点击
    @Override
    public void onGankTextClick(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private int[] getClickLocation(View v) {
        int[] clickLocation = new int[2];
        v.getLocationOnScreen(clickLocation);
        clickLocation[0] += v.getWidth() / 2;

        return clickLocation;
    }
}
