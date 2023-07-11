package com.vertx.common;

import cn.hutool.log.StaticLog;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.vertx.common.config.AppStartKt;
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
        StaticLog.info("执行钩子函数:{}", "afterConfigParsed");
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
        AppStartKt.appStart(vertxOptions);
    }

    @Override
    public void afterStartingVertx(Vertx vertx) {
        StaticLog.info("执行钩子函数:{}", "afterStartingVertx");
    }

    @Override
    public void beforeDeployingVerticle(DeploymentOptions deploymentOptions) {
        StaticLog.info("执行钩子函数:{}", "beforeDeployingVerticle");
        // 设置实例数量为0 防止重复启动 目前没找到更好的解决方案
        deploymentOptions.setInstances(0);
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
