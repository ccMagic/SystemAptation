package com.github.ccmagic.systemaptation;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.github.ccmagic.systemaptation.meizu.FLYME;
import com.github.ccmagic.systemaptation.xiaomi.MIUI;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 2018/9/18.
 *
 * @author kxmc
 * 状态栏适配
 */
public class StatusBarUtil {
    private static final String TAG = "StatusBarUtil";
    private static int mStatusBarHeight = -1;
    private static int mActionBarHeight = -1;

    /**
     * 透明状态栏，界面布局会延伸到状态栏
     * */
    public static void translucentStatus(Activity activity) {
        if (activity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    /**
     * 通过设置全屏，设置状态栏透明
     */
    public static void fullScreen(Activity activity) {
        if (activity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//Android5.0以上
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            Window window = activity.getWindow();
            if (window != null) {
                View decorView = window.getDecorView();
                if (decorView != null) {
                    //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                    int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                    decorView.setSystemUiVisibility(option);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.TRANSPARENT);
                    //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//Android4.4以上
            Window window = activity.getWindow();
            if (window != null) {
                WindowManager.LayoutParams attributes = window.getAttributes();
                if (attributes != null) {
                    attributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//                attributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                    window.setAttributes(attributes);
                }
            }

        }
    }

    /**
     * 手动给根视图设置一个 paddingTop ，高度为状态栏高度，
     * 相当于手动实现了 fitsSystemWindows=true 的效果，
     * 然后再在根视图加入一个占位视图，其高度也设置为状态栏高度。
     * 通过这种方法达到沉浸式的效果后面也可以很方便地拓展出渐变色的状态栏。
     *
     * @param activity Activity
     * @param resource 状态栏背景样式
     */
    public static void addStatusViewWithPaddingTop(Activity activity, int resource) {
        if (activity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mStatusBarHeight = getStatusBarHeight(activity);
            //设置 paddingTop
            Window window = activity.getWindow();
            if (window != null) {
                View decorView = window.getDecorView();
                if (decorView instanceof ViewGroup) {
                    ViewGroup decorViewGroup = (ViewGroup) decorView;
                    ViewGroup rootView = decorViewGroup.findViewById(android.R.id.content);
                    if (rootView != null) {
                        if (activity.getActionBar() != null) {
                            if (mActionBarHeight == -1) {
                                //获取actionbar的高度
                                TypedArray actionbarSizeTypedArray = activity.obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
                                float h = actionbarSizeTypedArray.getDimension(0, 0);
                                mActionBarHeight = (int) h;
                                actionbarSizeTypedArray.recycle();
                            }
                            rootView.setPadding(0, mStatusBarHeight + mActionBarHeight, 0, 0);
                        } else {
                            rootView.setPadding(0, mStatusBarHeight, 0, 0);
                        }
                        //
                        //
                        View statusBarView = new View(activity);
                        statusBarView.setBackgroundResource(resource);
                        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mStatusBarHeight);
                        decorViewGroup.addView(statusBarView, lp);
                    }
                }
            }
        }
    }

    /**
     * 没有StatusView时，用padding保证特定布局在状态栏下方
     */
    public static void noStatusViewSetPaddingTop(final Activity activity, final ViewGroup viewGroup) {
        if (activity == null || viewGroup == null) {
            return;
        }
        viewGroup.post(() -> viewGroup.setPadding(viewGroup.getPaddingLeft(),
                viewGroup.getPaddingTop() + StatusBarUtil.getStatusBarHeight(activity),
                viewGroup.getPaddingRight(),
                viewGroup.getPaddingBottom()));
    }

    /**
     * 设置状态栏深色浅色切换
     */
    public static Disposable setStatusBarDarkTheme(@NonNull final Activity activity, boolean dark, SystemCheckCallBack osCheckCallBack) {
        return Observable.just(dark ? 1 : 0).observeOn(Schedulers.computation())
                .subscribeOn(Schedulers.computation())
                .map(integer -> {

                    if (MIUI.isXiaomiDevice()) {
                        //小米不仅要适配 Build.VERSION_CODES.M，还要适配7.7.13版本以上
                        return MIUI.setStatusbarColorUtils(activity, dark);
                    } else if (FLYME.isFlymeDevice()) {
                        return FLYME.setStatusBarColorUtils(activity, dark);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        return setCommonUI(activity, dark);
                    } else {//6.0以下,5.0原生系统无法修改字体颜色,用了个比较取巧的办法,设置成半透明灰色.
                        return StatusBarBgUtil.initStatusBar(activity);
                    }
                })
                .subscribe(aBoolean -> {
                    if (osCheckCallBack != null) {
                        osCheckCallBack.checkFinish(aBoolean);
                    }
                    Log.d(TAG, "accept aBoolean: " + aBoolean);
                }, throwable -> Log.e(TAG, "accept: ", throwable));
    }

    /**
     * 设置6.0 状态栏深色浅色切换
     *
     * @return 是否设置成功
     */
    private static boolean setCommonUI(Activity activity, boolean dark) {
        if (activity == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = activity.getWindow().getDecorView();
            if (decorView == null) {
                return false;
            }
            int vis = decorView.getSystemUiVisibility();
            if (dark) {
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            if (decorView.getSystemUiVisibility() != vis) {
                decorView.setSystemUiVisibility(vis);
            }
            return true;
        }
        return false;

    }


    /**
     * 利用反射获取状态栏高度
     *
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Activity activity) {
        if (activity != null) {
            if (mStatusBarHeight == -1) {
                //获取状态栏高度的资源id
                int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    mStatusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
                }
            }
        }
        return mStatusBarHeight;
    }

    /**
     * 修改状态栏字体颜色
     */
    public interface SystemCheckCallBack {
        /**
         * 修改状态栏字体颜色是否成功
         *
         * @param success true成功，false失败
         */
        void checkFinish(boolean success);
    }
}
