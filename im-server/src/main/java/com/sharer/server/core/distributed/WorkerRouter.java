package com.sharer.server.core.distributed;

import com.sharer.common.IMContanst;
import com.sharer.server.core.proto.RequestProto;
import com.sharer.server.core.utils.JsonUtils;
import com.sharer.server.core.utils.ThreadUtil;
import com.sharer.server.core.zk.CuratorZKclient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
@Data
@Slf4j
public class WorkerRouter {
    //Zk客户端
    private CuratorFramework client = null;

    private String pathRegistered = null;
    private ImNode node = null;


    private static WorkerRouter singleInstance = null;
    private static final String path = IMContanst.MANAGE_PATH;

    private ConcurrentHashMap<Long, PeerSender> workerMap =
            new ConcurrentHashMap<>();


    private BiConsumer<ImNode, PeerSender> runAfterAdd = (node, relaySender) -> {
        doAfterAdd(node, relaySender);
    };

    private Consumer<ImNode> runAfterRemove = (node) -> {
        doAfterRemove(node);
    };


    public synchronized static WorkerRouter getInst() {
        if (null == singleInstance) {
            singleInstance = new WorkerRouter();
        }
        return singleInstance;
    }

    private WorkerRouter() {
    }

    private boolean inited = false;

    /**
     * 初始化节点管理
     */
    public void init() {

        if (inited) {
            return;
        }
        inited = true;

        try {
            if (null == client) {
                this.client = CuratorZKclient.instance.getClient();

            }

            //订阅节点的增加和删除事件

            PathChildrenCache childrenCache = new PathChildrenCache(client, path, true);
            PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {

                @Override
                public void childEvent(CuratorFramework client,
                                       PathChildrenCacheEvent event) throws Exception {
                    log.info("开始监听其他的ImWorker子节点:-----");
                    ChildData data = event.getData();
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            log.info("CHILD_ADDED : " + data.getPath() + "  数据:" + data.getData());
                            processNodeAdded(data);
                            break;
                        case CHILD_REMOVED:
                            log.info("CHILD_REMOVED : " + data.getPath() + "  数据:" + data.getData());
                            processNodeRemoved(data);
                            break;
                        case CHILD_UPDATED:
                            log.info("CHILD_UPDATED : " + data.getPath() + "  数据:" + new String(data.getData()));
                            break;
                        default:
                            log.debug("[PathChildrenCache]节点数据为空, path={}", data == null ? "null" : data.getPath());
                            break;
                    }

                }

            };

            childrenCache.getListenable().addListener(
                    childrenCacheListener, ThreadUtil.getIoIntenseTargetThreadPool());
            System.out.println("Register zk watcher successfully!");
            childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processNodeRemoved(ChildData data) {

        byte[] payload = data.getData();
        ImNode node = JsonUtils.bytes2Object(payload, ImNode.class);

        long id = ImWorker.getInst().getIdByPath(data.getPath());
        node.setId(id);
        log.info("[TreeCache]节点删除, path={}, data={}",
                data.getPath(), JsonUtils.toJSONString(node));


        if (runAfterRemove != null) {
            runAfterRemove.accept(node);
        }


    }

    private void doAfterRemove(ImNode node) {
        PeerSender peerSender = workerMap.get(node.getId());

        if (null != peerSender) {
            peerSender.stopConnecting();
            workerMap.remove(node.getId());
        }


    }

    /**
     * 节点增加的处理
     *
     * @param data 新节点
     */
    private void processNodeAdded(ChildData data) {
        byte[] payload = data.getData();
        ImNode node = JsonUtils.bytes2Object(payload, ImNode.class);

        long id = ImWorker.getInst().getIdByPath(data.getPath());
        node.setId(id);

        log.info("[TreeCache]节点更新端口, path={}, data={}",
                data.getPath(), JsonUtils.toJSONString(node));

        if (node.equals(getLocalNode())) {
            log.info("[TreeCache]本地节点, path={}, data={}",
                    data.getPath(), JsonUtils.toJSONString(node));
            return;
        }
        PeerSender relaySender = workerMap.get(node.getId());
        //重复收到注册的事件
        if (null != relaySender && relaySender.getRmNode().equals(node)) {

            log.info("[TreeCache]节点重复增加, path={}, data={}",
                    data.getPath(), JsonUtils.toJSONString(node));
            return;
        }

        if (runAfterAdd != null) {
            runAfterAdd.accept(node, relaySender);
        }
    }


    private void doAfterAdd(ImNode n, PeerSender relaySender) {
        if (null != relaySender) {
            //关闭老的连接
            relaySender.stopConnecting();
        }
        //创建一个消息转发器
        relaySender = new PeerSender(n);
        //建立转发的连接
        relaySender.doConnect();

        workerMap.put(n.getId(), relaySender);
    }


    public PeerSender route(long nodeId) {
        PeerSender peerSender = workerMap.get(nodeId);
        if (null != peerSender) {
            return peerSender;
        }
        return null;
    }


    public void sendNotification(String json) {
        workerMap.keySet().stream().forEach(
                key ->
                {
                    if (!key.equals(getLocalNode().getId())) {
                        PeerSender peerSender = workerMap.get(key);
                        RequestProto.Message pkg = null;//NotificationMsgBuilder.buildNotification(json);
                        peerSender.writeAndFlush(pkg);
                    }
                }
        );

    }


    public ImNode getLocalNode() {
        return ImWorker.getInst().getLocalNodeInfo();
    }

    public void remove(ImNode remoteNode) {
        workerMap.remove(remoteNode.getId());
        log.info("[TreeCache]移除远程节点信息,  node={}", JsonUtils.toJSONString(remoteNode));
    }
}
