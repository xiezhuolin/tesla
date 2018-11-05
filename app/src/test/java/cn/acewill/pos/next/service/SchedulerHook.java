package cn.acewill.pos.next.service;

import rx.Scheduler;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

/**
 * 模拟安卓界面线程，否则跑RxAndroid代码会提示没有Loop
 * Created by Acewill on 2016/6/17.
 */
public class SchedulerHook extends RxAndroidSchedulersHook {

    @Override
    public Scheduler getMainThreadScheduler() {
        return Schedulers.immediate();
    }
}
