package xyz.zpayh.dagger2demo.module;

import dagger.Module;
import dagger.Provides;
import xyz.zpayh.dagger2demo.Poetry;
import xyz.zpayh.dagger2demo.scope.PoetryScope;

/**
 * 文 件 名: PoetryModule
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/7/5 00:46
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */
@Module
public class PoetryModule {

    // 这个方法需要一个String参数，在Dagger2注入中，这些参数也是注入形式的，也就是
    // 要有其他对方提供参数poems的生成，不然会造成编译出错
    @PoetryScope
    @Provides
    public Poetry providePoetry(String poems){
        return new Poetry(poems);
    }

    // 这里提供了一个生成String的方法，在这个Module里生成Poetry实例时，会查找到这里
    // 可以为上面提供String类型的参数
    @Provides
    public String providePoems(){
        return "只有意志坚强的人，才能到达彼岸";
    }
}
