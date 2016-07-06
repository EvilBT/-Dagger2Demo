package xyz.zpayh.dagger2demo;

import android.app.Application;

import xyz.zpayh.dagger2demo.component.*;
import xyz.zpayh.dagger2demo.module.AModule;
import xyz.zpayh.dagger2demo.component.DaggerApplicationComponent;
/**
 * 文 件 名: MainApplication
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/7/6 22:07
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */
public class MainApplication extends Application {

    private ApplicationComponent mApplicationComponent;
    private AComponent mAComponent;
    private static MainApplication sApplication;

    public static MainApplication getInstance() {
        return sApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;

        mApplicationComponent = DaggerApplicationComponent.builder()
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }

    public AComponent getAComponent() {
        if (mAComponent == null){
            mAComponent = mApplicationComponent.plus(new AModule());
        }
        return mAComponent;
    }
}
