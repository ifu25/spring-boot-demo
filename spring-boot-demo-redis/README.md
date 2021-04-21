# Spring-boot-demo-redis

## 一、Redis介绍

Redis是一个开源，内存存储的数据结构服务器，可用作数据库，高速缓存和消息队列代理。它支持字符串、哈希表、列表、集合、有序集合，位图，hyperloglogs等数据类型。内置复制、Lua脚本、LRU收回、事务以及不同级别磁盘持久化功能，同时通过Redis Sentinel提供高可用，通过Redis Cluster提供自动分区。

- Redis是以Key-Value形式进行存储的NoSQL数据库。

- Redis是使用C语言进行编写的。

- 平时操作的数据都在内存中，效率特高，读的效率110000/s，写81000/s，所以多把Redis当做缓存工具使用。

- Redis以solt（槽）作为数据存储单元，每个槽中可以存储N多个键值对。Redis中固定具有16384。理论上可以实现一个槽是一个Redis。每个向Redis存储数据的key都会进行crc16算法得出一个值后对16384取余就是这个key存放的solt位置。

## 二、jedis介绍

 Jedis是Redis官方推出的一款面向Java的客户端，提供了很多接口供Java语言调用。可以在Redis官网下载，当然还有一些开源爱好者提供的客户端，如Jredis、SRP等等，推荐使用Jedis。 

## 三、Spring Data Redis

Spring-data-redis是spring大家族的一部分，提供了在srping应用中通过简单的配置访问redis服务，对reids底层开发包(Jedis, JRedis, and RJC)进行了高度封装，RedisTemplate提供了redis各种操作、异常处理及序列化，支持发布订阅，并对spring 3.1 cache进行了实现。
spring-data-redis针对jedis提供了如下功能：

1. 连接池自动管理，提供了一个高度封装的“RedisTemplate”类。
2. 针对jedis客户端中大量api进行了归类封装,将同一类型操作封装为operation接口。 
  - ValueOperations：简单K-V操作
  - SetOperations：set类型数据操作
  - ZSetOperations：zset类型数据操作
  - HashOperations：针对map类型的数据操作
  - ListOperations：针对list类型的数据操作

3. 提供了对key的“bound”(绑定)便捷化操作API，可以通过bound封装指定的key，然后进行一系列的操作而无须“显式”的再次指定Key，即BoundKeyOperations将事务操作封装，由容器控制。 
  - BoundValueOperations
  - BoundSetOperations
  - BoundListOperations
  - BoundSetOperations
  - BoundHashOperations

4. 针对数据的“序列化/反序列化”，提供了多种可选择策略(RedisSerializer)

  - JdkSerializationRedisSerializer：POJO对象的存取场景，使用JDK本身序列化机制，将pojo类通过ObjectInputStream/ObjectOutputStream进行序列化操作，最终redis-server中将存储字节序列。是目前最常用的序列化策略。
  - StringRedisSerializer：Key或者value为字符串的场景，根据指定的charset对数据的字节序列编码成string，是“new String(bytes, charset)”和“string.getBytes(charset)”的直接封装。是最轻量级和高效的策略。
  - JacksonJsonRedisSerializer：jackson-json工具提供了javabean与json之间的转换能力，可以将pojo实例序列化成json格式存储在redis中，也可以将json格式的数据转换成pojo实例。因为jackson工具在序列化和反序列化时，需要明确指定Class类型，因此此策略封装起来稍微复杂。【需要jackson-mapper-asl工具支持】
  - OxmSerializer：提供了将javabean与xml之间的转换能力，目前可用的三方支持包括jaxb，apache-xmlbeans；redis存储的数据将是xml工具。不过使用此策略，编程将会有些难度，而且效率最低；不建议使用。【需要spring-oxm模块的支持】

## 四、集成步骤

### 1. 使用jedis操作Redis数据库

#### 1.1 build.gradle文件中添加依赖

```
dependencies {
    implementation 'redis.clients:jedis:3.5.2'
}
```

#### 1.2 初始化jedis客户端连接

```java
Jedis jedis = new Jedis("10.201.6.7", 6379);
```

#### 1.3 使用jedis进行数据操作

```java
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
```



### 2. 使用spring-boot-starter-data-redis操作Redis

#### 2.1 build.gradle文件中添加依赖

```
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
}
```

#### 2.2 修改配置文件application.yml

```yaml
spring:
  # 对应 RedisProperties 类
  redis:
    host: 10.201.6.7
    # 集群节点
    cluster:
      nodes: 10.201.6.7:7001,10.201.6.7:7002,10.201.6.7:7003,10.201.6.7:7004,10.201.6.7:7005,10.201.6.7:7006
    password: lttc
```

#### 2.3 添加RedisTemplate配置类

```java
@Configuration
public class MyRedisTemplate {
    /**
     * 初始化RedisTemplate
     *
     * @param redisConnectionFactory redis连接工厂
     * @return org.springframework.data.redis.core.RedisTemplate<java.lang.String, java.lang.Object> 返回RedisTemplate供其他类调用
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        //自定义实现对于RedisTemplate的配置
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        // key的序列化采用StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        // value值的序列化采用fastJsonRedisSerializer
        template.setHashValueSerializer(serializer);
        template.setValueSerializer(serializer);
        return template;
    }
    
    /**
     * StringRedisTemplate配置
     *
     * @param redisConnectionFactory redis连接工厂
     * @return org.springframework.data.redis.core.StringRedisTemplate StringRedisTemplate
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        stringRedisTemplate.setKeySerializer(RedisSerializer.string());
        stringRedisTemplate.setValueSerializer(RedisSerializer.string());
        stringRedisTemplate.setHashKeySerializer(RedisSerializer.string());
        stringRedisTemplate.setHashValueSerializer(RedisSerializer.string());
        return stringRedisTemplate;
    }
}
```

#### 2.4 使用StringRedisTemplate操作Redis

```java
public void testStringRedisTemplate() {
    stringRedisTemplate.opsForValue().set("redisTemplate:key", "redisTemplate:value");
    String retValue = stringRedisTemplate.opsForValue().get("redisTemplate:key");
    System.out.println(retValue);
}
```

#### 2.5使用RedisTemplate操作Redis

```java
public void testRedisTemplate() {
    Product product = new Product();
    product.setId(5);
    product.setName("abc");
    product.setPrice(123.00);
    redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Product>(Product.class));
    redisTemplate.opsForHash().put("test", "2", product);
    Product productRet = (Product) redisTemplate.opsForHash().get("test", "2");
    System.out.println(productRet);
}
```

## 五、参考文档

- [redis中文官网](https://www.redis.net.cn/)

- [RedisTemplate应用](https://blog.csdn.net/lydms/article/details/105224210)

  

