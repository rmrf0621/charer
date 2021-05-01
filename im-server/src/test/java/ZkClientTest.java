import com.sharer.server.core.IMContanst;
import com.sharer.server.core.distributed.ImNode;
import com.sharer.server.core.utils.JsonUtils;
import com.sharer.server.core.zk.CuratorZKclient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

public class ZkClientTest {


    public static void main(String[] args) throws Exception {
        // 测试连接zookeeper

        CuratorZKclient zKclient = new CuratorZKclient("localhost:2181", "60000");

        CuratorFramework client = zKclient.instance.getClient();

        ImNode imNode = new ImNode();
        imNode.setHost("localhost");
        imNode.setPort(8081);
        imNode.setWebPort(8082);
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(IMContanst.PATH_PREFIX, JsonUtils.toByte(imNode));


        //client.create().forPath(IMContanst.PATH_PREFIX, JsonUtils.toByte(imNode));

        TimeUnit.MINUTES.sleep(1);

    }


}
