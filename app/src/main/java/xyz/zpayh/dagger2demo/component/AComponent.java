package xyz.zpayh.dagger2demo.component;

import dagger.Subcomponent;
import xyz.zpayh.dagger2demo.AActivity;
import xyz.zpayh.dagger2demo.module.AModule;
import xyz.zpayh.dagger2demo.scope.AScope;

/**
 * 文 件 名: AComponent
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/7/6 22:33
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */
@AScope
@Subcomponent(modules = AModule.class)
public interface AComponent {
    void inject(AActivity activity);
}
