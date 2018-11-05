package cn.acewill.pos.next.service;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import rx.android.plugins.RxAndroidPlugins;
import rx.plugins.RxJavaPlugins;

/**
 * Created by Acewill on 2016/6/17.
 */
public class RxJavaResetRule implements TestRule {

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                //before: plugins reset, execution and schedulers hook defined
                RxAndroidPlugins.getInstance().reset();
                RxAndroidPlugins.getInstance().registerSchedulersHook(new SchedulerHook());
                //RxJavaPlugins.getInstance().registerObservableExecutionHook(new ExecutionHook());

                base.evaluate();

                //after: clean up
                RxAndroidPlugins.getInstance().reset();
            }
        };
    }
}
