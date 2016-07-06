package xyz.zpayh.dagger2demo.component;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Component;
import xyz.zpayh.dagger2demo.module.AModule;
import xyz.zpayh.dagger2demo.module.ApplicationModule;

/**
 * 文 件 名: ApplicationComponent
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/7/6 22:04
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    Gson getGson();// 暴露Gson对象接口

    //AComponent plus();
    AComponent plus(AModule module);//添加声明
}
