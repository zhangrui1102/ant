package com.etc.ant.common.core.client.rocketmq;

/**
 * @author
 * @version 1.0
 * @create 2018-10-04 22:12
 * @since JDK1.8
 */
public class RocketMQSimpleCanalClient extends AbstractRocketMQCanalClient {

    public RocketMQSimpleCanalClient(String nameServers,String topic,String groupName){
        super(nameServers,topic,groupName);
        logger.info("## Start the rocketmq consumer: {}-{}", topic, groupName);
        this.start();
        logger.info("## The canal rocketmq consumer is running now ......");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logger.info("## Stop the rocketmq consumer");
                this.stop();
            } catch (Throwable e) {
                logger.warn("## Something goes wrong when stopping rocketmq consumer:", e);
            } finally {
                logger.info("## Rocketmq consumer is down.");
            }
        }));
    }

}
