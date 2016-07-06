package xyz.zpayh.dagger2demo.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * 文 件 名: AScope
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/7/6 22:46
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface AScope {
}
