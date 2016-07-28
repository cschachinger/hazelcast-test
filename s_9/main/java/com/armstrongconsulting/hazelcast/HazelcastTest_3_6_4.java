package com.armstrongconsulting.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

public class HazelcastTest_3_6_4 {
	
	public static void main(String[] args) {
		Config cfg = new Config();
		NetworkConfig network = cfg.getNetworkConfig();

		JoinConfig join = network.getJoin();

		String groupName = "hazelcast-performance-test";

		network.setPort(60000);
		network.setPortAutoIncrement(true);
		join.getMulticastConfig().setEnabled(false);
		join.getTcpIpConfig().addMember("127.0.0.1").setEnabled(true);
		network.getInterfaces().setEnabled(true).addInterface("127.*.*.*");

		cfg.getGroupConfig().setName(groupName);
		cfg.getGroupConfig().setPassword(groupName);


		MapConfig mapCfg = new MapConfig();
		mapCfg.setName("default");
		mapCfg.setBackupCount(0);
		mapCfg.setTimeToLiveSeconds(30000);
		mapCfg.setMaxIdleSeconds(15000);
		mapCfg.setEvictionPolicy(EvictionPolicy.LFU);

		NearCacheConfig ncc = new NearCacheConfig();
		ncc.setEvictionPolicy("LFU");
		ncc.setTimeToLiveSeconds(7200);
		ncc.setMaxIdleSeconds(3600);
		ncc.setMaxSize(10000);
		ncc.setInvalidateOnChange(true);
		mapCfg.setNearCacheConfig(ncc);

		cfg.addMapConfig(mapCfg);


		HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance(cfg);
		
		IMap<String,String> map = hazelcast.getMap("testmap");
		map.put("test-key", "abc");
		
		long started = System.currentTimeMillis();

		for (int i=0; i<=10000; i++){
			map.get("test-key");
		}
		
		System.out.println("Duration: " + (System.currentTimeMillis() - started) + " ms");
		
		hazelcast.getLifecycleService().shutdown();
		
		
	}

}
