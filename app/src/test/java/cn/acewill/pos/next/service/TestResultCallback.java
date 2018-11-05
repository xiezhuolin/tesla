package cn.acewill.pos.next.service;

import cn.acewill.pos.next.exception.PosServiceException;

/**
 * Created by Acewill on 2016/6/17.
 */
public class TestResultCallback<T> implements ResultCallback<T> {
    private Object testLock;
    private T result;
    private PosServiceException exception;

    public TestResultCallback(Object testLock) {
        this.testLock = testLock;
    }

    @Override
    public void onResult(T result) {

        this.result = result;
        synchronized(testLock) {
            testLock.notifyAll();
        }
    }

    @Override
    public void onError(PosServiceException e) {

        synchronized(testLock) {
            testLock.notifyAll();
        }

        this.exception = e;
    }

    public void waitForComplete() throws InterruptedException {
        synchronized(testLock) {
            testLock.wait();
        }
    }

    public T getResult() {
        return result;
    }


    public PosServiceException getException() {
        return exception;
    }


}
