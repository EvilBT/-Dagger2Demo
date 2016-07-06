package xyz.zpayh.dagger2demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;

import javax.inject.Inject;

import xyz.zpayh.dagger2demo.qualifier.PoetryQualifier;

public class AActivity extends AppCompatActivity {
    TextView mTextView;


    @Inject
    Gson mGson;

    // 匹配Module中同样注解的方法
    @PoetryQualifier("A")
    @Inject
    Poetry mPoetry;

    // 匹配Module中同样注解的方法
    @PoetryQualifier("B")
    @Inject
    Poetry mPoetryB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);

        MainApplication.getInstance()
                .getAComponent()
                .inject(this);

        mTextView = (TextView) findViewById(R.id.text);
        String text = mPoetry.getPoems()+",mPoetryA:"+mPoetry+
                mPoetryB.getPoems()+",mPoetryB:"+mPoetryB+
                (mGson == null ? "Gson没被注入" : "Gson已经被注入");
        mTextView.setText(text);
    }
}
