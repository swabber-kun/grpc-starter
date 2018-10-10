package com.starter.rpc_server;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author spuerKun
 * @date 2018/10/8.
 */
public class ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    private static final int ZK_SESSION_TIMEOUT = 5000;

    private static final String ZK_REGISTRY_PATH = "/registry";

    private static final String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";

    private String registryAddress;

    private CountDownLatch latch = new CountDownLatch(1);

    public ServiceRegistry(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public void register(String data) {
        if (data != null) {
            ZooKeeper zk = connectServer();
            if (zk != null) {
                createNode(zk, data);
            }
        }
    }

    /**
     * 连接zookeeper
     */
    private ZooKeeper connectServer() {
        ZooKeeper zooKeeper = null;

        try {
            zooKeeper = new ZooKeeper(registryAddress, ZK_SESSION_TIMEOUT, new Watcher() {

                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return zooKeeper;
    }

    /**
     * 在zk上创建节点
     *
     * @param zooKeeper
     * @param data
     */
    private void createNode(ZooKeeper zooKeeper, String data) {
        byte[] bytes = data.getBytes();
        try {
            String path = zooKeeper.create(ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            LOGGER.debug("create zookeeper node ({} => {})", path, data);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }
    }
}
