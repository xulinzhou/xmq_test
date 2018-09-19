package com.xmq.resolver;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;

import java.util.List;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.resolver
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/18 16:00
 * @Version: 1.0
 */
public class ZKClient {
    private final CuratorFramework client;

    public ZKClient(String adds) {
        client = CuratorFrameworkFactory.newClient(adds, new RetryOneTime(1));
        client.start();
    }
    public CuratorFramework getClient() {
        return client;
    }

    public List<String> getChildren(String path) throws Exception {
        return client.getChildren().forPath(path);
    }

    public PathChildrenCache listenChildrenPath(String path, PathChildrenCacheListener listener, boolean buildCache) throws Exception {
        PathChildrenCache cache = new PathChildrenCache(client, path, false);
        cache.getListenable().addListener(listener);
        cache.start(buildCache);
        return cache;
    }
    public String addEphemeralNode(String parentNode, String node) throws Exception {
        return addEphemeralNode(ZKPaths.makePath(parentNode, node));
    }
    public String addEphemeralNode(String path) throws Exception {
        return client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
    }
    public void addPersistentNode(String node) throws Exception {
        try {
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(node);
        } catch (KeeperException.NodeExistsException e) {
        } catch (Exception e) {
            throw new Exception("addPersistentNode error", e);
        }
    }

}
