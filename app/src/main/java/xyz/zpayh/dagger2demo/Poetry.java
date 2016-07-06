package xyz.zpayh.dagger2demo;

import javax.inject.Inject;

/**
 * 文 件 名: Poetry
 * 创 建 人: 陈志鹏
 * 创建日期: 2016/7/3 23:54
 * 邮   箱: ch_zh_p@qq.com
 * 修改时间:
 * 修改备注:
 */
public class Poetry {
    private String mPoems;

    // 用Inject标记构造函数,表示用它来注入到目标对象中去
    @Inject
    public Poetry() {
        mPoems = "生活就像海洋";
    }

    public Poetry(String poems){
        mPoems = poems;
    }

    public String getPoems() {
        return mPoems;
    }
}
