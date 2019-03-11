# SystemAptation
国内主流手机厂商操作系统适配（华为、小米、oppo、vivo等等），持续更新...

## 备注
打包jar发布maven库

![image](上传私有maven库.jpg)

其他项目引入
```
implementation 'xx.xxx:SystemAdapter:v1.0.0@jar'
```
## 零、版本说明
当前版本只是适配了不分小米和魅族的状态栏字体颜色

## 一、使用说明
1. StatusBarBgUtil这个类直接修改状态栏背景颜色
2. StatusBarUtil直接封装了所有状态栏的操作，是适配后的封装类，可以直接调用

## 二、手机厂商开发者平台
1. [华为开发者联盟：https://developer.huawei.com/consumer/cn/](https://developer.huawei.com/consumer/cn/)
1. [小米开放平台：https://dev.mi.com/console/doc/](https://dev.mi.com/console/doc/)
1. [oppo开放平台：https://open.oppomobile.com/](https://open.oppomobile.com/)
1. [vivo开发者平台：https://dev.vivo.com.cn/documentCenter](https://dev.vivo.com.cn/documentCenter)
1. [魅族开放平台：https://open.flyme.cn/](https://open.flyme.cn/)
1. [锤子开发者中心：http://dev.smartisan.com/](http://dev.smartisan.com/)
1. [三星开发者中心：https://developer.samsung.com/home.do](https://developer.samsung.com/home.do)


## 二、Build类说明

#### 1、代码
```
 Build.ID //修订版本列表
 Build.DISPLAY //显示屏参数
 Build.PRODUCT//整个产品的名称
 Build.DEVICE //设备参数
 Build.BOARD //主板
 Build.MANUFACTURER //硬件制造商
 Build.BRAND //系统定制商
 Build.MODEL //版本即最终用户可见的名称
 Build.BOOTLOADER //系统启动程序版本号
 Build.RADIO 
 Build.SERIAL 
 Build.HARDWARE // 硬件名称
 Build.FINGERPRINT 
 Build.VERSION.INCREMENTAL 
 Build.VERSION.RELEASE 
 Build.VERSION.BASE_OS 
 Build.VERSION.SECURITY_PATCH 
 System.getProperty("ro.miui.ui.version.name") 
 SystemPropertyUtil.getSystemProperty("ro.miui.ui.version.name")
```
#### 2、输出结果实例
<img src="Screenshot.png" width="300" hegiht="400" align=center />

## 三、注意事项
1. 修改状态栏字体颜色后再调用透明状态栏属性才会使得界面布局延伸到状态栏，例如
```
      mCompositeDisposable.add(StatusBarUtil.setStatusBarDarkTheme(this, false, success -> {
            //需要在完成之后回调才会保证布局可以延伸到状态栏
            StatusBarUtil.translucentStatus(this);
        }));
```
> 由于适配中需要查询是什么操作系统/手机厂商,设置状态栏深色浅色切换的方法直接封装到了Rxjava中，并且方便做线程切换
```

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
```
2. 如果你需要调用这个方法，注意会导致线程阻塞，请不要在UI线程调用
```
    @WorkerThread
    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }
```
