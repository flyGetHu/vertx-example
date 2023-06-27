package com.vertx.common;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.util.Arrays;

public class MainLaunch extends Launcher {

    public Logger logger = LoggerFactory.getLogger(MainLaunch.class);


    /**
     * 定义集群成员IP地址
     */
    private static final String[] CLUSTER_IPS = {""};

    @Override
    public void beforeStartingVertx(VertxOptions options) {
        Config config = new Config();
        // 获取网络配置
        NetworkConfig networkConfig = config.getNetworkConfig();

        // 获取Join配置
        JoinConfig joinConfig = networkConfig.getJoin();
        // 启用TCP/IP发现机制
        joinConfig.getTcpIpConfig().setEnabled(true);
        // 添加成员节点的IP地址和端口
        joinConfig.getTcpIpConfig().setMembers(Arrays.asList(CLUSTER_IPS));
        ClusterManager mgr = new HazelcastClusterManager(config);
        options.setClusterManager(mgr);
        Vertx.clusteredVertx(options);
        logger.info("启动集群成功");
    }


    public static void main(String[] args) {
        new MainLaunch().dispatch(args);
    }
}
