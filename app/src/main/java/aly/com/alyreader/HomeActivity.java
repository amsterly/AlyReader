package aly.com.alyreader;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import aly.com.alyreader.fragments.MusicFragment;
import aly.com.alyreader.fragments.NewsFragment;
import aly.com.alyreader.utils.ScreenUtils;
import aly.com.alyreader.utils.StatusBarCompat;
import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {


    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.id_title_toolebar)
    TextView idTitleToolebar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.id_fragmentMain)
    FrameLayout idFragmentMain;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.main_content)
    CoordinatorLayout mainContent;
    @Bind(R.id.id_navigationView)
    NavigationView idNavigationView;
    @Bind(R.id.id_drawerLayout)
    DrawerLayout idDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mMenuItemID = -1;
    private MusicFragment musicFragment;
    private NewsFragment newsFragment;
    private Fragment mFragment;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initNewsFragment(savedInstanceState);
        initView();
    }
    private void initNewsFragment(Bundle savedInstanceState) {
        //判断activity是否重建，如果是正常启动，则新建fragment.
        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            newsFragment = NewsFragment.newInstance(null, null);
//            mainFragment.setToolbarInterface(MainActivity.this);
            mFragment = newsFragment;
            mMenuItemID = R.id.id_new_menuitem;
            ft.replace(R.id.id_fragmentMain, newsFragment, newsFragment.getClass().getName()).commit();
            this.fab.setVisibility(View.INVISIBLE);
        }
    }

    private void initView() {
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(this);
        idDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
        setSupportActionBar(idToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        idNavigationView = (NavigationView) findViewById(R.id.id_navigationView);
        idDrawerLayout.closeDrawer(Gravity.LEFT);
        idNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mMenuItemID = menuItem.getItemId();
                initFragmentByMenuItemID(mMenuItemID);
                idNavigationView.getMenu().clear();
                idNavigationView.inflateMenu(R.menu.menu_drawer);
                idNavigationView.getMenu().findItem(mMenuItemID).setChecked(true);
                idDrawerLayout.closeDrawer(Gravity.LEFT);
                return true;

            }
        });

        int width = ScreenUtils.getScreenWidth(this);
        int height = ScreenUtils.getScreenHeight(this);
        Toast.makeText(this, String.format("宽:%s高:%s", width, height), Toast.LENGTH_SHORT).show();
        if (width > 0) {
            ViewGroup.LayoutParams params = idNavigationView.getLayoutParams();
            params.width = width * 2 / 3;
            idNavigationView.setLayoutParams(params);
        }
        mDrawerToggle = new ActionBarDrawerToggle(this, idDrawerLayout, idToolbar, R.string.togglebtn_open,
                R.string.togglebtn_close);
        mDrawerToggle.syncState();
        idDrawerLayout.setDrawerListener(mDrawerToggle);
//        StatusBarCompat.compat(this, 0xFFFF0000);
        StatusBarCompat.compat(this);
    }

    private void initFragmentByMenuItemID(int menuItemID) {
        switch (mMenuItemID) {
            case R.id.id_music_menuitem:
                setToolbarTitle("阿狸音乐");
                //创建前先检测是否已经创建
                musicFragment = (MusicFragment) getSupportFragmentManager().findFragmentByTag(MusicFragment.class.getName());
                if (musicFragment == null) {
                    musicFragment = MusicFragment.newInstance(null, null);
                }
                switchContent(mFragment, musicFragment);
                break;
            case R.id.id_new_menuitem:
                setToolbarTitle("阿狸新闻");
                //创建前先检测是否已经创建
                newsFragment = (NewsFragment) getSupportFragmentManager().findFragmentByTag(NewsFragment.class.getName());
                if (newsFragment == null) {
                    newsFragment = NewsFragment.newInstance(null, null);
                }
                switchContent(mFragment, newsFragment);

                break;
        }
    }

    private void setToolbarTitle(String title) {
        idTitleToolebar.setText(title);
    }

    //切换或者隐藏fragment通用做法
    public void switchContent(Fragment from, Fragment to) {
        if (mFragment != to) {
            mFragment = to;
            FragmentManager fm = getSupportFragmentManager();
            //添加渐隐渐现的动画
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in,
                    R.anim.slide_out);
            if (!to.isAdded()) {    // 先判断是否被add过  如果没有添加过 则添加一个含有其类名的tag的fragment
                Log.i(TAG, "switchContent: fragment" + to.getClass().getName() + " 没有被添加过 隐藏" + from.getClass().getName());
                ft.hide(from).add(R.id.id_fragmentMain, to, to.getClass().getName()).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                ft.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
                Log.i(TAG, "switchContent: fragment" + to.getClass().getName() + " 已经被添加过 隐藏" + from.getClass().getName());
            }
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }
}
