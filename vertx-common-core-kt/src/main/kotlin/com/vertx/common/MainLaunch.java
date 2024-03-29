/**
 * This class is responsible for launching a Vert.x cluster. It extends VertxCommandLauncher and implements VertxLifecycleHooks.
 * It sets up HazelcastClusterManager as the cluster manager and configures the EventBusOptions for the cluster.
 * It also defines several hooks that are executed at different stages of the Vert.x lifecycle.
 * The class has a main method that dispatches the command line arguments to VertxCommandLauncher.
 * The class is located at d:\workspace\java\vertx-example\common\src\main\java\com\vertx\common\MainLaunch.java.
 */
package com.vertx.common;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.TcpIpConfig;
import com.vertx.common.config.VertxLoadConfigKt;
import com.vertx.common.enums.EnvEnum;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.impl.launcher.VertxCommandLauncher;
import io.vertx.core.impl.launcher.VertxLifecycleHooks;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.ConfigUtil;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


/**
 * 集群启动类
 * <p>
 * java -jar example-fat.jar -active=dev -cluster --vertx-id=example-service
 * <p>
 * -active=dev 激活指定配置文件,prod为生产环境,dev为开发环境,test为测试环境
 * -cluster 集群模式
 * --vertx-id=example-service 指定vertx的id
 *
 * @author huan
 */
public class MainLaunch extends VertxCommandLauncher implements VertxLifecycleHooks {

    /**
     * 定义线上集群成员IP地址
     * <p>
     */
    private final String[] CLUSTER_IPS;

    /**
     * Initializes MainLaunch with the provided cluster IPs.
     *
     * @param CLUSTER_IPS An array of cluster IPs.
     */
    public MainLaunch(String[] CLUSTER_IPS) {
        this.CLUSTER_IPS = CLUSTER_IPS;
    }


    public static void main(String[] args) {
        final String[] clusterIps = new String[]{"127.0.0.1"};
        new MainLaunch(clusterIps).dispatch(args);
    }

    @Override
    public void afterConfigParsed(JsonObject config) {
        // 隐藏jooq的logo
        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("org.jooq.no-tips", "true");
        StaticLog.info("执行钩子函数:{}/{}", "afterConfigParsed", config.encode());
    }

    @Override
    public void beforeStartingVertx(VertxOptions vertxOptions) {
        StaticLog.info("执行钩子函数:{}", "beforeStartingVertx");
        //创建Vertx实例时启用本机传输选项：
        vertxOptions.setPreferNativeTransport(true);
        // 加载配置文件
        final Config config = ConfigUtil.loadConfig();
        // 获取网络配置
        final NetworkConfig networkConfig = config.getNetworkConfig();
        // 获取Join配置
        final JoinConfig joinConfig = networkConfig.getJoin();
        // 启用TCP/IP发现机制
        final TcpIpConfig tcpIpConfig = joinConfig.getTcpIpConfig();
        tcpIpConfig.setEnabled(true);
        // 添加成员节点的IP地址和端口
        tcpIpConfig.setMembers(Arrays.asList(CLUSTER_IPS));
        final ClusterManager mgr = new HazelcastClusterManager(config);
        vertxOptions.setClusterManager(mgr);
        final EventBusOptions eventBusOptions = vertxOptions.getEventBusOptions();
        // 设置bus集群超时时间
        eventBusOptions.setConnectTimeout(1000 * 5);
        // 设置集群ping时间
        eventBusOptions.setClusterPingInterval(TimeUnit.SECONDS.toMillis(10));
        // 设置集群ping回复时间
        eventBusOptions.setClusterPingReplyInterval(TimeUnit.SECONDS.toMillis(10));
        vertxOptions.setEventBusOptions(eventBusOptions);
        // 配置打包线上配置 会启用配置文件conf/config-prod.yaml
        if (StrUtil.isBlank(VertxLoadConfigKt.getActive())) {
            VertxLoadConfigKt.setActive(EnvEnum.PROD.getEnv());
        }
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
        // 从args中找出是否有-active=prod参数,有的话就设置为生产环境
        for (String arg : args) {
            if (arg.startsWith("-active=")) {
                final String active = arg.substring(8);
                VertxLoadConfigKt.setActive(active);
                break;
            }
        }
        super.dispatch(args);
    }
}
