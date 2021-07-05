package com.dlut.toyRPC.client;

import com.dlut.toyRPC.client.proxy.IAsyncObjectProxy;
import com.dlut.toyRPC.client.proxy.ObjectProxy;
import com.dlut.toyRPC.registry.ServiceDiscovery;

import java.lang.reflect.Proxy;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcClient {
    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;
    private static int threads = 16;
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(threads, threads, 600L,
            TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));


    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }
    public static void setThreads(int num){
        threads = num;
    }
    public RpcClient(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    /**
     * 创建同步代理
     * @param interfaceClass
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ObjectProxy<T>(interfaceClass)
        );
    }

    /**
     * 创建异步代理，其实也没那么同步异步，异步就是可以加callBacks的代理
     * @param interfaceClass
     * @param <T>
     * @return
     */
    public static <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<T>(interfaceClass);
    }

    public void stop() {
        threadPoolExecutor.shutdown();
        serviceDiscovery.stop();
        ConnectManage.getInstance().stop();
    }
}
