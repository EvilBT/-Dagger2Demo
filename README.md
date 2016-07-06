# Dagger2 使用详解

标签（空格分隔）： Android Dagger2

---
## 前言
Dagger2 是一款使用在Java和Android上的依赖注入的一个类库。
## 配置信息
使用Android Studio 创建一个新的项目，在Project的 `build.gradle`文件添加以下内容：
``` gradle
buildscript {
    
    dependencies {
        classpath 'me.tatarka:gradle-retrolambda:3.2.4'
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
    }
}
```
并在Module下的`build.gradle`添加以下内容:
``` gradle
apply plugin: 'com.neenbedankt.android-apt'
apply plugin: 'me.tatarka.retrolambda'

android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    apt 'com.google.dagger:dagger-compiler:2.4'
    compile 'com.google.dagger:dagger:2.4'
    provided 'org.glassfish:javax.annotation:10.0-b28'
}
```
这样就基本完全了Dagger2的配置环境（顺便也配置了支持lambda表达式）。
## Dagger2基本使用
我们先简单地创建一个类：
``` java
public class Poetry {
    private String mPemo;

    // 用Inject标记构造函数,表示用它来注入到目标对象中去
    @Inject
    public Poetry() {
        mPemo = "生活就像海洋";
    }

    public String getPemo() {
        return mPemo;
    }
}
```
然后我们在MainActivity中使用这个类：
``` java
public class MainActivity extends AppCompatActivity {

    //添加@Inject注解，表示这个mPoetry是需要注入的
    @Inject
    Poetry mPoetry;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.tv_poetry);
        mTextView.setText(mPoetry.getPoems());
    }
}
```
但是这样直接运行是会出错的，此时这样子在MainActivity中的*mPoetry*对象是无法被注入的，因为MainActivity不知道去哪里找到它的实例去注入生成，这时我们需要一个连接器*Component*，让上面这两个类产生联系：
``` java
//用@Component表示这个接口是一个连接器，能用@Component注解的只
//能是interface或者抽象类
@Component
public interface MainComponent {

    /**
     * 需要用到这个连接器的对象，就是这个对象里面有需要注入的属性
     * （被标记为@Inject的属性）
     * 这里inject表示注入的意思，这个方法名可以随意更改，但建议就
     * 用inject即可。
     */
    void inject(MainActivity activity);
}
```
先运行一遍，AS会生成一些类，再修改一下MainActivity:
``` java
public class MainActivity extends AppCompatActivity {

    //添加@Inject注解，表示这个mPoetry是需要注入的
    @Inject
    Poetry mPoetry;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 使用Dagger2生成的类 生成组件进行构造，并注入
        DaggerMainComponent.builder()
                .build()
                .inject(this);

        initView();
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.tv_poetry);
        mTextView.setText(mPoetry.getPoems());
    }
}
```
运行，如下
![运行结果][1]
上面`MainActivity`中的`Poetry`实例并不直接由MainActivity类创建，而是由`MainActivityComponent`类注入生成实例。以上就是一个简单的Dagger2示例。
### @Module
有时候我们并不能直接在构造函数里面添加`@Inject`注解，或者类中存在多个构造函数时，`@Inject`也只能注解其中一个构造函数，不能注解多个构造函数，这里是会产生*歧义性*，因为Dagger2无法确认调用哪一个构造函数来生成例的实例对象。另外一种情况是我们在项目中引用第三方类库时，也是无法直接在类构造函数中添加`@Inject`注解的，所以我们需要用到`@Module`注解了。
`@Module`是用来生产实例来注入对象的，它类似一个工厂，集中创建要注入的类的对象实例。下面我们引用一下Gson库来看看`@Module`是怎么使用的，创建`MainModule`类：
``` java
/*
@Module注解表示这个类提供生成一些实例用于注入
 */
@Module
public class MainModule {

    /**
     * @Provides 注解表示这个方法是用来创建某个实例对象的，这里我们创建返回Gson对象
     * 方法名随便，一般用provideXXX结构
     * @return 返回注入对象
     */
    @Provides
    public Gson provideGson(){
        return new Gson();
    }
}
```
添加完这个类后，我们要与之前写的类产生关联，不然谁知道你这里提供了生成Gson实例的方法啊。修改`MainCompontent`:
``` java
//这里表示Component会从MainModule类中拿那些用@Provides注解的方法来生成需要注入的实例
@Component(modules = MainModule.class)
public interface MainComponent {

    /**
     * 需要用到这个连接器的对象，就是这个对象里面有需要注入的属性
     * （被标记为@Inject的属性）
     * 这里inject表示注入的意思，这个方法名可以随意更改，但建议就
     * 用inject即可。
     */
    void inject(MainActivity activity);
}
```
这里多了一个依赖，依赖`MainModule`类中的方法生成Gson实例，我们在`MainActivity`里注入Gson实例：
``` java
public class MainActivity extends AppCompatActivity {

    //添加@Inject注解，表示这个mPoetry是需要注入的
    @Inject
    Poetry mPoetry;

    @Inject
    Gson mGson;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 使用Dagger2生成的类 生成组件进行构造，并注入
        DaggerMainComponent.builder()
                .build()
                .inject(this);

        initView();
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.tv_poetry);
        String json = mGson.toJson(mPoetry);
        mTextView.setText(json);
    }
}
```
运行，结果如下：
![运行结果][2]
`Component`可以依赖多个`Module`对象，以上的构造方法与生成方法都是无参生成实例的，如果我们带参数应该怎么做了？我们创建多一个`PoetryModule`用于提供`Poetry`实例:
``` java
@Module
public class PoetryModule {

    // 这个方法需要一个String参数，在Dagger2注入中，这些参数也是注入形式的，也就是
    // 要有其他对方提供参数poems的生成，不然会造成编译出错
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
```
修改`MainComponent`依赖:
``` java
//这里表示Component会从MainModule类中拿那些用@Provides注解的方法来生成需要注入的实例
@Component(modules = {MainModule.class,PoetryModule.class})
public interface MainComponent {

    /**
     * 需要用到这个连接器的对象，就是这个对象里面有需要注入的属性
     * （被标记为@Inject的属性）
     * 这里inject表示注入的意思，这个方法名可以随意更改，但建议就
     * 用inject即可。
     */
    void inject(MainActivity activity);
}
```
运行，就可以看到不同的诗词了：
![运行结果][3]
细心的同学就会发现了，我们提供了两个可以生成`Poetry`实例的方法，一个是在`Poetry`类的构造函数时候用`@Inject`提供的实例创建方法，一个是在`PoetryModule`中的`@Privodes`注解的*providePoetry*方法，而在上面的运行结果中我们发现是调用了`PoetryModule`提供的方法，这里就要说明一下优先级的问题，在上面这种既在构造函数中用`@Inject`提供注入来源，也在`@Module`中用`@Privodes`注解提供注入来源的，Dagger2是先从`@Privodes`查找类实例，如果找到了就用`@Module`提供的方法来创建类实例，如果没有就从构造函数里用`@Inject`注解的生成类实例，如果二者都没有，则报错，简而言之，就是`@Module`的优先级高于`@Inject`。
另外这里还要说明一点，在providePoetry(String)方法中，String这个参数也是要注入提供的，必须也要有在同一个连接器里面有提供，其中在构建类实例的时候，会按照以下顺序执行：

 1. 从Module中查找类实例创建方法
 2. Module中存在创建方法，则看此创建方法有没有参数
    1. 如果有参数，这些参数也是由Component提供的，返回**步骤1**逐一生成参数类实例，最后再生成最终类实例
    2. 如果无参数，则直接由这个方法生成最终类实例
 3. Module中没有创建方法，则从构造函数里面找那个用@Inject注解的构造函数
    1. 如果该构造函数有参数，则也是返回到**步骤1**逐一生成参数类实例，最后调用该构造函数生成类实例
    2. 如果该构造函数无参数，则直接调用该构造函数生成类实例

