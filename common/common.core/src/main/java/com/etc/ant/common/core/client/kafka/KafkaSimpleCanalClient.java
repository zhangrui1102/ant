package com.etc.ant.common.core.client.kafka;

/**
 * @author
 * @version 1.0
 * @create 2018-10-04 22:12
 * @since JDK1.8
 **/
public class KafkaSimpleCanalClient extends AbstractKafkaCanalClient {

    public KafkaSimpleCanalClient(String servers, String topic, Integer partition, String groupId){
        super(servers,topic,partition,groupId);
        logger.info("## start the kafka consumer: {}-{}",topic, groupId);
        this.start();
        logger.info("## the canal kafka consumer is running now ......");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logger.info("## stop the kafka consumer");
                this.stop();
            } catch (Throwable e) {
                logger.warn("##something goes wrong when stopping kafka consumer:", e);
            } finally {
                logger.info("## kafka consumer is down.");
            }
        }));
    }
}
