package com.dlut.toyRPC.registry;

import com.dlut.toyRPC.client.ConnectManage;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * zooKeeper依然不熟悉，对于多服务器应该在这里进行改进
 */
public class ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private volatile List<String>dataList = new ArrayList<>();

    private String registryAddress;
    private ZooKeeper zooKeeper;




    public ServiceDiscovery(String registryAddress){
        this.registryAddress = registryAddress;
        zooKeeper = connectServer();
        if(zooKeeper != null){
            watchNode(zooKeeper);
        }
    }
    public String discover(){
        String data = null;
        int size = dataList.size();
        if(size > 0){
            if(size == 1){
                data = dataList.get(0);
                logger.debug("using only data: {}",data);
            }else{
                data = dataList.get(ThreadLocalRandom.current().nextInt(size));
                logger.debug("using random data: {}",data);
            }
        }
        return data;
    }
    private ZooKeeper connectServer(){
        ZooKeeper zk = null;
        try{
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if(event.getState() == Event.KeeperState.SyncConnected){
                        latch.countDown();
                    }
                }
            });
            latch.await();
        }catch(Exception e){
            logger.error("ZooKeeper Error" + e);
        }
        return zk;
    }

    private void watchNode(final ZooKeeper zk){
        try{
            List<String>nodeList = zk.getChildren(Constant.ZK_REGISTERY_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if(event.getType() == Event.EventType.NodeChildrenChanged){
                        watchNode(zk);
                    }
                }
            });
            List<String>dataList = new ArrayList();
            for(String node :nodeList){
                byte[]bytes = zk.getData(Constant.ZK_REGISTERY_PATH + "/" + node,false,null);
                dataList.add(new String (bytes));
                logger.debug("node data: {}",dataList);
            }
            this.dataList = dataList;
            UpdateConnectedServer();
        }catch(Exception e){
            logger.error("Zookeeper watch node error: " + e);
        }
    }

    private void UpdateConnectedServer(){
        ConnectManage.getInstance().updateConnectedServer(this.dataList);
    }

    public void stop(){
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{1,2});

        int a = queue.peek()[0];
        if(zooKeeper != null){
            try{
                zooKeeper.close();
            }catch(Exception e){
                logger.error("Service Discovery error : " + e);
            }
        }
    }
}