以上就是一次注入生成类实例的生成步骤。
### @Scope
我们创建多一个Activity，这个Activity也注入了Poetry跟Gson对象:
``` java
public class OtherActivity extends AppCompatActivity {

    //添加@Inject注解，表示这个mPoetry是需要注入的
    @Inject
    Poetry mPoetry;

    @Inject
    Gson mGson;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        MainComponent.getInstance()
                .inject(this);

        initView();
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.tv_poetry);
        String json = mGson.toJson(mPoetry);
        String text = json + ",mPoetry:"+mPoetry;
        mTextView.setText(text);
    }
}
```
我们顺便也把`MainComponent`改成抽象类的形式，并添加返回`MainComponent`单例的方法,对应添加MainActivity跳转到OtherActivity的方法.
``` java
@Component(modules = {MainModule.class,PoetryModule.class})
public abstract class MainComponent {

    /**
     * 需要用到这个连接器的对象，就是这个对象里面有需要注入的属性
     * （被标记为@Inject的属性）
     * 这里inject表示注入的意思，这个方法名可以随意更改，但建议就
     * 用inject即可。
     */
    abstract void inject(MainActivity activity);

    abstract void inject(OtherActivity activity);

    private static MainComponent sComponent;
    public static MainComponent getInstance(){
        if (sComponent == null){
            sComponent = DaggerMainComponent.builder().build();
        }
        return sComponent;
    }
}

public class MainActivity extends AppCompatActivity {

    //添加@Inject注解，表示这个mPoetry是需要注入的
    @Inject
    Poetry mPoetry;

    @Inject
    Gson mGson;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 使用Dagger2生成的类 生成组件进行构造，并注入
        MainComponent.getInstance()
                .inject(this);

        initView();
    }

    private void initView() {
        mTextView = (TextView) findViewById(R.id.tv_poetry);
        String json = mGson.toJson(mPoetry);
        String text = json + ",mPoetry:"+mPoetry;
        mTextView.setText(text);

        findViewById(R.id.open).setOnClickListener(view ->
                startActivity(new Intent(this,OtherActivity.class)));
    }
}
```
运行结果如下：
![运行结果][4]![运行结果][5]
可以看到，调用同一个`MainComponent`实例多次注入的时候每次都重新生成Poetry实例，有时候我们需要只希望生成一个共用实例的时候应该怎么办呢，这里我们就需要用到Dagger2的@Scope属性了，Scope是作用域的意思，我们先自定义一个@Scope注解:
``` java
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PoetryScope {
}
```
同时在Module与Component加上这个自定义Scope:
``` java
@PoetryScope
@Component(modules = {MainModule.class,PoetryModule.class})
public abstract class MainComponent {
    /**
     * 需要用到这个连接器的对象，就是这个对象里面有需要注入的属性
     * （被标记为@Inject的属性）
     * 这里inject表示注入的意思，这个方法名可以随意更改，但建议就
     * 用inject即可。
     */
    abstract void inject(MainActivity activity);

    abstract void inject(OtherActivity activity);

    private static MainComponent sComponent;
    public static MainComponent getInstance(){
        if (sComponent == null){
            sComponent = DaggerMainComponent.builder().build();
        }
        return sComponent;
    }
}

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
```
重新运行：
![运行结果][6]![运行结果][7]
这时你会发现这两个Poetry实例是同一个实例来的，通过实现自定义@Scope注解，标记当前生成对象的使用范围，标识一个类型的注射器只实例化一次，在同一个作用域内，只会生成一个实例，然后在此作用域内共用一个实例。这样看起来很像单例模式，我们可以查看@Singleton其实就是@Scope的一个默认实现而已。当然，你得是同一个Component对象来生成，这点我们应该可以理解的吧。
我们可以通过自定义Scope来组织Component的作用域，使得每个Component的作用域清晰明了，各施其职。
### 组织Component
我们在一个项目之中不可能只使用一个Component连接器来注入对象完成注入工作，一般除了一个全局的ApplicationComponent之外，还有一些作用域在Activity/Fragment的Component，Component之间存在依赖关系与从属关系。如果我们已经创建好了一个全局的ApplicationComponent，然后其它的Component刚好需要ApplicationComponent里面的一个全局属性，想要与ApplicationComponent共享同一个实例，这时就需要用到依赖关系了。
#### 依赖方式
一个Component可以依赖一个或多个Component，并拿到被依赖Component暴露出来的实例，Component的**dependencies**属性就是确定依赖关系的实现。
这里的有点像数学里面的交集方式，被依赖的Component主动暴露对象给二者共享，如我们在ApplicationModule提供了一个全局的Gson对象，我们想要提供给其他Component时，要在ApplicationComponent显式的提供一个接口:
``` java
@Module
public class ApplicationModule {

    /**
     * @Provides 注解表示这个方法是用来创建某个实例对象的，这里我们创建返回Gson对象
     * 方法名随便，一般用provideXXX结构
     * @return 返回注入对象
     */
    @Singleton
    @Provides
    public Gson provideGson(){
        return new Gson();
    }
}

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    Gson getGson();// 暴露Gson对象接口
}
```
并在自定义的MainApplication中初始化它，更改MainComponent:
``` java
public class MainApplication extends Application {

    private ApplicationComponent mApplicationComponent;
    private static MainApplication sApplication;

    public static MainApplication getInstance() {
        return sApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;

        mApplicationComponent = DaggerApplicationComponent.builder().build();
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }
}

//这里表示Component会从MainModule类中拿那些用@Provides注解的方法来生成需要注入的实例
@PoetryScope
@Component(dependencies = ApplicationComponent.class, modules = {MainModule.class,PoetryModule.class})
public abstract class MainComponent {

    /**
     * 需要用到这个连接器的对象，就是这个对象里面有需要注入的属性
     * （被标记为@Inject的属性）
     * 这里inject表示注入的意思，这个方法名可以随意更改，但建议就
     * 用inject即可。
     */
    abstract void inject(MainActivity activity);

    abstract void inject(OtherActivity activity);

    private static MainComponent sComponent;
    public static MainComponent getInstance(){
        if (sComponent == null){
            sComponent = DaggerMainComponent.builder()
                    .applicationComponent(MainApplication.getInstance()
                    .getApplicationComponent())
                    .build();
        }
        return sComponent;
    }
}
```
这样就达到了MainComponent依赖ApplicationComponent。并且这里需要注意的是，MainComponent的作用域不能和ApplicationComponent的作用域一样，否则会报错，一般来讲，我们应该对每个Component都定义不同的作用域。
#### 包含方式（从属方式）@SubComponent
如果我们需要父组件全部的提供对象，这时我们可以用包含方式而不是用依赖方式，相比于依赖方式，包含方式不需要父组件显式显露对象，就可以拿到父组件全部对象。且SubComponent只需要在父Component接口中声明就可以了。添加多一个AActivity,AComponent:
``` java
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface AScope {
}

@Module
public class AModule {

    @AScope
    @Provides
    public Poetry getPoetry(){
        return new Poetry("万物美好");
    }
}

@AScope
@Subcomponent(modules = AModule.class)
public interface AComponent {
    void inject(AActivity activity);
}

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    Gson getGson();// 暴露Gson对象接口

    //AComponent plus();
    AComponent plus(AModule module);//添加声明
}


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

        mApplicationComponent = DaggerApplicationComponent.builder().build();
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

public class AActivity extends AppCompatActivity {
    TextView mTextView;

    @Inject
    Gson mGson;

    @Inject
    Poetry mPoetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);

        MainApplication.getInstance()
                .getAComponent()
                .inject(this);

        mTextView = (TextView) findViewById(R.id.text);
        String text = mPoetry.getPoems()+",mPoetry:"+mPoetry+(mGson == null ? "Gson没被注入" : "Gson已经被注入");
        mTextView.setText(text);
    }
}
```
最后我们在OtherActivity中添加一个按钮跳转到AActivity，运行结果如下:
![运行结果][8]

