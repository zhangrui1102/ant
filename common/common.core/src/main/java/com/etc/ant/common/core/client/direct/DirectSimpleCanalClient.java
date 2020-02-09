package com.etc.ant.common.core.client.direct;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;

import java.net.InetSocketAddress;

/**
 * @author
 * @version 1.0
 * @create 2018-10-04 22:12
 * @since JDK1.8
 **/
public class DirectSimpleCanalClient extends AbstractDirectCanalClient{

    public DirectSimpleCanalClient(String ip, Integer port, String username, String password, String destination, Integer batchSize){
        super(destination,batchSize);
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(ip, port),
                destination,username,password);
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

    public DirectSimpleCanalClient(String ip, Integer port, String destination){
        this(ip, port,null,null, destination,null);
    }

    public DirectSimpleCanalClient(String ip, Integer port, String destination, Integer batchSize){
        this(ip, port,null,null, destination,batchSize);
    }
}
