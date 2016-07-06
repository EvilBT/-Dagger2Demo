package xyz.zpayh.dagger2demo.component;

import dagger.Component;
import xyz.zpayh.dagger2demo.MainActivity;
import xyz.zpayh.dagger2demo.MainApplication;
import xyz.zpayh.dagger2demo.OtherActivity;
import xyz.zpayh.dagger2demo.module.PoetryModule;
import xyz.zpayh.dagger2demo.scope.PoetryScope;

/**
 * 文 件 名: MainComponent
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/7/4 00:10
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */
//这里表示Component会从MainModule类中拿那些用@Provides注解的方法来生成需要注入的实例
@PoetryScope
@Component(dependencies = ApplicationComponent.class, modules = PoetryModule.class)
public abstract class MainComponent {

    /**
     * 需要用到这个连接器的对象，就是这个对象里面有需要注入的属性
     * （被标记为@Inject的属性）
     * 这里inject表示注入的意思，这个方法名可以随意更改，但建议就
     * 用inject即可。
     */
    public abstract void inject(MainActivity activity);

    public abstract void inject(OtherActivity activity);

    private static MainComponent sComponent;
    public static MainComponent getInstance(){
        if (sComponent == null){
            sComponent = DaggerMainComponent.builder()
                    .applicationComponent(MainApplication
                    .getInstance().getApplicationComponent())
                    .build();
        }
        return sComponent;
    }
}
