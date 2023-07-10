package com.vertx.common;

import cn.hutool.log.StaticLog;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.util.Arrays;


/**
 * 集群启动类
 * <p>
 * @author huan
 */
public class MainLaunch extends Launcher {

    /**
     * 定义集群成员IP地址
     */
    private final String[] CLUSTER_IPS = {"127.0.0.1"};

    @Override
    public void beforeStartingVertx(VertxOptions vertxOptions) {
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
        vertxOptions.setClusterManager(mgr);
        // 集群启动上下文
        JsonObject startVerticleContext = new JsonObject();
        // 读取配置文件
        Vertx.clusteredVertx(vertxOptions).compose(vertx -> {
            startVerticleContext.put("vertx", vertx);
            return com.vertx.common.config.InitVertxKt.loadConfig(vertx);
        }).compose(appConfig -> {
            startVerticleContext.put("appConfig", appConfig);
            return Future.succeededFuture();
        }).compose(o -> {
            final Vertx vertx = (Vertx) startVerticleContext.getValue("vertx");
            final com.vertx.common.entity.AppConfig appConfig = (com.vertx.common.entity.AppConfig) startVerticleContext.getValue("appConfig");
            final DeploymentOptions deploymentOptions = new DeploymentOptions();
            final com.vertx.common.entity.Vertx configVertx = appConfig.getVertx();
            deploymentOptions.setInstances(configVertx.getInstances());
            deploymentOptions.setHa(configVertx.getHa());
            final String vertxVerticle = configVertx.getVerticle();
            return vertx.deployVerticle(vertxVerticle, deploymentOptions);
        }).onComplete(res -> {
            if (res.succeeded()) {
                StaticLog.info("集群启动成功");
            } else {
                StaticLog.error(res.cause(), "集群启动失败");
            }
        });
    }

    public static void main(String[] args) {
        new MainLaunch().dispatch(args);
    }
}