### @Qualifier 

假如在上面的AActivity里面我们想要注入两个不同的Poetry(指peoms不一样)实例，我们可以在AModule下添加多一个生成Poetry的方法:
``` java
@Module
public class AModule {

    @AScope
    @Provides
    public Poetry getPoetry(){
        return new Poetry("万物美好");
    }
    
    @AScope
    @Provides
    public Poetry getOtherPoetry(){
        return new Poetry("我在中间");
    }
}
```
但是直接这样做Dagger2是无法区分调用哪个方法生成Poetry实例的，这个时候就需要自定义@Qualifier限定符来匹配注入方法了，添加一个自定义Qualifier并修AMoudule，AActivity:
``` java
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PoetryQualifier {
    String value() default "";
}

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
```
重新编译运行：
![运行结果][9]
而Dagger2已经默认帮我们实现了一个@Named:
```java
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface Named {

    /** The name. */
    String value() default "";
}
```
跟我们自定义的PoetryQualifier其实是一样的。
### 后记
这篇是我参考了其他文章之后自己又重新总结一遍的，错误之处请帮忙指出，大家一起进步。除了以上常用到的注解之外，Dagger还提供了其他一些注解，如Set，Map类的注解，具体可以参考以下文章。
### 参考
[Dagger2图文完全教程][10]
[Google官方MVP+Dagger2架构详解【从零开始搭建android框架系列（6）】][11]
[Android：dagger2让你爱不释手-基础依赖注入框架篇][12]
[Android：dagger2让你爱不释手-重点概念讲解、融合篇][13]
[Android：dagger2让你爱不释手-终结篇][14]
[Android:Dagger2学习之由浅入深][15]


  [1]: http://o9qzkbu2x.bkt.clouddn.com/001.png?imageMogr2/auto-orient/thumbnail/500x
  [2]: http://o9qzkbu2x.bkt.clouddn.com/002.png?imageMogr2/auto-orient/thumbnail/500x
  [3]: http://o9qzkbu2x.bkt.clouddn.com/003.png?imageMogr2/auto-orient/thumbnail/500x
  [4]: http://o9qzkbu2x.bkt.clouddn.com/006.png?imageMogr2/auto-orient/thumbnail/400x
  [5]: http://o9qzkbu2x.bkt.clouddn.com/007.png?imageMogr2/auto-orient/thumbnail/400x
  [6]: http://o9qzkbu2x.bkt.clouddn.com/004.png?imageMogr2/auto-orient/thumbnail/400x
  [7]: http://o9qzkbu2x.bkt.clouddn.com/005.png?imageMogr2/auto-orient/thumbnail/400x
  [8]: http://o9qzkbu2x.bkt.clouddn.com/008.png?imageMogr2/auto-orient/thumbnail/500x
  [9]: http://o9qzkbu2x.bkt.clouddn.com/009.png?imageMogr2/auto-orient/thumbnail/500x
  [10]: https://github.com/luxiaoming/dagger2Demo
  [11]: http://www.jianshu.com/p/01d3c014b0b1
  [12]: http://www.jianshu.com/p/cd2c1c9f68d4
  [13]: http://www.jianshu.com/p/1d42d2e6f4a5
  [14]: http://www.jianshu.com/p/65737ac39c44
  [15]: http://www.jianshu.com/p/8fd84680939c