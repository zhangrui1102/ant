package com.etc.ant.common.core.client;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.etc.ant.common.core.event.DDLCanalEvent;
import com.etc.ant.common.core.event.DeleteCanalEvent;
import com.etc.ant.common.core.event.InsertCanalEvent;
import com.etc.ant.common.core.event.UpdateCanalEvent;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author
 * @version 1.0
 * @create 2018-10-04 22:12
 * @since JDK1.8
 */
public abstract class AbstractCanalClient implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    protected abstract void start();

    protected abstract void stop();

    /**
     *
     * @param entry
     */
    protected void publishCanalEvent(CanalEntry.Entry entry) {
        CanalEntry.EventType eventType = entry.getHeader().getEventType();
        try {
            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            if(rowChange.getIsDdl()){
                //DDL语句
                applicationContext.publishEvent(new DDLCanalEvent(entry));
                return;
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        //DML处理
        switch (eventType) {
            case INSERT:
                applicationContext.publishEvent(new InsertCanalEvent(entry));
                break;
            case UPDATE:
                applicationContext.publishEvent(new UpdateCanalEvent(entry));
                break;
            case DELETE:
                applicationContext.publishEvent(new DeleteCanalEvent(entry));
                break;
            default:
                break;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
