package com.mricefox.archmage.runtime;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/1/30
 */

public abstract class LifecycleHook {

    public void onStart() {
    }

    public void onDeclareDependencyStart() {
    }

    public void onDeclareDependencyEnd(long nanos) {
    }

    public void onFlattenDependencyStart() {
    }

    public void onFlattenDependencyEnd(long nanos) {
    }

    public void onLightBootTaskStart(Class<? extends LightBootTaskAlias> alias) {
    }

    public void onLightBootTaskEnd(Class<? extends LightBootTaskAlias> alias, long nanos) {
    }

    public void onAllLightBootTaskDone() {

    }

    public void onDoInBackgroundStart() {
    }

    public void onHeavyBootTaskDoInBackgroundStart(Class<? extends HeavyBootTaskAlias> alias) {
    }

    public void onHeavyBootTaskDoInBackgroundEnd(Class<? extends HeavyBootTaskAlias> alias, long nanos) {
    }

    public void onAllBackgroundTaskDone() {
    }

    public void onPostExecuteStart() {
    }

    public void onHeavyBootTaskPostExecuteStart(Class<? extends HeavyBootTaskAlias> alias) {
    }

    public void onHeavyBootTaskPostExecuteEnd(Class<? extends HeavyBootTaskAlias> alias, long nanos) {
    }

    public void onAllPostExecuteDone() {
    }
}
