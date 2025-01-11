package com.example;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;

public class HazelcastConfiguration {
    public static Config createConfig() {
        Config config = new Config();
        JoinConfig joinConfig = config.getNetworkConfig().getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(true).addMember("192.168.0.101").addMember("192.168.0.102");
        return config;
    }
}