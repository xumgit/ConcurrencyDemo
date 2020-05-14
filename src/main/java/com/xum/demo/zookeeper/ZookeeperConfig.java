package com.xum.demo.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;

/*@Configuration*/
public class ZookeeperConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperConfig.class);

    @Value("${zookeeper.address}")
    private String address;

    @Value("${zookeeper.timeout}")
    private  int timeout;

    @Bean(name = "zkClient")
    public ZooKeeper zkClient(){
        LOG.info("address:" + address + ",timeout:" + timeout);
        ZooKeeper zooKeeper=null;
        try {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            //连接成功后，会回调watcher监听，此连接操作是异步的，执行完new语句后，直接调用后续代码
            //  可指定多台服务地址 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
            zooKeeper = new ZooKeeper(address, timeout, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if(Event.KeeperState.SyncConnected == event.getState()){
                        //如果收到了服务端的响应事件,连接成功
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
            LOG.info("Zookeeper connect status:" + zooKeeper.getState());

        }catch (Exception e){
            LOG.error("Zookeeper connect exception" + e.getMessage());
        }
        return  zooKeeper;
    }

}
