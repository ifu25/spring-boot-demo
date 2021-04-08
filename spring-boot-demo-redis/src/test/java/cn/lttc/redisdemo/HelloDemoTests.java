package cn.lttc.redisdemo;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;

@RunWith(SpringRunner.class)
class HelloDemoTests {
    Jedis jedis = new Jedis("192.168.190.139",7001);

    @Test
    public void testStringSetKey() {
        jedis.auth("123456");
        //jedis.select(0);
//        jedis.set("test1","value1");
//        jedis.setex("test2",20,"value2" );
//        jedis.setnx("test1","overrider value1");
//        jedis.setnx("test3","value3");
//        System.out.println("test1="+jedis.get("test1"));
//        System.out.println("test2="+jedis.get("test2"));
//
//        jedis.mset("username","张三","password","123456");
//        List<String> mget = jedis.mget("username", "password");
//        System.out.println("mget:"+mget.toString());

        jedis.set("test_count","50");
        jedis.incrBy("test_count",20);
        System.out.println(jedis.get("test_count"));
    }

    @Test
    public void testHSet(){
        jedis.auth("123456");
        jedis.hset("hsettest1","username","zhangsan");
        System.out.println("hsettest1:"+jedis.hget("hsettest1","username"));
    }
    @Test
    public void testMap(){
        jedis.auth("123456");
        Map<String,String> map = new HashMap<String,String>();
        map.put("test2","value2");
        jedis.hset("hsettest2",map);
    }
    @Test
    public void testCluster(){
        Set<HostAndPort> set = new HashSet<>();
        set.add(new HostAndPort("192.168.190.139",7001));
        set.add(new HostAndPort("192.168.190.139",7002));
        set.add(new HostAndPort("192.168.190.139",7003));
        set.add(new HostAndPort("192.168.190.139",7004));
        set.add(new HostAndPort("192.168.190.139",7005));
        set.add(new HostAndPort("192.168.190.139",7006));
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(5);
        jedisPoolConfig.setMinIdle(3);
        JedisCluster jedisCluster = new JedisCluster(set,15000,15000,15000,"123456",jedisPoolConfig);

        jedisCluster.set("testkey01","testvalue");
        jedisCluster.get("testkey");
        jedisCluster.set("testkey02","testvalue");
        jedisCluster.get("testkey");
    }


}
