package com.mricefox.archmage.runtime;

import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mricefox.archmage.runtime.Utils.checkNull;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:Boot task which running for long periods of time, used like AsyncTask
 * <p>Date:2018/1/4
 */

public abstract class HeavyBootTask<Result> extends BootNode implements HeavyBootTaskAlias {
    private static final Handler sUIHandler = new Handler(Looper.getMainLooper());
    private static final ExecutorService sExecutor = Executors.newSingleThreadExecutor(new BootThreadFactory("boot-thread"));
    private static final LifecycleHook lifecycleHook = ArchmageAspectPlugins.inst().getLifecycleHook();

    @Override
    protected Class<? extends HeavyBootTaskAlias> alias() {
        return super.alias();
    }

    public final HeavyBootTask before(Class<? extends HeavyBootTaskAlias> alias) {
        checkNull(alias, "Alias");

        HeavyBootTaskManager.inst().addDependency(alias, alias());
        return this;
    }

    public final HeavyBootTask after(Class<? extends HeavyBootTaskAlias> alias) {
        checkNull(alias, "Alias");

        HeavyBootTaskManager.inst().addDependency(alias(), alias);
        return this;
    }

    /**
     * @return whether boot in other process
     */
    protected boolean bootBesideMainProcess() {
        return false;
    }

    final void boot(final Application application, final Bundle extra) {
        Runnable backgroundWork = new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                final Result result = doInBackground(application, extra);

                Runnable postWork = new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(application, result);
                    }
                };
                sUIHandler.postAtFrontOfQueue(createPostExecuteWorkProxy(postWork));
            }
        };

        sExecutor.execute(createBackgroundWorkProxy(backgroundWork));
    }

    /**
     *
     * @param extra extra bundle shared between every boot task
     * @return
     */
    protected abstract Result doInBackground(Application application, Bundle extra);

    protected void onPostExecute(Application application, Result result) {
        //empty
    }

    boolean isFirst() {
        HeavyBootTask first = HeavyBootTaskManager.inst().getFirst();

        if (first == null) {
            //Heavy boot task boot but not added into task manager?
            throw new AssertionError();
        }
        return first.alias() == this.alias();
    }

    boolean isLast() {
        HeavyBootTask last = HeavyBootTaskManager.inst().getLast();

        if (last == null) {
            //Heavy boot task boot but not added into task manager?
            throw new AssertionError();
        }
        return last.alias() == this.alias();
    }

    private Runnable createBackgroundWorkProxy(final Runnable work) {
        return (Runnable) Proxy.newProxyInstance(Runnable.class.getClassLoader(), new Class<?>[]{Runnable.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (isFirst()) {
                    lifecycleHook.onDoInBackgroundStart();
                }

                lifecycleHook.onHeavyBootTaskDoInBackgroundStart(alias());

                long t = System.nanoTime();

                Object o = method.invoke(work, args);

                lifecycleHook.onHeavyBootTaskDoInBackgroundEnd(alias(), System.nanoTime() - t);

                if (isLast()) {
                    lifecycleHook.onAllBackgroundTaskDone();
                }
                return o;
            }
        });
    }

    private Runnable createPostExecuteWorkProxy(final Runnable work) {
        return (Runnable) Proxy.newProxyInstance(Runnable.class.getClassLoader(), new Class<?>[]{Runnable.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (isFirst()) {
                    lifecycleHook.onPostExecuteStart();
                }

                lifecycleHook.onHeavyBootTaskPostExecuteStart(alias());

                long t = System.nanoTime();

                Object o = method.invoke(work, args);

                lifecycleHook.onHeavyBootTaskPostExecuteEnd(alias(), System.nanoTime() - t);

                if (isLast()) {
                    lifecycleHook.onAllPostExecuteDone();
                }
                return o;
            }
        });
    }

    private static final class BootThreadFactory implements ThreadFactory {
        private final AtomicInteger index;
        private final String threadName;

        BootThreadFactory(String threadName) {
            this.threadName = /*"BootThread-" + */threadName;
            this.index = new AtomicInteger();
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, threadName + "-" + index.getAndIncrement());
        }
    }
}
