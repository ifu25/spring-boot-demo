package cn.lttc.redisdemo;

import cn.lttc.redisdemo.model.Product;
import cn.lttc.redisdemo.util.RedisUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
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
    Jedis jedis = new Jedis("10.201.6.7", 7001);

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //region ===========================jedis测试===========================

    /**
     * 使用Jedis测试redis string类型的数据
     *
     * @param
     * @return void
     */
    @Test
    public void testJedisStringSetKey() {
        //密码认证
        jedis.auth("lttc");
        //选择redis库
        jedis.select(0);
        //设置普通String类型key/value
        jedis.set("test1", "value1");
        //设置普通String类型key/value并添加过期时间
        jedis.setex("test2", 20, "value2");
        //设置普通String类型key/value，若含有此key，则不添加；若不含有此key则添加对应的key/value
        jedis.setnx("test1", "overrider value1");
        jedis.setnx("test3", "value3");
        System.out.println("test1=" + jedis.get("test1"));
        System.out.println("test2=" + jedis.get("test2"));
    }

    /**
     * 同时设置多个普通String类型key/value测试
     *
     * @param
     * @return void
     */
    @Test
    public void testJedisMutiKey() {
        jedis.auth("lttc");
        //jedis incrBy方法测试
        jedis.mset("username", "张三", "password", "123456");
        List<String> mget = jedis.mget("username", "password");
        System.out.println("mget:" + mget.toString());
    }

    /**
     * jedis incrBy方法测试
     *
     * @param
     * @return
     */
    @Test
    public void testJedisStringIncr() {
        jedis.auth("lttc");
        jedis.set("test_count", "50");
        jedis.incrBy("test_count", 20);
        System.out.println(jedis.get("test_count"));
    }

    /**
     * jedis hset方法测试 使用key/field/value的方式赋值
     *
     * @param
     * @return void
     */
    @Test
    public void testHSet() {
        jedis.auth("lttc");
        jedis.hset("hsettest1", "username", "zhangsan");
        System.out.println("hsettest1:" + jedis.hget("hsettest1", "username"));
    }

    /**
     * jedis hset方法测试 使用key/map的方式赋值
     *
     * @param
     * @return void
     */
    @Test
    public void testMap() {
        jedis.auth("lttc");
        Map<String, String> map = new HashMap<String, String>();
        map.put("test2", "value2");
        jedis.hset("hsettest2", map);
    }

    /**
     * 测试集群
     *
     * @param
     * @return void
     */
    @Test
    public void testCluster() {

        //初始化集群HostAndPord集合
        Set<HostAndPort> set = new HashSet<>();
        set.add(new HostAndPort("10.201.6.7", 7001));
        set.add(new HostAndPort("10.201.6.7", 7002));
        set.add(new HostAndPort("10.201.6.7", 7003));
        set.add(new HostAndPort("10.201.6.7", 7004));
        set.add(new HostAndPort("10.201.6.7", 7005));
        set.add(new HostAndPort("10.201.6.7", 7006));
        //初始化JedisPoolConfig文件，用于集群jedis初始化
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(5);
        jedisPoolConfig.setMinIdle(3);
        //初始化集群jedis即JedisCluster
        JedisCluster jedisCluster = new JedisCluster(set, 15000, 15000, 15000, "123456", jedisPoolConfig);
        //使用JedisCluster取值赋值，根据hash算法，使用不同的redis节点存/取值
        jedisCluster.set("testkey01", "testvalue");
        jedisCluster.get("testkey");
        jedisCluster.set("testkey02", "testvalue");
        jedisCluster.get("testkey");

    }

    //endregion

    //region ===========================redisTemplate测试===========================

    /**
     * RedisTemplate普通String方法测试
     *
     * @param
     * @return void
     */
    @Test
    public void testRedisTemplateString() {
        redisTemplate.opsForValue().set("redisTemplate:key", "redisTemplate:value");
        String retValue = (String) redisTemplate.opsForValue().get("redisTemplate:key");
        System.out.println(retValue);
    }

    /**
     * RedisTemplate普通String set方法设置过期时间
     *
     * @param
     * @return void
     */
    @Test
    public void testRedisTemplateStringEx() {
        redisTemplate.opsForValue().set("redisTemplate:key1", "redisTemplate:value1", 5, TimeUnit.SECONDS);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String retValue1 = (String) redisTemplate.opsForValue().get("redisTemplate:key1");
        System.out.println(retValue1);
    }

    /**
     * RedisTemplate普通String set方法，对应redis setnx命令，若不存在则赋值，若存在则无操作
     *
     * @param
     * @return void
     */
    @Test
    public void testRedisTemplateStringNx() {
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("redisTemplate:key", "redisTemplate:value2");
        System.out.println(aBoolean);
    }

    /**
     * RedisTemplate hash put方法，放置String类型的数据测试
     *
     * @param
     * @return void
     */
    @Test
    public void testRedisTemplateHashString() {
        redisTemplate.opsForHash().put("redisTemplate:hash", "redisTemplate:hashkey", "redisTemplate:hashvalue");
        String retValue1 = (String) redisTemplate.opsForHash().get("redisTemplate:hash", "redisTemplate:hashkey");
        System.out.println(retValue1);
    }

    /**
     * RedisTemplate hash put方法，放置java对象类型的数据测试
     *
     * @param
     * @return void
     */
    @Test
    public void testRedisTemplateHashJavaObj() {
        Product product = new Product();
        product.setId(6);
        product.setName("test");
        product.setPrice(123.00);
        redisTemplate.opsForHash().put("redistemplate:hash", "redisTemplate:hashObjkey", product);
        //错误示范
        //Product productRet = (Product)redisTemplate.opsForHash().get("redistemplate:hash", "redisTemplate:hashObjkey");
        //正确示范
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Product>(Product.class));
        Product productRet = (Product) redisTemplate.opsForHash().get("redistemplate:hash", "redisTemplate:hashObjkey");
        System.out.println(productRet);
    }

    /**
     * RedisTemplate hash incr方法测试
     *
     * @param
     * @return void
     */
    @Test
    public void testRedisTemplateHashIncr() {
        redisTemplate.opsForHash().put("redistemplate:hash", "redisTemplate:hashIntKey", 1);
        redisTemplate.opsForHash().increment("redistemplate:hash", "redisTemplate:hashIntKey", 5);
        Integer retValue2 = (Integer) redisTemplate.opsForHash().get("redistemplate:hash", "redisTemplate:hashIntKey");
        System.out.println(retValue2);
    }

    /**
     * RedisTemplate list lpush、lpushall、range方法测试
     *
     * @param
     * @return void
     */
    @Test
    public void testRedisTemplateList() {
        redisTemplate.opsForList().leftPush("redistemplate:list", "redistemplate:value1");
        List<String> testStringLst = new ArrayList<>();
        testStringLst.add("redistemplate:value2");
        testStringLst.add("redistemplate:value3");
        redisTemplate.opsForList().leftPushAll("redistemplate:list", "redistemplate:value4", "redistemplate:value5");
        List<Object> range = redisTemplate.opsForList().range("redistemplate:list", 0, -1);
        range.forEach((value) -> System.out.println(value));
    }

    /**
     * RedisTemplate list set方法测试
     *
     * @param
     * @return void
     */
    @Test
    public void testRedisTemplateListSet() {
        redisTemplate.opsForList().set("redistemplate:list", 0, "redistemplate:value6");
        List<Object> range = redisTemplate.opsForList().range("redistemplate:list", 0, 0);
        range.forEach((value) -> System.out.println(value));
    }

    /**
     * RedisTemplate list pop方法测试
     *
     * @param
     * @return void
     */
    @Test
    public void testRedisTemplateListPop() {
        Object leftPopValue = redisTemplate.opsForList().leftPop("redistemplate:list");
        Object rightPopValue = redisTemplate.opsForList().rightPop("redistemplate:list");
        System.out.println(leftPopValue);
        System.out.println(rightPopValue);
    }

    /**
     * RedisTemplate set add、isMember、members方法测试
     *
     * @param
     * @return void
     */
    @Test
    public void testRedisTemplateSet() {
        redisTemplate.opsForSet().add("redistemplate:set", "redistemplate:setValue1");
        redisTemplate.opsForSet().add("redistemplate:set", "redistemplate:setValue2");
        redisTemplate.opsForSet().add("redistemplate:set", "redistemplate:setValue3", "redistemplate:setValue4");
        Boolean isMember = redisTemplate.opsForSet().isMember("redistemplate:set", "redistemplate:setValue1");
        System.out.println("isMember:" + isMember);
        Set<Object> members = redisTemplate.opsForSet().members("redistemplate:set");
        members.forEach((member) -> System.out.println(member));
    }

    /**
     * RedisTemplate set randomMember、pop方法测试
     *
     * @param
     * @return void
     */
    @Test
    public void testRedisTemplateSetPop() {
        Object randomMember = redisTemplate.opsForSet().randomMember("redistemplate:set");
        System.out.println("randomMember:" + randomMember);
        Object popValue = redisTemplate.opsForSet().pop("redistemplate:set");
        System.out.println("popvalue:" + popValue);
    }

    /**
     * redisutil 存取java对象第一种方式
     *
     * @param
     * @return void
     */
    @Test
    public void testRedisUtil() {

        Product product = new Product();
        product.setId(5);
        product.setName("abc");
        product.setPrice(123.00);

        redisTemplate.opsForHash().put("test", "2", product);
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Product>(Product.class));
        Product productRet = (Product) redisTemplate.opsForHash().get("test", "2");

        System.out.println(productRet);
    }

    /**
     * redisutil 存取java对象第二种方式
     *
     * @param
     * @return void
     */
    @Test
    public void testRedisUtil2() {

        Product product = new Product();
        product.setId(5);
        product.setName("abc");
        product.setPrice(123.00);

        redisTemplate.opsForHash().put("test", "2", product);
        redisUtil.hset("test", "1", product);
        Object productRet = redisUtil.hget("test", "1");
        Product productRetNew = JSON.parseObject(JSON.toJSONString(productRet), new TypeReference<Product>() {
        });

        System.out.println(productRet);
    }

    //endregion

}
