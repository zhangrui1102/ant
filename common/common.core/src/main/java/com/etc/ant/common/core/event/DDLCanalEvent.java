package com.etc.ant.common.core.event;

import com.alibaba.otter.canal.protocol.CanalEntry;

/**
 * @author
 * @version 1.0
 * @create 2018-11-09 21:45
 * @since JDK1.8
 **/
public class DDLCanalEvent extends CanalEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public DDLCanalEvent(CanalEntry.Entry source) {
        super(source);
    }
}
