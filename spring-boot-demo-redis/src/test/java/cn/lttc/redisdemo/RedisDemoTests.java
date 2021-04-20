package cn.lttc.redisdemo;

import cn.lttc.redisdemo.model.Product;
import cn.lttc.redisdemo.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis-jedis测试类
 *
 * @author sunjian
 * @create 2021-04-16
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisDemoTests {
    //jedis客户端连接
    Jedis jedis = new Jedis("10.201.6.7",7001);

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //region ===========================jedis测试===========================
    @Test
    public void testJedisStringSetKey() {
        //密码认证
        jedis.auth("lttc");
        //选择redis库
        jedis.select(0);
        jedis.set("test1","value1");
        jedis.setex("test2",20,"value2" );
        jedis.setnx("test1","overrider value1");
        jedis.setnx("test3","value3");
        System.out.println("test1="+jedis.get("test1"));
        System.out.println("test2="+jedis.get("test2"));

        jedis.mset("username","张三","password","123456");
        List<String> mget = jedis.mget("username", "password");
        System.out.println("mget:"+mget.toString());

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
        set.add(new HostAndPort("10.201.6.7",7001));
        set.add(new HostAndPort("10.201.6.7",7002));
        set.add(new HostAndPort("10.201.6.7",7003));
        set.add(new HostAndPort("10.201.6.7",7004));
        set.add(new HostAndPort("10.201.6.7",7005));
        set.add(new HostAndPort("10.201.6.7",7006));
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
    //endregion

    //region ===========================redisTemplate测试===========================

    @Test
    public void testRedisTemplateString(){
        //region ===========================普通set方法===========================

        redisTemplate.opsForValue().set("redisTemplate:key","redisTemplate:value");
        String retValue = (String)redisTemplate.opsForValue().get("redisTemplate:key");
        System.out.println(retValue);

        //endregion

        //region ===========================set方法设置过期时间===========================

        redisTemplate.opsForValue().set("redisTemplate:key1","redisTemplate:value1",5, TimeUnit.SECONDS);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String retValue1 = (String)redisTemplate.opsForValue().get("redisTemplate:key1");
        System.out.println(retValue1);

        //endregion

        //region ===========================set方法，对应redis setnx命令，若不存在则赋值，若存在则无操作===========================

        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("redisTemplate:key", "redisTemplate:value2");
        System.out.println(aBoolean);

        //endregion
    }

    @Test
    public void testRedisTemplateHash(){
        //region ===========================hash put方法，放置String类型的数据===========================

//        redisTemplate.opsForHash().put("redisTemplate:hash","redisTemplate:hashkey","redisTemplate:hashvalue");
//        String retValue1 = (String)redisTemplate.opsForHash().get("redisTemplate:hash", "redisTemplate:hashkey");
//        System.out.println(retValue1);

        //endregion

        //region ===========================hash put方法，放置java对象类型的数据===========================

//        Product product = new Product();
//        product.setId(6);
//        product.setName("test");
//        product.setPrice(123.00);
//        redisTemplate.opsForHash().put("redistemplate:hash","redisTemplate:hashObjkey",product);
//        //错误示范
//        //Product productRet = (Product)redisTemplate.opsForHash().get("redistemplate:hash", "redisTemplate:hashObjkey");
//        //正确示范
//        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Product>(Product.class));
//        Product productRet = (Product)redisTemplate.opsForHash().get("redistemplate:hash", "redisTemplate:hashObjkey");
//        System.out.println(productRet);

        //endregion

        //region ===========================hash increment方法===========================

//        redisTemplate.opsForHash().put("redistemplate:hash","redisTemplate:hashIntKey",1);
//        redisTemplate.opsForHash().increment("redistemplate:hash","redisTemplate:hashIntKey",5);
//        Integer retValue2 = (Integer)redisTemplate.opsForHash().get("redistemplate:hash", "redisTemplate:hashIntKey");
//        System.out.println(retValue2);

        //endregion
    }

    @Test
    public void testRedisTemplateList() {

        //region ===========================list lpush、lpushall、range方法===========================

//        redisTemplate.opsForList().leftPush("redistemplate:list","redistemplate:value1");
//        List<String> testStringLst = new ArrayList<>();
//        testStringLst.add("redistemplate:value2");
//        testStringLst.add("redistemplate:value3");
//        redisTemplate.opsForList().leftPushAll("redistemplate:list", "redistemplate:value4","redistemplate:value5");
//        List<Object> range = redisTemplate.opsForList().range("redistemplate:list", 0, -1);
//        range.forEach((value)-> System.out.println(value));

        //endregion

        //region ===========================set方法===========================

//        redisTemplate.opsForList().set("redistemplate:list",0,"redistemplate:value6");
//        List<Object> range = redisTemplate.opsForList().range("redistemplate:list", 0, 0);
//        range.forEach((value)-> System.out.println(value));

        //endregion

        //region ===========================leftpop、rightpop方法===========================

//        Object leftPopValue = redisTemplate.opsForList().leftPop("redistemplate:list");
//        Object rightPopValue = redisTemplate.opsForList().rightPop("redistemplate:list");
//        System.out.println(leftPopValue);
//        System.out.println(rightPopValue);

        //endregion
    }

    @Test
    public void testRedisTemplateSet(){

        //region ===========================set add、isMember、members方法===========================

//        redisTemplate.opsForSet().add("redistemplate:set","redistemplate:setValue1");
//        redisTemplate.opsForSet().add("redistemplate:set","redistemplate:setValue2");
//        redisTemplate.opsForSet().add("redistemplate:set","redistemplate:setValue3","redistemplate:setValue4");
//        Boolean isMember = redisTemplate.opsForSet().isMember("redistemplate:set", "redistemplate:setValue1");
//        System.out.println("isMember:"+isMember);
//        Set<Object> members = redisTemplate.opsForSet().members("redistemplate:set");
//        members.forEach((member)-> System.out.println(member));
        //endregion

        //region ===========================randomMember、pop方法===========================

        Object randomMember = redisTemplate.opsForSet().randomMember("redistemplate:set");
        System.out.println("randomMember:"+randomMember);
        Object popValue = redisTemplate.opsForSet().pop("redistemplate:set");
        System.out.println("popvalue:"+popValue);

        //endregion
    }

    @Test
    public void testRedisUtil(){
        //region ===========================redisutil 存取java对象两种方式===========================

//        Product product = new Product();
//        product.setId(5);
//        product.setName("abc");
//        product.setPrice(123.00);
//
//        redisTemplate.opsForHash().put("test","2",product);;
//        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Product>(Product.class));
//        Product productRet = (Product)redisTemplate.opsForHash().get("test", "2");

//        redisUtil.hset("test","1",product);
//        Object productRet = redisUtil.hget("test", "1");
//        Product productRetNew = JSON.parseObject(JSON.toJSONString(productRet), new TypeReference<Product>() {
//        });
        //System.out.println(productRet);

        //endregion
    }

    //endregion

}
