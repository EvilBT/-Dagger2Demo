package xyz.zpayh.dagger2demo.module;

import dagger.Module;
import dagger.Provides;
import xyz.zpayh.dagger2demo.qualifier.PoetryQualifier;
import xyz.zpayh.dagger2demo.scope.AScope;
import xyz.zpayh.dagger2demo.Poetry;

/**
 * 文 件 名: AModule
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/7/6 22:48
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */
@Module
public class AModule {

    @PoetryQualifier("A")
    @AScope
    @Provides
    public Poetry getPoetry(){
        return new Poetry("万物美好");
    }

    @PoetryQualifier("B")
    @AScope
    @Provides
    public Poetry getOtherPoetry(){
        return new Poetry("我在中间");
    }
}
