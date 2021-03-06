package com.leeway.rssreader.ui.main;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.leeway.rssreader.R;
import com.leeway.rssreader.base.BaseActivity;
import com.leeway.rssreader.chrome.ChromeTabsWrapper;
import com.leeway.rssreader.model.Feed;
import com.leeway.rssreader.model.RError;
import com.leeway.rssreader.model.RssItem;
import com.leeway.rssreader.ui.rss.RssFragment;
import com.leeway.rssreader.ui.rss.RssFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity<MainContract.Presenter> implements
        MainContract.View, RssFragment.OnItemSelectListener {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.tablayout)
    TabLayout mTabLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    ChromeTabsWrapper mChromeTabsWrapper;

    ProgressDialog progress;

    @Override
    protected int getContentResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(@Nullable Bundle state) {
        setSupportActionBar(mToolbar);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        getPresenter().loadRssFragments();
    }

    @Override
    protected void injectDependencies() {
        getActivityComponent().inject(this);
    }

    @Override
    public void onLoadRssFragments(Feed[] feeds) {
        setUpViewPager(feeds);
    }

    @Override
    public void showLoading() {
        mTabLayout.setVisibility(View.GONE);
//        progressBar.setVisibility(View.VISIBLE);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show();
    }

    @Override
    public void hideLoading() {
        progress.hide();
//        progressBar.setVisibility(View.GONE);
        mTabLayout.setVisibility(View.VISIBLE);
    }

    private void setUpViewPager(Feed[] feeds) {
        List<RssFragment> fragmentList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for (Feed feed :
//                new FeedParser().parseFeeds(this)) {
                feeds) {
            fragmentList.add(RssFragment.newInstance(feed));
            titles.add(feed.getTitle());
        }

        RssFragmentAdapter adapter = new RssFragmentAdapter(getSupportFragmentManager()
                , fragmentList, titles);
        adapter.notifyDataSetChanged();
        mViewPager.setAdapter(adapter);
//        mViewPager.notify();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mChromeTabsWrapper.bindCustomTabsService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mChromeTabsWrapper.unbindCustomTabsService();
    }

    @Override
    public void onItemSelected(RssItem rssItem) {
        mChromeTabsWrapper.openCustomtab(rssItem.getUrl());
    }

    @Override
    public void onFail(RError error) {
    }
}
