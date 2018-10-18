package com.starter.rpc_client;

import com.starter.rpc_common.Constant;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author spuerKun
 * @date 2018/10/8.
 */
public class ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);

    private String registryAddress;

    private volatile List<String> dataList = new ArrayList<>();

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public ServiceDiscovery(String registryAddress) {
        this.registryAddress = registryAddress;

        ZooKeeper zk = connectServer();
        if (zk != null) {
            watchNode(zk);
        }
    }

    public String discover() {
        String data = null;
        int size = dataList.size();
        if (size > 0) {
            if (size == 1) {
                data = dataList.get(0);
                LOGGER.debug("using only data: {}", data);
            } else {
                data = dataList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("using random data: {}", data);
            }
        }
        return data;
    }

    /**
     * 连接zookeeper
     */
    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
        } catch (InterruptedException | IOException e) {
            LOGGER.error("serviceDiscovery.connectServer.error", e);
        }
        return zk;
    }

    private void watchNode(final ZooKeeper zooKeeper) {
        try {
            List<String> nodeList = zooKeeper.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        watchNode(zooKeeper);
                    }
                }
            });

            List<String> dataList = new ArrayList<>();
            for (String node : nodeList) {
                byte[] bytes = zooKeeper.getData(Constant.ZK_REGISTRY_PATH + "/" + node, false, null);
                dataList.add(new String(bytes));
            }
            this.dataList = dataList;
            LOGGER.debug("node data: {}", dataList);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("serviceDiscovery.watchNode.error", e);
        }
    }
}
