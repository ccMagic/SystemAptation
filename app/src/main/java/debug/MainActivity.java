package debug;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.github.ccmagic.systemaptation.R;
import com.github.ccmagic.systemaptation.StatusBarUtil;
import com.github.ccmagic.systemaptation.SystemPropertyUtil;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCompositeDisposable.add(StatusBarUtil.setStatusBarDarkTheme(this, false, success -> {
            //需要在完成之后回调才会保证布局可以延伸到状态栏
            StatusBarUtil.translucentStatus(this);
        }));

        StringBuilder stringBuilder = new StringBuilder();

        //
        stringBuilder.append("Build.ID: " + Build.ID + "\n");//修订版本列表
        stringBuilder.append("Build.DISPLAY: " + Build.DISPLAY + "\n");//显示屏参数
        stringBuilder.append("Build.PRODUCT: " + Build.PRODUCT + "\n");//整个产品的名称
        stringBuilder.append("Build.DEVICE: " + Build.DEVICE + "\n");//设备参数
        stringBuilder.append("Build.BOARD: " + Build.BOARD + "\n");//主板
        stringBuilder.append("Build.MANUFACTURER: " + Build.MANUFACTURER + "\n");//硬件制造商
        stringBuilder.append("Build.BRAND: " + Build.BRAND + "\n");//系统定制商
        stringBuilder.append("Build.MODEL: " + Build.MODEL + "\n");//版本即最终用户可见的名称
        stringBuilder.append("Build.BOOTLOADER: " + Build.BOOTLOADER + "\n");//系统启动程序版本号
        stringBuilder.append("Build.RADIO: " + Build.RADIO + "\n");
        stringBuilder.append("Build.SERIAL : " + Build.SERIAL + "\n");
        stringBuilder.append("Build.HARDWARE: " + Build.HARDWARE + "\n");// 硬件名称
        stringBuilder.append("Build.FINGERPRINT : " + Build.FINGERPRINT + "\n");
        stringBuilder.append("Build.VERSION.INCREMENTAL: " + Build.VERSION.INCREMENTAL + "\n");
        stringBuilder.append("Build.VERSION.RELEASE: " + Build.VERSION.RELEASE + "\n");
        stringBuilder.append("Build.VERSION.BASE_OS: " + Build.VERSION.BASE_OS + "\n");
        stringBuilder.append("Build.VERSION.SECURITY_PATCH: " + Build.VERSION.SECURITY_PATCH + "\n");
        stringBuilder.append("System System.getProperty: " + System.getProperty("ro.miui.ui.version.name") + "\n");

        new Thread(() -> {
            stringBuilder.append("System getSystemProperty: " + SystemPropertyUtil.getSystemProperty("ro.miui.ui.version.name") + "\n");
            runOnUiThread(() -> ((TextView) findViewById(R.id.text)).setText(stringBuilder.toString()));
        }
        ).start();
    }

    @Override
    protected void onDestroy() {
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
        }
        super.onDestroy();
    }
}
