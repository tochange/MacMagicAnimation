package com.gplibs.magic_surface_view_sample;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.gplibs.magicsurfaceview.MagicSurface;
import com.gplibs.magicsurfaceview.MagicSurfaceModelUpdater;
import com.gplibs.magicsurfaceview.MagicSurfaceView;
import com.gplibs.magicsurfaceview.MagicUpdater;
import com.gplibs.magicsurfaceview.MagicUpdaterListener;

public abstract class MagicActivity extends AppCompatActivity {

    private View mPageViewContainer;
    private MagicSurfaceView mPageSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_magic);
        mPageViewContainer = findViewById(R.id.page_view_container);
        mPageSurfaceView = (MagicSurfaceView) findViewById(R.id.page_surface_view);
        if (!show()) {
            mPageViewContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPageSurfaceView.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // 进行离场动画
        if (!hide()) {
            finish();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, (FrameLayout) findViewById(R.id.fl_page_content), true);
    }


    protected MagicUpdater getPageUpdater(boolean isHide) {
        return new MacWindowAnimUpdater(isHide, Direction.RIGHT, 0.184f, false);
    }


    /**
     * 页面转场动画对应 SurfaceModel 行数
     *
     * @return
     */
    protected int pageAnimRowCount() {
        return 30;
    }

    /**
     * 页面转场动画对应 SurfaceModel 列数
     *
     * @return
     */
    protected int pageAnimColCount() {
        return 30;
    }

    /**
     * 页面转场动画入场动画完成后调用
     *
     * @return
     */
    protected void onPageAnimEnd() {
    }

    /**
     * 开始入场动画
     *
     * @return
     */
    private boolean show() {
        MagicUpdater updater = getPageUpdater(false);
        return showWithSurface(updater);
    }

    private boolean showWithSurface(MagicUpdater updater) {
        updater.addListener(new MagicUpdaterListener() {
            @Override
            public void onStart() {
                mPageViewContainer.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStop() {
                mPageViewContainer.setVisibility(View.VISIBLE);
                mPageSurfaceView.setVisibility(View.GONE);
                // 动画完成释放资源
                mPageSurfaceView.release();
                onPageAnimEnd();
            }
        });
        final MagicSurface s = new MagicSurface(mPageViewContainer)
                .setGrid(pageAnimRowCount(), pageAnimColCount())
                .drawGrid(false);
        s.setModelUpdater((MagicSurfaceModelUpdater) updater);
        mPageSurfaceView.setVisibility(View.VISIBLE);
        mPageSurfaceView.render(s);
        return true;
    }


    /**
     * 开始离场动画
     *
     * @return
     */
    private boolean hide() {
        MagicUpdater updater = getPageUpdater(true);
        return hideWithSurface(updater);
    }

    private boolean hideWithSurface(MagicUpdater updater) {
        updater.addListener(new MagicUpdaterListener() {
            @Override
            public void onStart() {
                mPageViewContainer.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStop() {
                mPageSurfaceView.setVisibility(View.GONE);
                // 动画完成释放资源
                mPageSurfaceView.release();
                finish();
            }
        });
        MagicSurface s = new MagicSurface(mPageViewContainer)
                .setGrid(pageAnimRowCount(), pageAnimColCount())
                .drawGrid(false);
        s.setModelUpdater((MagicSurfaceModelUpdater) updater);
        mPageSurfaceView.setVisibility(View.VISIBLE);
        mPageSurfaceView.render(s);
        return true;
    }

}
