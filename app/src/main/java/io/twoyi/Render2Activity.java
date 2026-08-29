package io.twoyi;
import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.cleveroad.androidmanimation.LoadingAnimationView;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import io.twoyi.utils.AppKV;
import io.twoyi.utils.LogEvents;
import io.twoyi.utils.NavUtils;
import io.twoyi.utils.Profile;
import io.twoyi.utils.ProfileManager;
import io.twoyi.utils.RomManager;

public class Render2Activity extends Activity implements View.OnTouchListener {
    private static final String TAG = "Render2Activity";
    private SurfaceView mSurfaceView;
    private String mRootfsPath;
    private File mRootfsDir;
    private ViewGroup mRootView;
    private LoadingAnimationView mLoadingView;
    private TextView mLoadingText;
    private View mLoadingLayout;
    private View mBootLogView;
    private final AtomicBoolean mIsExtracting = new AtomicBoolean(false);

    private final SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override public void surfaceCreated(@NonNull SurfaceHolder holder) {
            Surface surface = holder.getSurface();
            WindowManager wm = getWindowManager();
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getRealMetrics(dm);
            Renderer.init(surface, RomManager.getLoaderPath(getApplicationContext()), mRootfsPath, dm.xdpi, dm.ydpi, (int) getBestFps());
        }
        @Override public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            Renderer.resetWindow(holder.getSurface(), 0, 0, mSurfaceView.getWidth(), mSurfaceView.getHeight());
        }
        @Override public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            Renderer.removeWindow(holder.getSurface());
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        boolean started = TwoyiStatusManager.getInstance().isStarted();
        if (started) { finish(); RomManager.reboot(this); return; }
        TwoyiStatusManager.getInstance().reset();
        NavUtils.hideNavigation(getWindow());
        super.onCreate(savedInstanceState);
        ProfileManager pm = ProfileManager.getInstance(this);
        Profile active = pm.getActiveProfile();
        if (active != null) { mRootfsDir = pm.getRootfsDir(active); } else { mRootfsDir = RomManager.getRootfsDir(this); }
        mRootfsPath = mRootfsDir.getAbsolutePath();
        setContentView(R.layout.ac_render);
        mRootView = findViewById(R.id.root);
        mSurfaceView = new SurfaceView(this);
        mSurfaceView.getHolder().addCallback(mSurfaceCallback);
        mLoadingLayout = findViewById(R.id.loadingLayout);
        mLoadingView = findViewById(R.id.loading);
        mLoadingText = findViewById(R.id.loadingText);
        mBootLogView = findViewById(R.id.bootlog);
        mLoadingLayout.setVisibility(View.VISIBLE);
        mLoadingView.startAnimation();
        UITips.checkForAndroid12(this, this::bootSystem);
        mSurfaceView.setOnTouchListener(this);
    }

    private void bootSystem() {
        boolean romExist = RomManager.romExist(mRootfsDir);
        boolean factoryRomUpdated = RomManager.needsUpgrade(this, mRootfsDir);
        boolean forceInstall = AppKV.getBooleanConfig(getApplicationContext(), AppKV.FORCE_ROM_BE_RE_INSTALL, false);
        boolean use3rdRom = AppKV.getBooleanConfig(getApplicationContext(), AppKV.SHOULD_USE_THIRD_PARTY_ROM, false);
        boolean shouldExtractRom = !romExist || forceInstall || (!use3rdRom && factoryRomUpdated);
        if (shouldExtractRom) {
            showTipsForFirstBoot();
            new Thread(() -> {
                mIsExtracting.set(true);
                RomManager.extractRootfs(getApplicationContext(), mRootfsDir, romExist, factoryRomUpdated, forceInstall, use3rdRom);
                mIsExtracting.set(false);
                RomManager.initRootfs(getApplicationContext(), mRootfsDir);
                RomManager.ensureBootFiles(getApplicationContext(), mRootfsDir);
                runOnUiThread(() -> { mRootView.addView(mSurfaceView, 0); showBootingProcedure(); });
            }, "extract-rom").start();
        } else {
            RomManager.ensureBootFiles(getApplicationContext(), mRootfsDir);
            mRootView.addView(mSurfaceView, 0);
            showBootingProcedure();
        }
    }

    private void showTipsForFirstBoot() {
        mLoadingText.setText(R.string.extracting_tips);
        mRootView.postDelayed(() -> { if (mIsExtracting.get()) mLoadingText.setText(R.string.first_boot_tips); }, 5000);
        mRootView.postDelayed(() -> { if (mIsExtracting.get()) mLoadingText.setText(R.string.first_boot_tips2); }, 10000);
        mRootView.postDelayed(() -> { if (mIsExtracting.get()) mLoadingText.setText(R.string.first_boot_tips3); }, 15000);
    }

    private void showBootingProcedure() {
        mLoadingText.setText(R.string.booting_tips);
        mLoadingText.setVisibility(View.VISIBLE);
        mBootLogView.setVisibility(View.GONE);
        new Thread(() -> {
            boolean success = false;
            try { success = TwoyiStatusManager.getInstance().waitBoot(45, TimeUnit.SECONDS); } catch (Throwable ignored) {}
            if (!success) {
                LogEvents.trackBootFailure(getApplicationContext());
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "容器启动超时，请检查 rootfs 后重试", Toast.LENGTH_LONG).show();
                    mLoadingView.stopAnimation();
                });
                SystemClock.sleep(2000);
                finish();
                return;
            }
            runOnUiThread(() -> { mLoadingView.stopAnimation(); mLoadingLayout.setVisibility(View.GONE); });
        }, "waiting-boot").start();
    }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) NavUtils.hideNavigation(getWindow());
        TwoyiStatusManager.getInstance().updateVisibility(hasFocus);
    }
    @Override public boolean onTouch(View v, MotionEvent event) { Renderer.handleTouch(event); return true; }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) { return super.onKeyDown(keyCode, event); }
    @Override public void onBackPressed() { Renderer.sendKeycode(KeyEvent.KEYCODE_HOME); }
    private float getBestFps() { return 45f; }
}
