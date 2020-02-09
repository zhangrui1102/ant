package com.etc.ant.common.core.client.rocketmq;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.etc.ant.common.core.client.AbstractCanalClient;
import com.etc.ant.common.core.client.rocketmq.connector.RocketMQCanalConnector;
import com.etc.ant.common.core.client.rocketmq.connector.RocketMQCanalConnectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 通过rocketmq连接模式
 * @author
 * @version 1.0
 * @create 2018-10-04 22:12
 * @since JDK1.8
 */
public abstract class AbstractRocketMQCanalClient extends AbstractCanalClient {

    protected final static Logger logger  = LoggerFactory.getLogger(AbstractRocketMQCanalClient.class);

    private RocketMQCanalConnector connector;

    private static volatile boolean         running = false;

    private Thread                          thread  = null;

    private Thread.UncaughtExceptionHandler handler = (t, e) -> logger.error("parse events has an error", e);

    public AbstractRocketMQCanalClient(String nameServers, String topic, String groupName){
        connector = RocketMQCanalConnectors.newRocketMQConnector(nameServers, topic, groupName);
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
                e.printStackTrace();
            }
        }
    }

    private void process() {
        while (!running){}
        while (running) {
            try {
                connector.connect();
                connector.subscribe();
                while (running) {
                    List<Message> messages = connector.getListWithoutAck(100L, TimeUnit.MILLISECONDS);
                    if (CollectionUtils.isEmpty(messages)) {
                        continue;
                    }
                    for (Message message : messages) {
                        long batchId = message.getId();
                        List<CanalEntry.Entry> entries = message.getEntries();
                        if (batchId != -1 && entries.size() > 0) {
                            entries.forEach(entry -> {
                                if (CanalEntry.EntryType.ROWDATA.equals(entry.getEntryType())) {
                                    publishCanalEvent(entry);
                                }
                            });
                        }
                    }
                    connector.ack(); // 提交确认
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        connector.unsubscribe();
    }
}
