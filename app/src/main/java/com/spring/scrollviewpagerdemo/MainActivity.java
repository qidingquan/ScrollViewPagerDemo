package com.spring.scrollviewpagerdemo;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SwipeRefreshLayout refresh_view;
    private MyScrollView scrollView;
    private ViewPager view_pager;
    private TextView tv_tab;

    private ArrayList<View> views;
    private MyViewPagerAdapter pagerAdapter;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<MyRecyclerView> recyclerViewList=new ArrayList<>();
    private List<String> oneDataList;
    private List<String> twoDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refresh_view = findViewById(R.id.refresh_view);
        scrollView = findViewById(R.id.scrollView);
        view_pager = findViewById(R.id.view_pager);
        tv_tab = findViewById(R.id.tv_tab);

        //动态设置ViewPager高度使其显示在标题和TAB下面占满全部布局
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view_pager.getLayoutParams();
        params.height = DisplayUtil.getScreenHeight(this) - DisplayUtil.getStatusBarHeight(this)
                - DisplayUtil.dip2px(this, 48 + 40);
        view_pager.setLayoutParams(params);

        //获取数据
        getData();

        //初始化ViewPager项布局
        views = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_layout, null);

            final MyRecyclerView recycler_view = view.findViewById(R.id.recycler_view);
            recyclerViewAdapter = new RecyclerViewAdapter(this, R.layout.item_recycler, i % 2 == 0 ? oneDataList : twoDataList);
            final MyLayoutManager layoutManager=new MyLayoutManager(this);
            recycler_view.setLayoutManager(layoutManager);
            recycler_view.setAdapter(recyclerViewAdapter);
            views.add(view);
            recyclerViewList.add(recycler_view);
            recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    //获取TEB的位置
                    int[] tabLocation = new int[2];
                    tv_tab.getLocationOnScreen(tabLocation);
                    //判断TAB是否滑动到标题下面
                    if (tabLocation[1] > DisplayUtil.getStatusBarHeight(MainActivity.this)
                            + DisplayUtil.dip2px(MainActivity.this, 48) + 1) {
                        scrollView.setEnableScroll(true);
                        recycler_view.setEnableScroll(false);
                        Log.e(TAG, "scrollView: true" );
                    } else {
                        scrollView.setEnableScroll(false);
                        recycler_view.setEnableScroll(true);
                        Log.e(TAG, "RecyclerView: true" );
                        //判断是否滑动到顶部
                        if(0==((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition()){

                        }
                    }
                }
            });
        }
        pagerAdapter = new MyViewPagerAdapter(views);
        view_pager.setAdapter(pagerAdapter);

        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tv_tab.setText("tab" + (position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        refresh_view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh_view.setRefreshing(false);
            }
        });
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //获取TEB的位置
                int[] tabLocation = new int[2];
                tv_tab.getLocationOnScreen(tabLocation);
                //判断TAB是否滑动到标题下面
                if (tabLocation[1] > DisplayUtil.getStatusBarHeight(MainActivity.this)
                        + DisplayUtil.dip2px(MainActivity.this, 48)) {
                    scrollView.setEnableScroll(true);
                    recyclerViewList.get(0).setEnableScroll(false);
                    recyclerViewList.get(1).setEnableScroll(false);
                } else {
                    scrollView.setEnableScroll(false);
                    recyclerViewList.get(0).setEnableScroll(true);
                    recyclerViewList.get(1).setEnableScroll(true);
                }
            }
        });
    }

    private void getData() {
        oneDataList = new ArrayList<>();
        twoDataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            oneDataList.add("A项目:" + i);
            twoDataList.add("B项目:" + i);
        }
    }
}
