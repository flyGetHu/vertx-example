package com.vertx.common;

import cn.hutool.log.StaticLog;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.vertx.common.config.InitVertxKt;
import com.vertx.common.entity.AppConfig;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.launcher.VertxCommandLauncher;
import io.vertx.core.impl.launcher.VertxLifecycleHooks;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.util.Arrays;


/**
 * 集群启动类
 * <p>
 *
 * @author huan
 */
public class MainLaunch extends VertxCommandLauncher implements VertxLifecycleHooks {

    /**
     * 定义集群成员IP地址
     */
    private final String[] CLUSTER_IPS = {"127.0.0.1"};

    @Override
    public void afterConfigParsed(JsonObject config) {

    }

    @Override
    public void beforeStartingVertx(VertxOptions vertxOptions) {
        StaticLog.info("执行钩子函数:{}", "beforeStartingVertx");
        final Config config = new Config();
        // 获取网络配置
        final NetworkConfig networkConfig = config.getNetworkConfig();

        // 获取Join配置
        final JoinConfig joinConfig = networkConfig.getJoin();
        // 启用TCP/IP发现机制
        joinConfig.getTcpIpConfig().setEnabled(true);
        // 添加成员节点的IP地址和端口
        joinConfig.getTcpIpConfig().setMembers(Arrays.asList(CLUSTER_IPS));
        final ClusterManager mgr = new HazelcastClusterManager(config);
        vertxOptions.setClusterManager(mgr);
        // 集群启动上下文
        JsonObject startVerticleContext = new JsonObject();
        // 读取配置文件
        Vertx.clusteredVertx(vertxOptions).compose(vertx -> {
            StaticLog.info("集群启动成功:{}", vertx.isClustered());
            startVerticleContext.put("vertx", vertx);
            return InitVertxKt.loadConfig(vertx);
        }).compose(appConfig -> {
            StaticLog.info("获取配置文件成功");
            startVerticleContext.put("appConfig", appConfig);
            return Future.succeededFuture();
        }).compose(o -> {
            final Vertx vertx = (Vertx) startVerticleContext.getValue("vertx");
            final AppConfig appConfig = (AppConfig) startVerticleContext.getValue("appConfig");
            final DeploymentOptions deploymentOptions = new DeploymentOptions();
            final com.vertx.common.entity.Vertx configVertx = appConfig.getVertx();
            deploymentOptions.setInstances(configVertx.getInstances());
            deploymentOptions.setHa(configVertx.getHa());
            final String vertxVerticle = configVertx.getVerticle();
            return vertx.deployVerticle(vertxVerticle, deploymentOptions);
        }).onComplete(res -> {
            if (res.succeeded()) {
                StaticLog.info("项目启动成功");
            } else {
                StaticLog.error(res.cause(), "项目启动失败");
            }
        });
    }

    @Override
    public void afterStartingVertx(Vertx vertx) {

    }

    @Override
    public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
        // 设置实例数量为0 防止重复启动 目前没找到更好的解决方案
        deploymentOptions.setInstances(0);
    }

    @Override
    public void beforeStoppingVertx(Vertx vertx) {

    }

    @Override
    public void afterStoppingVertx() {

    }

    @Override
    public void handleDeployFailed(Vertx vertx, String mainVerticle, DeploymentOptions deploymentOptions, Throwable cause) {

    }

    public static void main(String[] args) {
        new MainLaunch().dispatch(args);
    }
}
