package com.xum.demo.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatcherApi implements Watcher {

    private static final Logger LOG = LoggerFactory.getLogger(WatcherApi.class);

    @Override
    public void process(WatchedEvent watchedEvent) {
        LOG.info("watch event status ={ }", watchedEvent.getState());
        LOG.info("watch event path = {}", watchedEvent.getPath());
        LOG.info("watch event type = {}", watchedEvent.getType()); //  三种监听类型： 创建，删除，更新
    }
}
