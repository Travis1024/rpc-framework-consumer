package com.space.travis.client.transport;

import java.util.concurrent.*;

/**
 * @ClassName RpcFuture
 * @Description 异步执行结果获取
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/3/3
 */
public class RpcFuture<T> implements Future<T> {

    /**
     * 响应结果
     */
    private T response;
    /**
     * 使用 CountDownLatch 等待线程，当获取到响应的时候setResponse函数被调用，countDownLatch减到0，阻塞的get开始执行。
     * 因为一个请求对应一个future对象，所以初始设置为1
     */
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        // 进入阻塞状态，等待countDownLatch减少值为0 返回响应结果
        countDownLatch.await();
        return response;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (countDownLatch.await(timeout, unit)) {
            return response;
        }
        // 如果超时，则返回null
        return null;
    }

    /**
     * 当handler获取到响应时，调用此方法，设置响应结果，让countDownLatch减到0，使得被阻塞的get方法开始执行
     * @param response
     */
    public void setResponse(T response){
        this.response = response;
        countDownLatch.countDown();
    }
}
