package com.etc.ant.common.core.listener;

import com.etc.ant.common.core.event.CanalEvent;
import org.springframework.context.ApplicationListener;


/**
 * @author
 * @version 1.0
 * @create 2018-10-04 22:12
 * @since JDK1.8
 **/
public interface CanalListener extends ApplicationListener<CanalEvent> {

    @Override
    void onApplicationEvent(CanalEvent event);
}
