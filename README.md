# Archmage

Archmage是一个android组件化框架，组件之间可以用类似OSGI的导入\导出部分包实现互相依赖，最小化依赖范围，从根本上解除耦合。
> [android组件化协议管理的一种思路](http://www.mricefox.com/2018/04/24/android-modularity-protocal/)

在此基础上，实现了组件之间的服务通信、页面跳转，以及各组件自定义启动任务。

## Getting Started

### 1. 插件配置
在根工程的build.gradle中添加插件
```gradle
buildscript {
	dependencies {
		classpath "com.mricefox.archmage.build.gradle:archmage-gradle-plugin:1.0.0"	
	}
}
```

在宿主组件（一般是app）build.gradle中应用插件
```gradle
apply plugin: 'archmage-build-plugin'
```

对于两个相互没有依赖却又有通信需求的组件来说，服务提供方可以导出包给其他组件使用
```gradle
apply plugin: 'archmage-build-plugin'
archmage {
    exportPackages = ['com.mricefox.archmage.sample.hotel.export']
}
```

而需要调用服务的组件可以从其他组件导入包
```gradle
apply plugin: 'archmage-build-plugin'
archmage {
	//project
    provided(project(':hotel')) {
        importPackages = ['com.mricefox.archmage.sample.hotel.export']
    }
	//aar
	provided('com.mricefox.archmage.sample.ticket:ticket:1.0.0') {
        importPackages = ['com.mricefox.archmage.sample.ticket.export']
    }
}
```
之后点击Android Studio中的Gradle Sync按钮即可 [演示Gif](https://github.com/MrIceFox/Archmage/raw/master/art/export_package_step_by_step.gif)



### 2. 运行时代码配置
#### 初始化
```java
public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Archmage.install(this, false);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Archmage.terminate(this);
    }
}
```

#### 服务通信
```java
public interface HotelService extends IService {
    //...
}

//直接获取服务,失败将抛出ServiceNotFoundException，不建议采用这种方式
HotelBean bean = Archmage.service(HotelService.class).getHotelDetail(3);

//通过回调获取服务，建议采用这种方式
Archmage.service(HotelService.class, new ServiceFindCallback<HotelService>() {
	@Override
	public void found(HotelService hotelService) {
		HotelBean bean = hotelService.getHotelDetail(5);
	}

	@Override
	public void notFound(Class<HotelService> alias) {
		//Service no found
	}
});
```

#### 注册服务实现
```java
@ServiceImpl
public class HotelServiceImpl implements HotelService {
	//...
}
```

#### 注册Activity或者Fragment
```java
//Fragment
@Target(path = "/share/ShareArea")
public class ShareFragment extends Fragment {
	//...
}

//Activity
@Target(path = "/pay/PayPage")
public class PayActivity extends AppCompatActivity {
	//...
}
```
也就是在Fragment或者Activity前面加上@Target(path = "/组件前缀/页面名称")

#### Activity跳转
```java
//直接跳转Activity,失败将抛出TargetNotFoundException
Archmage.transfer(DefaultTargetUriParser.createUri("pay", "PayPage"))
        .activity()
		.intent(new Intent().putExtra("source", "ticket"))
		.startForResult(TicketListActivity.this, 5);

//通过回调跳转Activity               
Archmage.transfer(DefaultTargetUriParser.createUri("pay", "PayPage"))
		.activity(new TargetFindCallback<Transfer.TargetActivity>() {
			@Override
            public void found(Transfer.TargetActivity targetActivity) {
            	targetActivity.intent(new Intent().putExtra("source", "ticket"))
                            .startForResult(TicketListActivity.this, 5);
            }

            @Override
            public void notFound(Uri uri) {
            	//Activity not found
            }
        });
```

#### Fragment获取
```java
//直接获取,失败将抛出TargetNotFoundException
Bundle bundle = new Bundle();
bundle.putString("source", "ticket");
Fragment fragment = Archmage.transfer(DefaultTargetUriParser.createUri("share", "ShareArea"))
        .fragmentV4()
        .arguments(bundle)
        .get();

//通过回调获取
Archmage.transfer(DefaultTargetUriParser.createUri("share", "ShareArea"))
        .fragmentV4(new TargetFindCallback<Transfer.TargetFragmentV4>() {
            @Override
            public void found(Transfer.TargetFragmentV4 targetFragmentV4) {
                Bundle bundle = new Bundle();
                bundle.putString("source", "ticket");
                Fragment fragment = targetFragmentV4.arguments(bundle).get();
            }

            @Override
            public void notFound(Uri uri) {
				//Fragment not found
            }
        });
````

#### 组件自定义启动任务
假设有如下组件依赖关系
![](https://github.com/MrIceFox/Archmage/raw/master/art/sample_module_dependencies.png)

他们有下面的启动任务和依赖关系
![](https://github.com/MrIceFox/Archmage/raw/master/art/sample_boot_task.png)
除白色方块表示的基础组件代码对上层所有组件可见外，其他上层组件（酒店、门票、账号）互相之间并没有依赖关系。这里的酒店组件并不知道账户组件有些什么启动任务，账户组件需要给自己的启动任务设置一个别名暴露给酒店组件
```java
public interface AccountBootAlias extends LightBootTaskAlias {
	//这个接口放在export包里
}
```
然后账号组件需要一个入口来声明自己的启动任务，注意**需要添加`com.mricefox.archmage.annotation.Module`这个注解**
```java
@Module
public class AccountModule extends ArchmageModule {
    @Override
    protected Class<? extends LightBootTaskAlias> alias() {
		//设置别名，默认是this.getClass()
        return AccountBootAlias.class;
    }
	//...
}
```
酒店组件的启动入口，声明启动顺序，需要导入账号的export包，以便引用AccountBootAlias
```java
@Module
public class HotelModule extends ArchmageModule {

    @Override
    protected void declareBootDependency() {
		//在AccountModule之后启动
        dependsOn(AccountBootAlias.class);
    }
}
```
##### 轻量启动任务
可以定义LightBootTask作为启动任务，上面的组件入口ArchmageModule本身也是一个启动任务。**注意轻量启动任务的boot方法都是在主线程上被调用**
```java
public class HotelSdkInitTask extends LightBootTask {
    @Override
    protected void boot(Application application, Bundle extra) {
        //具体的任务
    }
}
```

在组件的启动入口declareBootDependency方法中加入
```java
@Module
public class HotelModule extends ArchmageModule {

    @Override
    protected void declareBootDependency() {
		//添加一个轻量启动任务
        addLightBootTask(new HotelSdkInitTask());
    }
}
```

具体的使用可以参考[sample](https://github.com/MrIceFox/Archmage/tree/master/sample)
##### 耗时启动任务
一些比较耗时的任务可以放在后台线程执行，减少app启动时间，使用上类似AsyncTask，doInBackground方法在后台线程调用，onPostExecute方法在UI线程调用
```java
public class PushSdkInitTask extends HeavyBootTask<String> {

    @Override
    protected String doInBackground(Application application, Bundle extra) {
		//后台耗时任务
        Log.d(Constants.BOOT_TASK_TAG, "push sdk boot, run in thread:" + Thread.currentThread().getName());
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Channel_X";
    }

    @Override
    protected void onPostExecute(Application application, String s) {
		//后台任务结果转到前台
        Log.d(Constants.BOOT_TASK_TAG, "push sdk boot, bg result:" + s);
        Log.d(Constants.BOOT_TASK_TAG, "push sdk boot, run in thread:" + Thread.currentThread().getName());
    }

    @Override
    protected boolean bootBesideMainProcess() {
		//是否在app进程以外的进程启动该任务
        return false;
    }
}
```
这里HeavyBootTask之间也可以用before、after设置启动顺序，注意**HeavyBootTask将在所有的LightBootTask执行完毕之后开始执行**
具体的使用可以参考[sample](https://github.com/MrIceFox/Archmage/tree/master/sample)
#### AOP监控
对Archmage框架执行的各种节点进行监控，注意需要在Archmage.install之前注册
```java
ArchmageAspectPlugins.inst().registerDependencyLookupHook
ArchmageAspectPlugins.inst().registerDependencyInjectionHook
ArchmageAspectPlugins.inst().registerLifecycleHook
```

### 3. 混淆规则
```
-keep public class * extends com.mricefox.archmage.runtime.ModuleActivator
```

## License

This project is licensed under the MIT License - see the [LICENSE](https://raw.githubusercontent.com/MrIceFox/Archmage/master/LICENSE) file for details