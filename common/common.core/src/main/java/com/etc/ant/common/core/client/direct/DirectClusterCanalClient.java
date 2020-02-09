package com.etc.ant.common.core.client.direct;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;

/**
 * @author
 * @version 1.0
 * @create 2018-10-04 22:12
 * @since JDK1.8
 **/
public class DirectClusterCanalClient extends AbstractDirectCanalClient{

    public DirectClusterCanalClient(String zkServers, String username, String password, String destination, Integer batchSize){
        super(destination, batchSize);
        CanalConnector connector = CanalConnectors.newClusterConnector(zkServers, destination, username, password);
        this.setConnector(connector);
        this.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logger.info("## stop the canal connector");
                this.stop();
            } catch (Throwable e) {
                logger.warn("##something goes wrong when stopping canal:", e);
            } finally {
                logger.info("## canal connector is down.");
            }
        }));
    }

    public DirectClusterCanalClient(String zkServers, String destination){
        this(zkServers,null,null, destination,null);
    }

    public DirectClusterCanalClient(String zkServers, String destination, Integer batchSize){
        this(zkServers,null,null, destination,batchSize);
    }

}
