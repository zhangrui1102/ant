package com.etc.ant.common.core.client.kafka;

import com.alibaba.otter.canal.client.kafka.KafkaCanalConnector;
import com.alibaba.otter.canal.client.kafka.KafkaCanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.etc.ant.common.core.client.AbstractCanalClient;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 通过kafka连接模式
 * @author
 * @version 1.0
 * @create 2018-10-04 22:12
 * @since JDK1.8
 **/
public abstract class AbstractKafkaCanalClient extends AbstractCanalClient {

    protected final static Logger logger  = LoggerFactory.getLogger(AbstractKafkaCanalClient.class);

    private KafkaCanalConnector connector;

    private static volatile boolean         running = false;

    private Thread                          thread  = null;

    private Integer batchSize = 1024;

    private Thread.UncaughtExceptionHandler handler = (t, e) -> logger.error("parse events has an error", e);

    public AbstractKafkaCanalClient(String servers, String topic, Integer partition, String groupId){
        connector = KafkaCanalConnectors.newKafkaConnector(servers, topic, partition, groupId);
    }
    public AbstractKafkaCanalClient(String zkServers, String servers, String topic, Integer partition, String groupId){
        connector = KafkaCanalConnectors.newKafkaConnector(servers, topic, partition, groupId);
    }

    @Override
    public void start() {
        Assert.notNull(connector, "connector is null");
        thread = new Thread(() -> process());
        thread.setUncaughtExceptionHandler(handler);
        thread.start();
        running = true;
    }

    @Override
    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private void process() {
        while (!running) {
        }
        while (running) {
            try {
                connector.connect();
                connector.subscribe();
                while (running) {
                    try {
                        // 获取message
                        Message message = connector.getWithoutAck(batchSize,1L, TimeUnit.SECONDS);
                        if (message == null) {
                            continue;
                        }
                        long batchId = message.getId();
                        try {
                            List<CanalEntry.Entry> entries = message.getEntries();
                            if (batchId != -1 && entries.size() > 0) {
                                entries.forEach(entry -> {
                                    if (CanalEntry.EntryType.ROWDATA.equals(entry.getEntryType())) {
                                        publishCanalEvent(entry);
                                    }
                                });
                            }
                            connector.ack();
                        } catch (Exception e) {
                            logger.error("发送监听事件失败！batchId回滚,batchId=" + batchId, e);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        try {
            connector.unsubscribe();
        } catch (WakeupException e) {
            // No-op. Continue process
        }
        connector.disconnect();
    }
}
