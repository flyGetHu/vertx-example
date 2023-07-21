/**
 * This class is responsible for launching a Vert.x cluster. It extends VertxCommandLauncher and implements VertxLifecycleHooks.
 * It sets up HazelcastClusterManager as the cluster manager and configures the EventBusOptions for the cluster.
 * It also defines several hooks that are executed at different stages of the Vert.x lifecycle.
 * The class has a main method that dispatches the command line arguments to VertxCommandLauncher.
 * The class is located at d:\workspace\java\vertx-example\common\src\main\java\com\vertx\common\MainLaunch.java.
 */
package com.vertx.common;

import cn.hutool.log.StaticLog;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.vertx.common.config.VertxLoadConfigKt;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.impl.launcher.VertxCommandLauncher;
import io.vertx.core.impl.launcher.VertxLifecycleHooks;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


/**
 * 集群启动类
 * <p>
 * java -jar example-fat.jar -cluster --vertx-id=example-service
 *
 * @author huan
 */
public class MainLaunch extends VertxCommandLauncher implements VertxLifecycleHooks {

    /**
     * 定义线上集群成员IP地址
     * <p>
     */
    private final String[] CLUSTER_IPS = {"127.0.0.1"};

    @Override
    public void afterConfigParsed(JsonObject config) {
        StaticLog.info("执行钩子函数:{}/{}", "afterConfigParsed", config.encode());
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
        final EventBusOptions eventBusOptions = vertxOptions.getEventBusOptions();
        // 设置bus集群超时时间
        eventBusOptions.setConnectTimeout(1000 * 5);
        // 设置集群ping时间
        eventBusOptions.setClusterPingInterval(TimeUnit.SECONDS.toMillis(10));
        // 设置集群ping回复时间
        eventBusOptions.setClusterPingReplyInterval(TimeUnit.SECONDS.toMillis(10));
        // 配置打包线上配置 会启用配置文件conf/config-prod.yaml
        VertxLoadConfigKt.setActive("prod");
    }

    @Override
    public void afterStartingVertx(Vertx vertx) {
        StaticLog.info("执行钩子函数:{},{}", "afterStartingVertx", vertx.isClustered());
    }

    @Override
    public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
        StaticLog.info("执行钩子函数:{}", "beforeDeployingVerticle");
        deploymentOptions.setInstances(1);
        deploymentOptions.setHa(true);
    }

    @Override
    public void beforeStoppingVertx(Vertx vertx) {
        StaticLog.info("执行钩子函数:{}", "beforeStoppingVertx");
    }

    @Override
    public void afterStoppingVertx() {
        StaticLog.info("执行钩子函数:{}", "afterStoppingVertx");
    }

    @Override
    public void handleDeployFailed(Vertx vertx, String mainVerticle, DeploymentOptions deploymentOptions, Throwable cause) {
        StaticLog.info("执行钩子函数:{}", "handleDeployFailed");
        vertx.close();
    }

    @Override
    public void dispatch(String[] args) {
        StaticLog.info("启动参数:{}", Arrays.toString(args));
        super.dispatch(args);
    }

    public static void main(String[] args) {
        new MainLaunch().dispatch(args);
    }
}
