package com.etc.ant.common.core.client.rocketmq.connector;

/**
 * 创建RocketMQCanalConnector对象工具类
 * RocketMQ connector provider.
 */
public class RocketMQCanalConnectors {

    /**
     *
     * @param nameServers
     * @param topic
     * @param groupId
     * @param flatMessage
     * @param retryTimes
     * @param vipChannelEnabled
     * @return
     */
    public static RocketMQCanalConnector newRocketMQConnector(String nameServers, String topic, String groupId,boolean flatMessage,
                                                              Integer retryTimes,boolean vipChannelEnabled) {
        return new RocketMQCanalConnector(nameServers, topic, groupId, flatMessage, retryTimes,vipChannelEnabled);
    }

    /**
     *
     * @param nameServers
     * @param topic
     * @param groupId
     * @return
     */
    public static RocketMQCanalConnector newRocketMQConnector(String nameServers, String topic, String groupId) {
        return new RocketMQCanalConnector(nameServers, topic, groupId, false, null,false);
    }
}
