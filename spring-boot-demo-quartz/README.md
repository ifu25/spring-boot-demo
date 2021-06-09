# spring-boot-demo-quartz

## Quartz介绍

> Quartz是OpenSymphony开源组织在Job scheduling领域又一个开源项目，它可以与J2EE与J2SE应用程序相结合也可以单独使用。
> 
> 在java企业级应用中，Quartz是使用最广泛的定时调度框架。

**在Quartz中的主要概念**
- Scheduler：任务调度API
- ScheduleBuilder：用于构建Scheduler，例如其简单实现类SimpleScheduleBuilder
- Job：调度任务执行的接口，也即定时任务执行的方法
- JobDetail：定时任务作业的实例
- JobBuilder：关联具体的Job，用于构建JobDetail
- Trigger：定义调度执行计划的组件，即定时执行
- TriggerBuilder：构建Trigger

## Quartz演示实例

```kotlin
internal class DemoJob : Job {

    /**
     * 重写方法z
     */
    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext) {
        var df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        println("当前时间是:" + df.format(Date()))
    }
}

@RequestMapping("/demo")
fun demo():String{
    //创建一个scheduler
    val scheduler = StdSchedulerFactory.getDefaultScheduler()
    //创建一个Trigger
    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1")
        .withSchedule(
            SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(3)
                .withRepeatCount(3)
        ).build()
    //创建一个job
    val job = JobBuilder.newJob(DemoJob::class.java)
        .withIdentity("DemoJob", "group1").build()
    //注册trigger并启动scheduler
    scheduler.scheduleJob(job, trigger)
    scheduler.start()
    return "请到控制台查看输出信息"
}

```

输出结果

```java
当前时间是:2021/05/08 12:51:49
当前时间是:2021/05/08 12:51:52
当前时间是:2021/05/08 12:51:55
当前时间是:2021/05/08 12:51:58
```

## 配置文件

> 配置文件参考网站：
>
> [中文网站](https://www.w3cschool.cn/quartz_doc/quartz_doc-i7oc2d9l.html)，不过感觉翻译都是机翻，存在好多不通顺和错误的地方
>
> [英文网站](https://github.com/quartz-scheduler/quartz/blob/d42fb7770f287afbf91f6629d90e7698761ad7d8/docs/configuration.adoc#simplethreadpool-specific-properties)，如果英文不错还是看英文网站

### 获取Scheduler实例

我们可以通过下面的代码获取一个Scheduler实例

```kotlin
val scheduler = StdSchedulerFactory.getDefaultScheduler()
```

上面是采用了一个默认的Quartz配置文件获取的Scheduler对象。

但是，经过测试，通过上述方法获取的的Scheduler对象是一个全局唯一的Scheduler对象，那我们改怎么获取多个Scheduler对象呢（后续演示实例如果用全局对象的话可能会产生相互干扰，影响演示的效果）。

我们查看`StdSchedulerFactory`类，发现其提供了一个`getScheduler`方法(kotlin中通过`scheduler`属性也是一样的，其来源于`getScheduler`方法)。

但是经测试，如果我们直接调用这个方法或属性的话，我们得不到一个Scheduler对象，得到的是null。

后经过查询资料可知，现在的Scheduler对象可以通过配置信息进行初始化，于是翻到了Quartz的默认配置文件（在其jar包里）quartz.properties。

```properties
# Default Properties file for use by StdSchedulerFactory

# to create a Quartz Scheduler Instance, if a different
# properties file is not explicitly specified.
org.quartz.scheduler.instanceName: DefaultQuartzScheduler
org.quartz.scheduler.rmi.export: false
org.quartz.scheduler.rmi.proxy: false
org.quartz.scheduler.wrapJobExecutionInUserTransaction: false
org.quartz.threadPool.class: org.quartz.simpl.SimpleThreadPool
org.quartz.threadPool.threadCount: 10
org.quartz.threadPool.threadPriority: 5
org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread: true
org.quartz.jobStore.misfireThreshold: 60000
org.quartz.jobStore.class: org.quartz.simpl.RAMJobStore
```

于是就照葫芦画瓢的，将这些配置内容拷贝一份，在代码里写到一个Properties里面，然后调用初始化方法。搞定！

代码如下：

```kotlin
object SchedulerHelper {
    /**
     * 获取一个scheduler
     */
    fun getScheduler(strName: String): Scheduler? {
        try {
            val sf = StdSchedulerFactory()
            val props = Properties()
            props["org.quartz.scheduler.instanceName"] = strName
            props["org.quartz.threadPool.threadCount"] = "10"
            sf.initialize(props)
            return sf.scheduler
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        return null
    }
}
```

> 需要注意的是：配置文件一般为quartz.properties文件，但是如果使用yml文件格式的配置，则quartz.properties里面的配置会失效

> 【程序内指定】在StdSchedulerFactory.getScheduler()之前使用StdSchedulerFactory.initialize(xx)

### 调度器属性配置

- org.quartz.scheduler.instanceName
  - 非必须
  - string类型，class类名
  - 默认值 QuartzScheduler
- 可以使任意字符串，同时属性值对调度器本身没有意义，而是如果同一程序中存在多个调度器实例，用来在客户端代码区分各个实例的。如果你正在使用集群，则集群中“逻辑上”是相同的调度程序的实例必须使用同样的名字。
  
- org.quartz.scheduler.instanceId
  - 非必须
  - string类型
  - 默认值  NON_CLUSTERED
- 可以使任意字符串，全局唯一。如果想自动生成，设置为AUTO。或者可以使用“SYS_PROP”通过系统属性“org.quartz.scheduler.instanceId”设置id
  
- org.quartz.scheduler.instanceIdGenerator.class
  - 非必须
  - string类型，class类名
  - 默认值 org.quartz.simpl .SimpleInstanceIdGenerator
- 仅当org.quartz.scheduler.instanceId设置为“AUTO” 时才有效，默认为“org.quartz.simpl.SimpleInstanceIdGenerator”，根据主机名和时间戳生成instance id。其他生成器：SystemPropertyInstanceIdGenerator （从系统属性org.quartz.scheduler.instanceId获取instance id），HostnameInstanceIdGenerator （根据当前主机名生成（InetAddress.getLocalHost().getHostName()）），你也可以实现InstanceIdGenerator 接口实现自己的生成类
  
- org.quartz.scheduler.threadName
  - 非必须                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
  - string类型
- 指定线程名称，不指定默认使用instanceName属性值加上后缀字符串_QuartzSchedulerThread
  
- org.quartz.scheduler.makeSchedulerThreadDaemon
  - 非必须
  - boolean类型
  - 默认值 false
- 指定调度程序的主线程是否为守护线程
  
- org.quartz.scheduler.threadsInheritContextClassLoaderOfInitializer
  - 非必须
  - boolean类型
  - 默认值 false
- 指定Quartz生成的线程是否继承初始化线程（初始化Quartz实例的线程）的上下文类加载器。这会影响Quartz的主调度线程、JDBCJobStore的”错时”处理线程、集群回复线程和简单线程池里的线程。 将该值设置为“true”可以帮助类加载，JNDI查找，和其他在应用程序服务器上使用Quartz等相关问题
  
- org.quartz.scheduler.idleWaitTime 
  - 非必须
  - long类型
  - 默认值 30000
- 在调度程序空闲的时候，重复查询可用触发器的等待的毫秒数。 5000ms以下是不推荐的，因为它会导致过的的数据库查询。1000ms以下是非法的
  
- org.quartz.scheduler.dbFailureRetryInterval
  - 非必须
  - long类型
  - 默认值 15000
- 连接超时重试连接需要等待的毫秒数。使用 RamJobStore时，该参数并没什么用
  
- org.quartz.scheduler.classLoadHelper.class
  - 非必须
  - string类型
  - 默认值 org.quartz.simpl.CascadingClassLoad
- 最可靠的方式就是使用默认值
  
- org.quartz.scheduler.jobFactory.class
  - 非必须
  - string类型
  - 默认值 org.quartz.simpl.SimpleJobFactory
- 指定JobFactory的类(接口)名称。负责实例化jobClass。【默认org.quartz.simpl.PropertySettingJobFactory】,只是在job被执行的时候简单调用newInstance()实例化一个job类。PropertySettingJobFactory 会使用反射机制通过SchedulerContext、 Job、Trigger和 JobDataMaps设置job bean的属性。在使用JTA事务时，可设置事务相关的属性
  
- org.quartz.context.key.SOME_KEY 
  - 非必须
  - string类型
  - 默认值 none
- 在scheduler context中的新建的键值对，例如：org.quartz.context.key.MyKey= MyValue，等价于scheduler.getContext().put(“MyKey”, “MyValue”)

> 除非使用JTA事务，否则事务相关属性应该不在配置文件中

- org.quartz.scheduler.userTransactionURL 
  - 非必须
  - string类型
  - 默认值 java:comp/UserTransaction
- 应该设置成JNDI URL，通过它Quartz可以找到应用服务的UserTransaction manger。默认值是java:comp/UserTransaction-大部分应用服务都是用这个配置。Websphere用户需要设置成“jta/usertransaction”。这个属性仅用于Quartz配置使用JobStoreCMT，同时wrapJobExecutionInUserTransaction设置为true
  
- org.quartz.scheduler.wrapJobExecutionInUserTransaction
  - 非必须
  - boolean类型
  - 默认值 false
- 如果想使用Quartz在执行一个job前使用UserTransaction，则应该设置该属性为true。job执行完、在JobDataMap改变之后事务会提交。默认值是false。 可以在你的job类中使用 @ExecuteInJTATransaction注解, 可以控制job是否使用事务。
  
- org.quartz.scheduler.skipUpdateCheck
  - 非必须
  - boolen类型
  - 默认值 false
- 是否跳过检查是否有可更新的Quartz版本可供下载。如果检查运行，并且找到更新，则会在Quartz的日志中报告它。您也可以使用系统属性“org.terracotta.quartz.skipUpdateCheck = true”（可以在系统环境中设置或在java命令行上设置为-D）来禁用更新检查。在生产环境建议设置为true
  
- org.quartz.scheduler.batchTriggerAcquisitionMaxCount
  - 非必须
  - int类型
  - 默认值 1
- 一个scheduler节点允许接收的trigger的最大数，默认值为1。数字越大，触发效率越高，但是以群集节点之间可能的不平衡负载为代价。如果值 > 1, 并且使用了 JDBC JobStore的话, org.quartz.jobStore.acquireTriggersWithinLock属性必须设置为true，以避免”弄脏”数据
  
- org.quartz.scheduler.batchTriggerAcquisitionFireAheadTimeWindow 
  - 非必须
  - long类型
  - 默认值 0
  - 允许触发器在其预定的火灾时间之前被获取和触发的时间（毫秒）的时间量，默认是0。这个值约大，trigger接受和执行的数量越大，但以触发计划不能准确的触发为代价。在调度程序具有大量的触发器，并且需要相近的时间执行时有用

### 线程池属性配置

- org.quartz.threadPool.class 
  - 必须
  - string类型，类名
  - 默认值 null
- 线程池的实现类。Quartz附带的线程池是“org.quartz.simpl.SimpleThreadPool”，几乎能够满足每个用户的需求，使用非常简单，并经过很好的测试。它提供了一个固定大小的线程池。
  
- org.quartz.threadPool.threadCount 
  - 必须
  - int类型
- 默认值 -1
  - 指定线程数，至少为1（无默认值）(一般设置为1-100直接的整数合适)
  
- org.quartz.threadPool.threadPriority 
  - 非必须
  - int 类型
  - Thread.NORM_PRIORITY (5)
  - 设置线程的优先级（可以是Thread.MIN_PRIORITY（即1）和Thread.MAX_PRIORITY（这是10）之间的任何整数）

#### SimpleThreadPool属性配置

- org.quartz.threadPool.makeThreadsDaemons
  - 非必须
  - boolean类型
  - 默认值false
  - 确定线程池的线程是否创建为守护线程

- org.quartz.threadPool.threadsInheritGroupOfInitializingThread
  - 非必须
  - boolean类型
  - 默认值true
  - 线程是否继承初始化线程的组

- org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread
  - 非必须
  - boolean类型
  - 默认值false
  - 线程是否继承初始化线程的ContextClassLoader

- org.quartz.threadPool.threadNamePrefix
  - 非必须
  - string类型
  - 默认值[Scheduler Name]_Worker
  - 用于指定线程池中线程的名称前缀

#### 自定义线程池

> 后续待研究

### JobStore配置

#### RAMJobStore

RAMJobStore用于存储内存中的调度信息（jobs，Triggers和日历）。RAMJobStore快速轻便，但是当进程终止时，所有调度信息都会丢失。

通过设置“org.quartz.jobStore.class”属性来选择RAMJobStore。

```properties
org.quartz.jobStore.class = org.quartz.simpl.RAMJobStore
```

- org.quartz.jobStore.misfireThreshold
  - 非必须
  - int类型
  - 默认值 60000
  - 用来指定调度引擎设置触发器超时的"临界值"，当触发器错过执行后，只要错过时间小于这个值，调度器不认为这个触发器超时，还能执行，但是超过这个时间后，调度器认为触发器超时

#### JDBC-JobStoreTX

JDBCJobStore用于在关系数据库中存储调度信息（jobs，Triggers和日历）。

通过设置“org.quartz.jobStore.class”属性来选择JobStoreTX：

```properties
org.quartz.jobStore.class = org.quartz.impl.jdbcjobstore.JobStoreTX
```

- org.quartz.jobStore.dataSource
  - 必须
  - string
  - 默认值null
  - 此属性的值必须是配置文件中配置的数据源的的名称，用于指定quartz的数据源

- org.quartz.jobStore.tablePrefix 
  - 非必须
  - string类型
  - 默认值QRTZ_
  - 数据库中Quaryz相关数据表的前缀。如果使用不同的表前缀，则可以在同一数据库中拥有多组Quartz表

- org.quartz.jobStore.useProperties
  - 非必须
  - boolean类型
  - 默认值false
  - 指示JobDataMap中的数据是否将以键值对的方式存储，而不是序列化成blob对象存储。

- org.quartz.jobStore.misfireThreshold
  - 非必须
  - int类型
  - 默认值60000
  - 用来指定调度引擎设置触发器超时的"临界值"，当触发器错过执行后，只要错过时间小于这个值，调度器不认为这个触发器超时，还能执行，但是超过这个时间后，调度器认为触发器超时

- org.quartz.jobStore.isClustered
  - 非必须
  - boolean类型
  - 默认值false
  - 是否开启集群功能。如果多个Quartz实例使用同一组数据库表，则必须设置为true，否则可能会破坏数据

- org.quartz.jobStore.clusterCheckinInterval
  - 非必须
  - long类型
  - 默认值15000
  - Scheduler 实例检入到数据库中的频率(单位：毫秒)。Scheduler 检查其他的实例到了它们应当检入的时候是否未检入；这能指出一个失败的 Scheduler 实例，且当前 Scheduler 会以此来接管任何执行失败并可恢复的 Job。通过检入操作，Scheduler 也会更新自身的状态记录。clusterChedkinInterval 越小，Scheduler 节点检查失败的 Scheduler 实例就越频繁。

- org.quartz.jobStore.maxMisfiresToHandleAtATime 
  - 非必须
  - int类型
  - 默认值20
  - 用于设定同一时间能够处理的misfire的最大个数

- org.quartz.jobStore.dontSetAutoCommitFalse
  - 非必须
  - boolean类型
  - 默认值false
  - 设置这个参数为 true 会告诉 Quartz获取数据库连接后不要调用它的 setAutoCommit(false) 方法
    - 数据库连接connection默认是自动提交到数据库，这样，执行完update ,delete或者insert的时候都会自动提交到数据库，无法回滚事务
    - 设置connection.setautocommit(false)后只有程序调用`connection.commit()`的时候才会将先前执行的语句一起提交到数据库，这样就实现了数据库的事务

- org.quartz.jobStore.selectWithLockSQL
  - 非必须
  - string类型
  - 默认值SELECT * FROM {0}LOCKS WHERE SCHED_NAME = {1} AND LOCK_NAME = ? FOR UPDATE
  - 这必须是一个从 LOCKS 表查询一行并对这行记录加锁的 SQL 语句。假如未设置，默认值就是 SELECT * FROM {0}LOCKS WHERE LOCK_NAME = ? FOR UPDATE，这能在大部分数据库上工作。{0} 会在运行期间被前面你配置的 TABLE_PREFIX 所替换

- org.quartz.jobStore.txIsolationLevelSerializable
  - 非必须
  - boolean类型
  - 默认值false
  - 值为 true 时告知 Quartz(当使用 JobStoreTX 或 CMT) 调用 JDBC 连接的 setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE) 方法。这有助于阻止某些数据库在高负载和长时间事务时锁的超时

- org.quartz.jobStore.acquireTriggersWithinLock
  - 非必须
  - boolean类型
  - 是否在数据库锁内获取下一个要触发的trigger。在之前的Quartz版本中，为避免死锁必须用它，但是现在不再是必要了，默认值为false。如果“org.quartz.scheduler.batchTriggerAcquisitionMaxCount”的值大于1，且使用了JDBC JobStore，那么这个属性必须设置true，以避免数据损坏

- org.quartz.jobStore.lockHandler.class
  - 非必须
  - string类型
  - 默认值null
  - 用这个类名生成一个org.quartz.impl.jdbcjobstore.Semaphore实例，用于锁定控制JobStore的数据。这是一个高级配置特性，大多数用户不应该使用它。默认情况下，Quartz会选择最合适的Semaphore实现。MS SQL Server用户可能会“org.quartz.impl.jdbcjobstore.UpdateLockRowSemaphore”感兴趣

- org.quartz.jobStore.driverDelegateInitString 
  - 非必须
  - string类型
  - 默认值null
  - 由“|”分割的属性（和它们的值），在初始化时，传递给DriverDelegate。字符串的格式类似于：“settingName=settingValue|otherSettingName=otherSettingValue|...“。StdJDBCDelegate和它所有的子类(Quartz自带的所有代理类)支持一个属性叫“triggerPersistenceDelegateClasses”，可以被设置为逗号（,）分割的类，它们实现了TriggerPersistenceDelegate 接口，用于存储自定义的trigger类型。

#### JDBC-JobStoreCMT

> 后续研究……

### Quartz配置DataSources

> 如果您使用JDBC-Jobstore，则需要使用DataSource

- org.quartz.dataSource.NAME.driver
  - 必须
  - string类型
  - 默认值null
  - 必须是数据库的JDBC驱动程序的java类名称

- org.quartz.dataSource.NAME.URL
  - 必须
  - string类型
  - 默认值null
  - 连接数据库的url（主机、端口等信息）

- org.quartz.dataSource.NAME.user
  - 非必须
  - string类型
  - 默认值“”
  - 连接数据库时的用户名

- org.quartz.dataSource.NAME.password
  - 非必须
  - string类型
  - 默认值“”
  - 连接数据库时的密码

- org.quartz.dataSource.NAME.maxConnections
  - 非必须
  - int类型
  - 默认值10
  - DataSource可以在其连接池中创建的最大连接数

- org.quartz.dataSource.NAME.validationQuery
  - 非必须
  - string 类型
  - 默认值null
  - 是可选的SQL查询字符串，DataSource可用于检测和替换失败/损坏的连接

- org.quartz.dataSource.NAME.idleConnectionValidationSeconds
  - 非必须
  - int类型
  - 默认值50
  - 空闲连接验证的时间间隔

- org.quartz.dataSource.NAME.validateOnCheckout
  - 非必须
  - boolean类型
  - 默认值false
  - 用于指定每次从连接池获取一个数据库连接时是否执行用于验证数据库连接的数据源SQL查询语句，以确保数据库连接是否仍然有效。如果设置为false，则在数据库连接加入连接池的时候验证

- org.quartz.dataSource.NAME.discardIdleConnectionsSeconds
  - 非必须
  - int 类型
  - 默认值 0
  - 当数据库连接闲置指定的秒数后将被丢弃。默认值 0，禁用该配置

> Quaryz DataSource 演示实例

```properties
rg.quartz.dataSource.myDS.driver = oracle.jdbc.driver.OracleDriver
org.quartz.dataSource.myDS.URL = jdbc:oracle:thin:@10.0.1.23:1521:demodb
org.quartz.dataSource.myDS.user = myUser
org.quartz.dataSource.myDS.password = myPassword
org.quartz.dataSource.myDS.maxConnections = 30
```

**<font color="red">还有一些其他的配置，这里就不在详细列举了，请自行参考配饰属性网站</font>**

## SchedulerFactory

> SchedulerFactory，顾名思义，用于构建一个Quartz Scheduler对象。下面将介绍两种SchedulerFactory

### StdSchedulerFactory

StdSchedulerFactory是org.quartz.SchedulerFactory接口的一个实现。它使用一组属性（java.util.Properties）来创建和初始化Quartz Scheduler。

属性通常存储在文件中并从文件中加载，但也可以由程序创建并直接传递到工厂。

简单地调用工厂中的getScheduler（）将生成调度程序，并初始化它（和它的ThreadPool，JobStore和DataSources）并返回一个句柄到它的公共接口。

### DirectSchedulerFactory

DirectSchedulerFactory是另一个SchedulerFactory实现。对于希望以更加程序化的方式创建其Scheduler实例的用户是有用的。

通常不鼓励使用它的用法，原因如下：

- 要求用户更好地了解他们正在做什么
- 它不允许声明性配置 - 换句话说，你最终会硬编辑所有调度程序的设置

## Job 与JobDetail

你定义了一个实现Job接口的类，这个类仅仅表明该job需要完成什么类型的任务，除此之外，Quartz还需要知道该Job实例所包含的属性；这将由JobDetail类来完成。

JobDetail实例是通过JobBuilder类创建的，导入该类下的所有静态方法，会让你编码时有DSL的感觉：

```java
import static org.quartz.JobBuilder.*;
```

让我们先看看Job的特征（nature）以及Job实例的生命期。不妨先回头看看演示实例中的代码。

可以看到，我们传给scheduler一个JobDetail实例，因为我们在创建JobDetail时，将要执行的job的类名传给了JobDetail，所以scheduler就知道了要执行何种类型的job；每次当scheduler执行job时，在调用其execute(…)方法之前会创建该类的一个新的实例；执行完毕，对该实例的引用就被丢弃了，实例会被垃圾回收；这种执行策略带来的一个后果是，job必须有一个无参的构造函数（当使用默认的JobFactory时）；另一个后果是，在job类中，不应该定义有状态的数据属性，因为在job的多次执行中，这些属性的值不会保留。

那么如何给job实例增加属性或配置呢？如何在job的多次执行中，跟踪job的状态呢？答案就是:JobDataMap，JobDetail对象的一部分。

### JobDataMap

JobDataMap中可以包含不限量的（序列化的）数据对象，在job实例执行的时候，可以使用其中的数据；JobDataMap是Java Map接口的一个实现，额外增加了一些便于存取基本类型的数据的方法。

将job加入到scheduler之前，在构建JobDetail时，可以将数据放入JobDataMap，如下示例：

```kotlin
@RequestMapping("/map1")
fun map1():String{
    //创建JobDetail
    val detail = JobBuilder.newJob(MapJob1::class.java)
        .withIdentity("JobForMap1", "group1")
        .usingJobData("str", "hello world") //传递string数据
        .usingJobData("float", 2.123f) //传递float数据
        .build()
    //创建Trigger
    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("TriggerForMap1", "group1")
        .withSchedule(SimpleScheduleBuilder.simpleSchedule())
        .build()
    //创建Scheduler
    val scheduler = SchedulerHelper.getScheduler("map1")
    scheduler?.scheduleJob(detail, trigger)
    scheduler?.start()
    return "通过JobDetail传递参数，请到控制台查看输出信息"
}
```

在job的执行过程中，可以从JobDataMap中取出数据，如下示例：

```kotlin
class MapJob1 : Job {
    /**
     * 重写Job接口方法
     *
     * @param context 传递的上下文内容
     * @throws JobExecutionException 异常信息
     */
    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext) {
        val key = context.jobDetail.key //获取JobDetail的key，包括名称和组信息
        val map = context.jobDetail.jobDataMap //获取通过JobDetail传递的数据信息
        val str = map.getString("str") //获取string类型数据
        val fl = map.getFloat("float") //获取float类型数据

        //输出到日志窗口
        println("job name is " + key.name + " str is " + str + " float is " + fl)
    }
}   
```

输出结果：

```java
job name is JobForMap1 str is hello world float is 2.123
```

**在Job执行时，JobExecutionContext中的JobDataMap为我们提供了很多的便利。它是JobDetail中的JobDataMap和Trigger中的JobDataMap的并集，但是如果存在相同的数据，则后者会覆盖前者的值。**

下面的示例，在job执行时，从JobExecutionContext中获取合并后的JobDataMap：

```kotlin
@RequestMapping("/map2")
fun map2():String{
    //创建JobDetail
    val detail = JobBuilder.newJob(MapJob2::class.java)
        .withIdentity("JobForMap2", "group1")
        .usingJobData("str", "hello world") //传递string数据
        .usingJobData("float", 2.123f) //传递float数据
        .build()
    //创建Trigger
    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("TriggerForMap1", "group1")
        .usingJobData("str", "hello wqj") //传递string数据，键和Detail传递的一样，测试数据是否会被覆盖
        .withSchedule(SimpleScheduleBuilder.simpleSchedule())
        .build()
    //创建Scheduler
    val scheduler = SchedulerHelper.getScheduler("map2")
    scheduler?.scheduleJob(detail, trigger)
    scheduler?.start()
    return "获取合并后的传递的数据，请到控制台查看输出信息"
}

class MapJob2 : Job {
    /**
     * 重写Job接口方法
     *
     * @param context 传递的上下文内容
     * @throws JobExecutionException 异常信息
     */
    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext) {

        //获取JobDetail的key，包括名称和组信息
        val key = context.jobDetail.key

        //获取合并的数据信息
        val map = context.mergedJobDataMap
        val str = map.getString("str") //获取string类型数据
        val fl = map.getFloat("float") //获取float类型数据

        //输出到日志窗口
        println("job name is " + key.name + " str is " + str + " float is " + fl)
    }
}
```

输出结果

```shell
job name is JobForMap2 str is hello wqj float is 2.123
```

如果你希望使用JobFactory实现数据的自动“注入”，则示例代码为：

```kotlin
class MapJob3 : Job {
    var str: String? = null
    var fl = 0f

    /**
     * 重写Job接口方法
     *
     * @param context 传递的上下文内容
     * @throws JobExecutionException 异常信息
     */
    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext) {

        //获取JobDetail的key，包括名称和组信息
        val key = context.jobDetail.key

        //获取合并的数据信息
        val map = context.mergedJobDataMap

        //输出到日志窗口
        println("job name is " + key.name + " str is " + str + " float is " + fl)
    }

    /**
     * str的set方法
     * @param str 传递的string值
     */
    @JvmName("setStr1")
    fun setStr(str: String?) {
        this.str = str
    }

    /**
     * fl的set方法
     * @param fl 传递float值
     */
    @JvmName("setFl1")
    fun setFl(fl: Float) {
        this.fl = fl
    }
}

@RequestMapping("/map3")
fun map3(): String {
    //创建JobDetail
    val detail = JobBuilder.newJob(MapJob3::class.java)
        .withIdentity("JobForMap3", "group1")
        .usingJobData("str", "hello world") //传递string数据
        .usingJobData("fl", 2.123f) //传递float数据
        .build()
    //创建Trigger
    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("TriggerForMap1", "group1")
        .withSchedule(SimpleScheduleBuilder.simpleSchedule())
        .build()
    //创建Scheduler
    val scheduler = SchedulerHelper.getScheduler("map3")
    scheduler?.scheduleJob(detail, trigger)
    scheduler?.start()
    return "自动注入传递的数据，请到控制台查看输出信息"
}
```

输出结果为：

```shell
job name is JobForMap3 str is hello world float is 2.123
```

### Job实例

你可以只创建一个job类，然后创建多个与该job关联的JobDetail实例，每一个实例都有自己的属性集和JobDataMap，最后，将所有的实例都加到scheduler中。

当一个trigger被触发时，与之关联的JobDetail实例会被加载，JobDetail引用的job类通过配置在Scheduler上的JobFactory进行初始化。默认的JobFactory实现，仅仅是调用job类的newInstance()方法，然后尝试调用JobDataMap中的key的setter方法。你也可以创建自己的JobFactory实现。

在Quartz的描述语言中，我们将保存后的JobDetail称为`job定义`或者`JobDetail实例`,将一个正在执行的job称为`job实例`或者`job定义的实例`。当我们使用`job`时，一般指代的是`job定义`，或者`JobDetail`；当我们提到实现`Job接口的类`时，通常使用`job类`。

### Job状态与并发

关于job的状态数据（即JobDataMap）和并发性，还有一些地方需要注意。

在job类上可以加入一些注解，这些注解会影响job的状态和并发性。

**@DisallowConcurrentExecution**

将该注解加到job类上，告诉Quartz不要并发地执行同一个job定义（这里指特定的job类）的多个实例。该限制是针对JobDetail的，而不是job类的。

当没有注解时，示例如下：

```kotlin
class StatusJob1 : Job {
    /**
     * 重写Job接口方法
     *
     * @param context 传递的上下文内容
     * @throws JobExecutionException 异常信息
     */
    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext) {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") //设置日期格式

        //输出到日志窗口
        println("Time is" + df.format(Date()))
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}

@RequestMapping("/status1")
fun status1():String{
    //创建一个scheduler
    val scheduler = SchedulerHelper.getScheduler("status1")
    //创建一个Trigger
    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1")
        .withSchedule(
            SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1)
                .withRepeatCount(3)
        ).build()
    //创建一个job
    val job = JobBuilder.newJob(StatusJob1::class.java)
        .withIdentity("myjob", "mygroup").build()
    //注册trigger并启动scheduler
    scheduler?.scheduleJob(job, trigger)
    scheduler?.start()
    return "并发演示，请到控制台查看输出信息"
}
```

结果：

```
Time is2021-05-08 13:47:59
Time is2021-05-08 13:48:00
Time is2021-05-08 13:48:01
Time is2021-05-08 13:48:02
```

从结果可以看出，上一个任务没执行完毕，下次任务已经开始执行。下面测试添加注解的：

```kotlin
@DisallowConcurrentExecution
class StatusJob2 : Job {
    /**
     * 重写Job接口方法
     *
     * @param context 传递的上下文内容
     * @throws JobExecutionException 异常信息
     */
    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext) {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") //设置日期格式

        //输出到日志窗口
        println("Time is" + df.format(Date()))
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}

@RequestMapping("/status2")
fun status2():String{
    //创建一个scheduler
    val scheduler = SchedulerHelper.getScheduler("status2")
    //创建一个Trigger
    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1")
        .withSchedule(
            SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1)
                .withRepeatCount(3)
        ).build()
    //创建一个job
    val job = JobBuilder.newJob(StatusJob2::class.java)
        .withIdentity("myjob", "mygroup").build()
    //注册trigger并启动scheduler
    scheduler?.scheduleJob(job, trigger)
    scheduler?.start()
    
    return "并发演示，请到控制台查看输出信息"
}
```

运行结果：

```java
Time is2021-05-08 13:52:27
Time is2021-05-08 13:52:30
Time is2021-05-08 13:52:33
Time is2021-05-08 13:52:36
```

可以看出，等上次任务执行完毕后，下次任务才开始执行

**@PersistJobDataAfterExecution**

将该注解加在job类上，告诉Quartz在成功执行了job类的execute方法后（没有发生任何异常），更新JobDetail中JobDataMap的数据，使得该job（即JobDetail）在下一次执行的时候，JobDataMap中是更新后的数据，而不是更新前的旧数据。和 @DisallowConcurrentExecution注解一样，尽管注解是加在job类上的，但其限制作用是针对job实例的，而不是job类的。

不带注解的实例

```kotlin
@DisallowConcurrentExecution
class StatusJob3 : Job {
    /**
     * 重写Job接口方法
     *
     * @param context 传递的上下文内容
     * @throws JobExecutionException 异常信息
     */
    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext) {
        val map = context.jobDetail.jobDataMap
        var count = map.getInt("count")
        val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

        //输出到日志窗口
        println("time is " + df.format(Date()) + " ,Count is " + count)
        count++
        map["count"] = count
        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}

@RequestMapping("/status3")
fun status3():String{
    //创建一个scheduler
    val scheduler = SchedulerHelper.getScheduler("status3")
    //创建一个Trigger
    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1")
        .withSchedule(
            SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1)
                .withRepeatCount(3)
        ).build()
    //创建一个job
    val job = JobBuilder.newJob(StatusJob3::class.java)
        .usingJobData("count", 1)
        .withIdentity("myjob", "mygroup").build()
    //注册trigger并启动scheduler
    scheduler?.scheduleJob(job, trigger)
    scheduler?.start()
    return "状态演示，请到控制台查看输出信息"
}
```

运行结果：

```
time is 2021/05/08 13:58:13 ,Count is 1
time is 2021/05/08 13:58:16 ,Count is 1
time is 2021/05/08 13:58:19 ,Count is 1
time is 2021/05/08 13:58:22 ,Count is 1
```

其他代码一样，添加注解后，运行结果如下：

```
time is 2021/05/08 14:00:11 ,Count is 1
time is 2021/05/08 14:00:14 ,Count is 2
time is 2021/05/08 14:00:17 ,Count is 3
time is 2021/05/08 14:00:20 ,Count is 4
```

<font color="red">如果你使用了@PersistJobDataAfterExecution注解，我们强烈建议你同时使用@DisallowConcurrentExecution注解，因为当同一个job（JobDetail）的两个实例被并发执行时，由于竞争，JobDataMap中存储的数据很可能是不确定的。</font>

## Triggers

### Trigger的公共属性

所有类型的trigger都有TriggerKey这个属性，表示trigger的身份；除此之外，trigger还有很多其它的公共属性。这些属性，在构建trigger的时候可以通过TriggerBuilder设置。

trigger的公共属性有：

- jobKey属性：当trigger触发时被执行的job的身份
- startTime属性：设置trigger第一次触发的时间；该属性的值是java.util.Date类型，表示某个指定的时间点；有些类型的trigger，会在设置的startTime时立即触发，有些类型的trigger，表示其触发是在startTime之后开始生效。
- endTime属性：表示trigger失效的时间点。

其它的属性，会在下文中解释。

### 优先级(priority)

Quartz可能没有足够的资源同时触发所有的trigger时，可以设置优先级来保证优先触发特定的trigger。

如果没有为trigger设置优先级，trigger使用默认优先级，值为5；priority属性的值可以是任意整数，正数、负数都可以；值越大，优先级越高。

注意：只有同时触发的trigger之间才会比较优先级。10:59触发的trigger总是在11:00触发的trigger之前执行。

注意：如果trigger是可恢复的，在恢复后再调度时，优先级与原trigger是一样的。

### 错过触发(misfire Instructions)

trigger还有一个重要的属性misfire。

如果scheduler关闭了，或者Quartz线程池中没有可用的线程来执行job，此时持久性的trigger就会错过(miss)其触发时间，即错过触发(misfire)。

不同类型的trigger，有不同的misfire机制。它们默认都使用“智能机制(smart policy)”，即根据trigger的类型和配置动态调整行为。当scheduler启动的时候，查询所有错过触发(misfire)的持久性trigger。然后根据它们各自的misfire机制更新trigger的信息。

### 日历示例(calendar)

Quartz的Calendar对象(不是java.util.Calendar对象)可以在定义和存储trigger的时候与trigger进行关联。Calendar用于从trigger的调度计划中排除时间段。

比如，可以创建一个trigger，每个工作日的上午9:30执行，然后增加一个Calendar，排除掉所有的商业节日。

任何实现了Calendar接口的可序列化对象都可以作为Calendar对象，Calendar接口如下：

```java
package org.quartz;

public interface Calendar {

  public boolean isTimeIncluded(long timeStamp);

  public long getNextIncludedTime(long timeStamp);

}
```

注意到这些方法的参数类型为long。你也许猜到了，他们就是毫秒单位的时间戳。即Calendar排除时间段的单位可以精确到毫秒。你也许对“排除一整天”的Calendar比较感兴趣。Quartz提供的org.quartz.impl.HolidayCalendar类可以很方便地实现。

Calendar必须先实例化，然后通过addCalendar()方法注册到scheduler。如果使用HolidayCalendar，实例化后，需要调用addExcludedDate(Date date)方法从调度计划中排除时间段。以下示例是将同一个Calendar实例用于多个trigger：

```java
HolidayCalendar cal = new HolidayCalendar();
cal.addExcludedDate( someDate );
cal.addExcludedDate( someOtherDate );

sched.addCalendar("myHolidays", cal, false);


Trigger t = newTrigger()
    .withIdentity("myTrigger")
    .forJob("myJob")
    .withSchedule(dailyAtHourAndMinute(9, 30)) // execute job daily at 9:30
    .modifiedByCalendar("myHolidays") // but not on holidays
    .build();

// .. schedule job with trigger

Trigger t2 = newTrigger()
    .withIdentity("myTrigger2")
    .forJob("myJob2")
    .withSchedule(dailyAtHourAndMinute(11, 30)) // execute job daily at 11:30
    .modifiedByCalendar("myHolidays") // but not on holidays
    .build();

// .. schedule job with trigger2
```

## SimpleTrigger

> SimpleTrigger可以满足的调度需求是：在具体的时间点执行一次，或者在具体的时间点执行，并且以指定的间隔重复执行若干次。
>
> 根据描述，你可能已经发现了，SimpleTrigger的属性包括：开始时间、结束时间、重复次数以及重复的间隔。

重复次数，可以是0、正整数，以及常量SimpleTrigger.REPEAT_INDEFINITELY。

重复的间隔，必须是0，或者long型的正数，表示毫秒。注意，如果重复间隔为0，trigger将会以重复次数并发执行(或者以scheduler可以处理的近似并发数)。

endTime属性的值会覆盖设置重复次数的属性值。

SimpleTrigger实例通过TriggerBuilder设置主要的属性，通过SimpleScheduleBuilder设置与SimpleTrigger相关的属性。

下面的例子，是基于简单调度(simple schedule)创建的trigger。建议都看一下，因为每个例子都包含一个不同的实现点。

要使用相关builder的静态方法，需要静态导入：

```java
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.DateBuilder.*:
```

指定时间开始触发，不重复：

通用的Job

```kotlin
class TriggerJob : Job {
    /**
     * 重写Job接口方法
     *
     * @param context 传递的上下文内容
     * @throws JobExecutionException 异常信息
     */
    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext) {
        val map = context.jobDetail.jobDataMap
        val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

        //输出到日志窗口
        println("time is " + df.format(Date()))
    }
}
```

```kotlin
@RequestMapping("/simple1")
fun simaple1(@RequestParam d:String): String {
    //创建一个scheduler
    val scheduler = SchedulerHelper.getScheduler("simple1")
    var df =SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    var date=Date()
    date=df.parse(d)
    //创建一个Trigger
    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1")
        .withSchedule(SimpleScheduleBuilder.simpleSchedule())
        .startAt(date).build()
    //创建一个job
    val job = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("myjob", "mygroup").build()
    //注册trigger并启动scheduler
    scheduler?.scheduleJob(job, trigger)
    scheduler?.start()
    return "指定时间点执行，请到控制台查看输出信息"
}
```

运行结果:

```
time is 2021/05/08 15:48:00
```

指定时间触发，每隔1秒执行一次，重复10次：

```kotlin
@RequestMapping("/simple2")
fun simaple2(@RequestParam d:String): String {
    //创建一个scheduler
    val scheduler = SchedulerHelper.getScheduler("simple2")
    var df =SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    var date=Date()
    date=df.parse(d)
    //创建一个Trigger
    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1")
        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).withRepeatCount(10))
        .startAt(date).build()
    //创建一个job
    val job = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("myjob", "mygroup").build()
    //注册trigger并启动scheduler
    scheduler?.scheduleJob(job, trigger)
    scheduler?.start()
    return "指定时间点执行，请到控制台查看输出信息"
}
```

执行结果：

```
time is 2021/05/08 15:52:00
time is 2021/05/08 15:52:01
time is 2021/05/08 15:52:02
time is 2021/05/08 15:52:03
time is 2021/05/08 15:52:04
time is 2021/05/08 15:52:05
time is 2021/05/08 15:52:06
time is 2021/05/08 15:52:07
time is 2021/05/08 15:52:08
time is 2021/05/08 15:52:09
time is 2021/05/08 15:52:10
```

5分钟以后开始触发，仅执行一次：

```kotlin
@RequestMapping("/simple3")
fun simaple3(): String {
    //创建一个scheduler
    val scheduler = SchedulerHelper.getScheduler("simple3")
    val df: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    println("begin time is " + df.format(Date()))
    //创建一个Trigger
    val trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1")
        .startAt(DateBuilder.futureDate(5, DateBuilder.IntervalUnit.MINUTE)).build()
    //创建一个job
    val job = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("myjob", "mygroup").build()
    //注册trigger并启动scheduler
    scheduler?.scheduleJob(job, trigger)
    scheduler?.start()
    return "五分钟后执行，请到控制台查看输出信息"
}
```

运行结果

```
begin time is 2021/05/08 15:59:10
time is 2021/05/08 16:04:10
```

立即触发，每分钟执行一次，直到指定的时间：

```kotlin
@RequestMapping("/simple4")
fun simaple4(@RequestParam d:String): String {
    //创建一个scheduler
    val scheduler = SchedulerHelper.getScheduler("simple4")
    var df =SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    var date=Date()
    date=df.parse(d)
    //创建一个Trigger
    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1")
        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(1).repeatForever())
        .endAt(date).build()
    //创建一个job
    val job = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("myjob", "mygroup").build()
    //注册trigger并启动scheduler
    scheduler?.scheduleJob(job, trigger)
    scheduler?.start()
    return "执行到指定时间，请到控制台查看输出信息"
}
```

执行结果：

```
time is 2021/05/08 16:11:30
time is 2021/05/08 16:12:30
time is 2021/05/08 16:13:30
time is 2021/05/08 16:14:30
```

## CronTrigger

> CronTrigger通常比Simple Trigger更有用，如果您需要基于日历的概念而不是按照SimpleTrigger的精确指定间隔进行重新启动的作业启动计划。
>
> 使用CronTrigger，您可以指定时间表，例如“每周五中午”或“每个工作日和上午9:30”，甚至“每周一至周五上午9:00至10点之间每5分钟”和1月份的星期五“。
>
> 即使如此，和SimpleTrigger一样，CronTrigger有一个startTime，它指定何时生效，以及一个（可选的）endTime，用于指定何时停止计划。

### Cron表达式

Cron-Expressions用于配置CronTrigger的实例。Cron Expressions是由七个子表达式组成的字符串，用于描述日程表的各个细节。这些子表达式用空格分隔，并表示：

1. Seconds  秒
2. Minutes 分
3. Hours 时
4. Day-of-Month 日期
5. Month 月
6. Day-of-Week 周
7. Year (optional field) 年(可选，留空)

例如：`0 0 12 ？ * WED`，这意味着每个星期三的12点

| 字段         | 允许值              | 允许的特殊符号  |
| ------------ | ------------------- | --------------- |
| Seconds      | 0-59整数            | , - * /         |
| Minutes      | 0-59整数            | , - * /         |
| Hours        | 0-23整数            | , - * /         |
| Day-of-Month | 1-31整数            | ,- * ? / L W C  |
| Month        | 1-12整数或者JAN-DEC | , - * /         |
| Day-of-Week  | 1-7整数或SUN-SAT    | , - * ? / L C # |
| Year         | 1970-2099           | , - * /         |

特殊符号含义：

- ,：表示列出枚举值
- -：表示范围
- *：表示匹配该域的所有值
- /：表示起始时间开始触发，然后每隔固定时间触发一次
- ?：只能用在DayofMonth和DayofWeek两个域。表示匹配改区域的任意值。日期和星期不能同时为*，也不能同时为？
- L：表示最后，只能出现在DayofWeek和DayofMonth域。
- W：表示有效工作日(周一到周五),只能出现在DayofMonth域，系统将在离指定日期的最近的有效工作日触发事件。例如：在 DayofMonth使用5W，如果5日是星期六，则将在最近的工作日：星期五，即4日触发。
- LW：这两个字符可以连用，表示在某个月最后一个工作日，即最后一个星期五。
- \#：用于确定每个月第几个星期几，只能出现在DayofMonth域。例如在4#2，表示某月的第二个星期三

### Cron表达式示例

CronTrigger示例1 - 创建一个触发器的表达式，每5分钟就会触发一次

`0 0/5 * * * ?`

CronTrigger示例2 - 创建触发器的表达式，每5分钟触发一次，分钟后10秒（即上午10时10分，上午10:05:10等）。

`10 0/5 * * * ?`

CronTrigger示例3 - 在每个星期三和星期五的10:30，11:30，12:30和13:30创建触发器的表达式。

`0 30 10-13 ? * WED，FRI`

CronTrigger示例4 - 创建触发器的表达式，每个月5日和20日上午8点至10点之间每半小时触发一次。请注意，触发器将不会在上午10点开始，仅在8:00，8:30，9:00和9:30

`0 0/30 8-9 5,20 * ?`

请注意，一些调度要求太复杂，无法用单一触发表示 - 例如“每上午9:00至10:00之间每5分钟，下午1:00至晚上10点之间每20分钟”一次。在这种情况下的解决方案是简单地创建两个触发器，并注册它们来运行相同的作业。

### 构建CronTriggers

CronTrigger实例使用TriggerBuilder（用于触发器的主要属性）和CronScheduleBuilder（对于CronTrigger特定的属性）构建。要以DSL风格使用这些构建器，请使用静态导入：

```java
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.DateBuilder.*:
```

建立一个触发器，每隔一分钟，每天上午8点至下午5点之间：

```java
@RequestMapping("/cron")
fun cron(): String {
    //创建一个scheduler
    val scheduler = SchedulerHelper.getScheduler("cron")
    //创建一个Trigger
    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1")
        .withSchedule(CronScheduleBuilder
            .cronSchedule("0 0/2 8-17 * * ?"))
        .build()
    //创建一个job
    val job = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("myjob", "mygroup").build()
    //注册trigger并启动scheduler
    scheduler?.scheduleJob(job, trigger)
    scheduler?.start()
    return "八点到十七点，每两分钟执行一次，请到控制台查看输出信息"
}
```

运行结果：

```
time is 2021/05/10 09:02:00
time is 2021/05/10 09:04:00
```

## TriggerListeners

> listener 是开发者创建的对象，用于根据调度程序中发生的事件执行操作。TriggerListeners接收到与触发器（trigger）相关的事件，JobListeners 接收与jobs相关的事件。

### org.quartz.TriggerListener接口

与触发相关的事件：`触发器触发`、`触发器失灵`、`触发完成`

| 成员               | 说明                                                         |
| ------------------ | ------------------------------------------------------------ |
| getName()          | 定义并返回监听器的名字                                       |
| triggerFired()     | 当与监听器相关联的 Trigger 被触发，Job 上的 execute() 方法将要被执行时，Scheduler 就调用这个方法。在全局 TriggerListener 情况下，这个方法为所有 Trigger 被调用。 |
| vetoJobExecution() | 在 Trigger 触发后，Job 将要被执行时由 Scheduler 调用这个方法。TriggerListener 给了一个选择去否决 Job 的执行。假如这个方法返回 true，这个 Job 将不会为此次 Trigger 触发而得到执行。 |
| triggerMisfired()  | Scheduler 调用这个方法是在 Trigger 错过触发时。如这个方法的 JavaDoc 所指出的，你应该关注此方法中持续时间长的逻辑：在出现许多错过触发的 Trigger 时，长逻辑会导致骨牌效应。你应当保持这方法尽量的小。 |
| triggerComplete()  | Trigger 被触发并且完成了 Job 的执行时，Scheduler 调用这个方法。这不是说这个 Trigger 将不再触发了，而仅仅是当前 Trigger 的触发(并且紧接着的 Job 执行) 结束时。这个 Trigger 也许还要在将来触发多次的。 |

### 自定义TriggerListener代码

```kotlin
class MyTriggerListener:TriggerListener {

    /**
     * 获取名字
     */
    override fun getName(): String {
        return "myTriggerListener"
    }

    /**
     * 在执行Job之前触发
     */
    override fun triggerFired(trigger: Trigger?, context: JobExecutionContext?) {
        println("在"+Date()+"触发了triggerFired方法！")
    }

    /**
     * 在执行Job之前触发，可以根据根究返回值来中断Job的执行
     */
    override fun vetoJobExecution(trigger: Trigger?, context: JobExecutionContext?): Boolean {
        var dataMap = context?.mergedJobDataMap

        println("在"+Date()+"触发了vetoJobExecution方法，且方法的返回值是"+dataMap?.getBoolean("isCancel"))

        return dataMap?.getBoolean("isCancel")!!
    }

    /**
     * 在错过触发时触发
     */
    override fun triggerMisfired(trigger: Trigger?) {
        println( "当前Trigger触发错过了");
    }

    /**
     * 在Job执行完毕后触发
     */
    override fun triggerComplete(
        trigger: Trigger?,
        context: JobExecutionContext?,
        triggerInstructionCode: Trigger.CompletedExecutionInstruction?
    ) {
        println("在"+Date()+"触发了triggerComplete方法！")
    }
}
```

### 实例

不中断Job执行

```kotlin
@RequestMapping("/tls1")
fun tls1(): String {
    //获取一个默认的scheduler
    val scheduler = SchedulerHelper.getScheduler("tls1")
    //获取一个Trigger
    var trigger = TriggerBuilder.newTrigger()
        .withIdentity("myTrigger", "group1")
        .withSchedule(SimpleScheduleBuilder.simpleSchedule())
        .build()
    //全局注册Listener
    //scheduler?.listenerManager?.addTriggerListener(MyTriggerListener())
    //局部注册Listener
    scheduler?.listenerManager?.addTriggerListener(
        MyTriggerListener(),
        KeyMatcher.keyEquals(TriggerKey("myTrigger", "group1"))
    )
    //获取一个JobDetail
    var jobDetail = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("myJob", "group1")
        .build()
    //添加调度工作，并开启调度
    scheduler?.scheduleJob(jobDetail,trigger)
    scheduler?.start()
    return "不中断任务的演示实例，输出结果请看控制台"
}
```

执行结果

```
在Mon May 10 11:33:23 CST 2021触发了triggerFired方法！
在Mon May 10 11:33:23 CST 2021触发了vetoJobExecution方法，且方法的返回值是false
time is 2021/05/10 11:33:23
在Mon May 10 11:33:23 CST 2021触发了triggerComplete方法！
```

中断Job执行

```kotlin
@RequestMapping("/tls2")
fun tls2(): String {
    //获取一个默认的scheduler
    val scheduler = SchedulerHelper.getScheduler("tls2")
    //获取一个Trigger
    var trigger = TriggerBuilder.newTrigger()
        .withIdentity("myTrigger", "group1")
        .usingJobData("isCancel",true)
        .withSchedule(SimpleScheduleBuilder.simpleSchedule())
        .build()
    //全局注册Listener
    //scheduler?.listenerManager?.addTriggerListener(MyTriggerListener())
    //局部注册Listener
    scheduler?.listenerManager?.addTriggerListener(
        MyTriggerListener(),
        KeyMatcher.keyEquals(TriggerKey("myTrigger", "group1"))
    )
    //获取一个JobDetail
    var jobDetail = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("myJob", "group1")
        .build()
    //添加调度工作，并开启调度
    scheduler?.scheduleJob(jobDetail,trigger)
    scheduler?.start()
    return "中断任务的演示实例，输出结果请看控制台"
```

执行结果

```
在Mon May 10 11:44:31 CST 2021触发了triggerFired方法！
在Mon May 10 11:44:31 CST 2021触发了vetoJobExecution方法，且方法的返回值是true
```

## JobListeners

### org.quartz.JobListener接口

与Job相关事件包括：`Job即将执行通知`、`Job即将完成通知

| 方法                 | 说明                                                         |
| -------------------- | ------------------------------------------------------------ |
| getName()            | 定义并返回监听器的名字                                       |
| jobToBeExecuted()    | Scheduler 在 JobDetail 将要被执行时调用这个方法              |
| jobExecutionVetoed() | Scheduler 在 JobDetail 即将被执行，但又被 TriggerListener 否决了时调用这个方法 |
| jobWasExecuted()     | Scheduler 在 JobDetail 被执行之后调用这个方法                |

### 自定义JobListener代码

```kotlin
class MyJobListener:JobListener{
    /**
     *获取Listener的名字
     */
    override fun getName(): String {
        return "myJobListener"
    }

    /**
     *Job将要执行前触发
     */
    override fun jobToBeExecuted(context: JobExecutionContext?) {
        println("在"+ Date() +"触发了jobToBeExecuted方法！")
    }

    /**
     *Job将要执行但是又被TrggerListener取消时触发
     */
    override fun jobExecutionVetoed(context: JobExecutionContext?) {
        println("在"+ Date() +"触发了jobExecutionVetoed方法！")
    }

    /**
     *Job执行后触发
     */
    override fun jobWasExecuted(context: JobExecutionContext?, jobException: JobExecutionException?) {
        println("在"+ Date() +"触发了jobWasExecuted方法！")
    }
}
```

### 实例

不中断任务

```kotlin
@RequestMapping("/jls1")
fun jls1(): String {
    //获取一个默认的scheduler
    val scheduler = SchedulerHelper.getScheduler("jls1")
    //获取一个Trigger
    var trigger = TriggerBuilder.newTrigger()
        .withIdentity("myTrigger", "group1")
        .withSchedule(SimpleScheduleBuilder.simpleSchedule())
        .build()
    //获取一个JobDetail
    var jobDetail = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("myJob", "group1")
        .build()
    //全局注册Listener
    //scheduler?.listenerManager?.addJobListener(MyJobListener())
    //局部注册Listener
    scheduler?.listenerManager?.addJobListener(
        MyJobListener(),
        KeyMatcher.keyEquals(JobKey("myJob", "group1"))
    )
    //添加调度工作，并开启调度
    scheduler?.scheduleJob(jobDetail,trigger)
    scheduler?.start()
    return "不中断任务的演示实例，输出结果请看控制台"
}
```

执行结果

```
在Tue May 11 07:49:27 CST 2021触发了jobToBeExecuted方法！
time is 2021/05/11 07:49:27
在Tue May 11 07:49:27 CST 2021触发了jobWasExecuted方法！
```

中断任务实例，中断任务是通过TriggerListener实现的

```kotlin
@RequestMapping("/jls2")
fun jls2(): String {
    //获取一个默认的scheduler
    val scheduler = SchedulerHelper.getScheduler("jls2")
    //获取一个Trigger
    var trigger = TriggerBuilder.newTrigger()
        .withIdentity("myTrigger", "group1")
        .usingJobData("isCancel",true)
        .withSchedule(SimpleScheduleBuilder.simpleSchedule())
        .build()
    //全局注册Listener
    //scheduler?.listenerManager?.addTriggerListener(MyTriggerListener())
    //局部注册Listener
    scheduler?.listenerManager?.addTriggerListener(
        MyTriggerListener(),
        KeyMatcher.keyEquals(TriggerKey("myTrigger", "group1"))
    )
    //获取一个JobDetail
    var jobDetail = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("myJob", "group1")
        .build()
    //局部注册Listener
    scheduler?.listenerManager?.addJobListener(
        MyJobListener(),
        KeyMatcher.keyEquals(JobKey("myJob", "group1"))
    )
    //添加调度工作，并开启调度
    scheduler?.scheduleJob(jobDetail,trigger)
    scheduler?.start()
    return "中断任务的演示实例，输出结果请看控制台"
}
```

执行结果

```
在Tue May 11 07:59:41 CST 2021触发了triggerFired方法！
在Tue May 11 07:59:41 CST 2021触发了vetoJobExecution方法，且方法的返回值是true
在Tue May 11 07:59:41 CST 2021触发了jobExecutionVetoed方法！
```

## SchedulerListeners

SchedulerListeners非常类似于TriggerListeners和JobListeners，除了它们在Scheduler本身中接收到事件的通知 - 不与特定触发器（trigger）或job相关的事件。

### org.quartz.SchedulerListener接口

与计划程序相关的事件包括：添加job/触发器，删除job/触发器，调度程序中的严重错误，关闭调度程序的通知等。

| 成员                     | 说明                                                         |
| ------------------------ | ------------------------------------------------------------ |
| jobAdded                 | 任务添加时触发                                               |
| jobDeleted               | 任务删除时触发                                               |
| jobScheduled()           | Scheduler 在有新的 JobDetail 部署时调用此方法                |
| jobUnscheduled()         | Scheduler 在有新的 JobDetail卸载时调用此方法                 |
| triggerFinalized()       | 当一个 Trigger 来到了再也不会触发的状态时调用这个方法        |
| triggersPaused()         | Scheduler 调用这个方法是发生在一个 Trigger 或 Trigger 组被暂停时。假如是 Trigger 组的话，triggerName 参数将为 null |
| triggersResumed()        | Scheduler 调用这个方法是发生成一个 Trigger 或 Trigger 组从暂停中恢复时。假如是 Trigger 组的话，triggerName 参数将为 null |
| jobsPaused()             | 当一个或一组 JobDetail 暂停时调用这个方法                    |
| jobsResumed()            | 当一个或一组 Job 从暂停上恢复时调用这个方法。假如是一个 Job 组，jobName 参数将为 null |
| schedulerError()         | Scheduler 的正常运行期间产生一个严重错误时调用这个方法，我们可以使用 SchedulerException 的 getErrorCode() 或者 getUnderlyingException() 方法或获取到特定错误的更详尽的信息 |
| schedulerInStandbyMode() | 进入待机模式时触发，关闭调度器或者调用待机方法时先进入待机模式 |
| schedulerShuttingdown()  | 关闭Scheduler时触发                                          |
| schedulerShutdown()      | 关闭Scheduler后触发                                          |
| schedulingDataCleared()  | 清空调度器后触发                                             |

### 自定义SchedulerListener代码

```kotlin
class MySchedulerListener:SchedulerListener {

    /**
     * JobDetail add时触发
     */
    override fun jobAdded(jobDetail: JobDetail?) {
        println("在"+ Date() +"触发了jobAdded方法！")
    }

    /**
     * JobDetail delete时触发
     */
    override fun jobDeleted(jobKey: JobKey?) {
        println("在"+ Date() +"触发了jobDeleted方法！")
    }

    /**
     * 任务被部署时触发
     */
    override fun jobScheduled(trigger: Trigger?) {
        println("在"+ Date() +"触发了jobScheduled方法！")
    }

    /**
     * 任务被卸载时触发
     */
    override fun jobUnscheduled(triggerKey: TriggerKey?) {
        println("在"+ Date() +"触发了jobUnscheduled方法！")
    }

    /**
     * 任务启动后触发
     */
    override fun schedulerStarted() {
        println("在"+ Date() +"触发了schedulerStarted方法！")
    }

    /**
     * Job 启动过程中触发
     */
    override fun schedulerStarting() {
        println("在"+ Date() +"触发了schedulerStarting方法！")
    }


    /**
     * 当任务执行完，且以后不再执行时触发
     */
    override fun triggerFinalized(trigger: Trigger?) {
        println("在"+ Date() +"触发了triggerFinalized方法！")
    }

    /**
     * 暂停Trigger时触发
     */
    override fun triggerPaused(triggerKey: TriggerKey?) {
        println("在"+ Date() +"触发了triggerPaused方法！")
    }


    /**
     * 恢复Trigger时触发
     */
    override fun triggerResumed(triggerKey: TriggerKey?) {
        println("在"+ Date() +"触发了triggerResumed方法！")
    }

    /**
     * 暂停Trigger时触发
     */
    override fun triggersPaused(triggerGroup: String?) {
        println("在"+ Date() +"触发了triggersPaused方法！")
    }

    /**
     * 恢复Trigger时触发
     */
    override fun triggersResumed(triggerGroup: String?) {
        println("在"+ Date() +"触发了triggersResumed方法！")
    }

    /**
     * 暂停Job时触发
     */
    override fun jobPaused(jobKey: JobKey?) {
        println("在"+ Date() +"触发了jobPaused方法！")
    }

    /**
     * 暂停Job时触发
     */
    override fun jobsPaused(jobGroup: String?) {
        println("在"+ Date() +"触发了jobsPaused方法！")
    }

    /**
     * 恢复Job时触发
     */
    override fun jobResumed(jobKey: JobKey?) {
        println("在"+ Date() +"触发了jobResumed方法！")
    }

    /**
     * 恢复Job时触发
     */
    override fun jobsResumed(jobGroup: String?) {
        println("在"+ Date() +"触发了jobsResumed方法！")
    }

    /**
     * Job 发生错误时触发
     */
    override fun schedulerError(msg: String?, cause: SchedulerException?) {
        println("在"+ Date() +"触发了schedulerError方法！")
    }

    /**
     * 待机模式触发，执行关闭和待机方法时会先进入待机模式
     */
    override fun schedulerInStandbyMode() {
        println("在"+ Date() +"触发了schedulerInStandbyMode方法！")
    }


    /**
     * 调度器关闭后触发
     */
    override fun schedulerShutdown() {
        println("在"+ Date() +"触发了schedulerShutdown方法！")
    }

    /**
     * 调度器正在关闭时触发
     */
    override fun schedulerShuttingdown() {
        println("在"+ Date() +"触发了schedulerShuttingdown方法！")
    }

    /**
     * 完全++清空调度器后触发
     */
    override fun schedulingDataCleared() {
        println("在"+ Date() +"触发了schedulingDataCleared方法！")
    }
}
```

### 实例

添加一个Job

```kotlin
@RequestMapping("/sls1")
fun sls1(): String {
    val scheduler = SchedulerHelper.getScheduler("sls1")
    if(scheduler?.listenerManager?.schedulerListeners?.isEmpty() == true){
        scheduler?.listenerManager?.addSchedulerListener(MySchedulerListener())
    }
    var job = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("slsJob", "groupSls")
        .storeDurably()
        .build()
    try {
        scheduler?.addJob(job, false)
    } catch (e: Exception) {
        return e.message!!
    }
    return "添加一个job，请看控制到输出信息"
}
```

执行结果

```
在Tue May 11 08:56:28 CST 2021触发了jobAdded方法！
```

**防止Listener接口重复添加，在添加的时候可以进行判断，如果为空才添加**

删除一个Job

```kotlin
@RequestMapping("/sls2")
fun sls2(): String {
    var scheduler = StdSchedulerFactory.getDefaultScheduler()
    scheduler.deleteJob(JobKey.jobKey("slsJob", "groupSls"))
    return "删除一个job，请看控制到输出信息"
}
```

执行结果

```
在Tue May 11 08:58:50 CST 2021触发了jobDeleted方法！
```

部署一个job

```kotlin
@RequestMapping("/sls3")
fun sls3():String{
    //获取一个调度器
    val scheduler = SchedulerHelper.getScheduler("sls3")
    //获取一个Job
    var job = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("job","group1")
        .build()
    //获取一个Trigger
    var trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger","group1")
        .build()
    //注册监听接口
    if(scheduler?.listenerManager?.schedulerListeners?.isEmpty() == true){
        scheduler?.listenerManager?.addSchedulerListener(MySchedulerListener())
    }
    scheduler?.scheduleJob(job,trigger)
    return "部署一个job，请看控制台输出信息"
```

执行结果

```
在Tue May 11 14:32:59 CST 2021触发了jobAdded方法！
在Tue May 11 14:32:59 CST 2021触发了jobScheduled方法！
```

由执行结果可以看出，scheduleJob方法，除了触发jobScheduled方法，还出发jobAdded方法

卸载一个任务

```kotlin
@RequestMapping("/sls4")
fun sls4():String{
    //获取一个调度器
    val scheduler = SchedulerHelper.getScheduler("sls3")
    scheduler?.unscheduleJob(TriggerKey.triggerKey("trigger","group1"))
    return "卸载一个job，请看控制台输出信息"
}
```

执行结果

```
在Tue May 11 14:40:42 CST 2021触发了jobDeleted方法！
在Tue May 11 14:40:42 CST 2021触发了jobUnscheduled方法！
```

由执行结果可以看出，unscheduleJob方法除了触发jobUnscheduled方法，还触发jobDeleted方法

执行一个Job，并运行四次后停止

```kotlin
@RequestMapping("/sls5")
fun sls5():String{
    //获取一个调度器
    val scheduler = SchedulerHelper.getScheduler("sls5")
    //获取一个Job
    var job = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("job","group1")
        .build()
    //获取一个Trigger
    var trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger","group1")
        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
            .withIntervalInSeconds(2)
            .withRepeatCount(3))
        .build()
    //注册监听接口
    if(scheduler?.listenerManager?.schedulerListeners?.isEmpty() == true){
        scheduler?.listenerManager?.addSchedulerListener(MySchedulerListener())
    }
    scheduler?.scheduleJob(job,trigger)
    scheduler?.start()
    return "启动一个job，并运行四次后自动停止，请看控制台输出信息"
}
```

执行结果

```
在Tue May 11 14:48:29 CST 2021触发了jobAdded方法！
在Tue May 11 14:48:29 CST 2021触发了jobScheduled方法！
在Tue May 11 14:48:29 CST 2021触发了schedulerStarting方法！
2021-05-11 14:48:29.717  INFO 3780 --- [nio-8080-exec-2] org.quartz.core.QuartzScheduler          : Scheduler sls5_$_NON_CLUSTERED started.
在Tue May 11 14:48:29 CST 2021触发了schedulerStarted方法！
time is 2021/05/11 14:48:29
time is 2021/05/11 14:48:31
time is 2021/05/11 14:48:33
time is 2021/05/11 14:48:35
在Tue May 11 14:48:35 CST 2021触发了triggerFinalized方法！
在Tue May 11 14:48:35 CST 2021触发了jobDeleted方法！
```

通过TriggerKey暂停和恢复Trigger

```kotlin
@RequestMapping("/sls6")
fun sls6():String{
    //获取一个调度器
    val scheduler = SchedulerHelper.getScheduler("sls6")
    //获取一个Job
    var job = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("job","group1")
        .build()
    //获取一个Trigger
    var trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger","group1")
        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
            .withIntervalInSeconds(2)
            .withRepeatCount(6))
        .build()
    //注册监听接口
    if(scheduler?.listenerManager?.schedulerListeners?.isEmpty() == true){
        scheduler?.listenerManager?.addSchedulerListener(MySchedulerListener())
    }
    scheduler?.scheduleJob(job,trigger)
    scheduler?.start()
    Thread.sleep(2000)
    scheduler?.pauseTrigger(TriggerKey.triggerKey("trigger","group1"))
    Thread.sleep(2000)
    scheduler?.resumeTrigger(TriggerKey.triggerKey("trigger","group1"))
    return "通过TriggerKey暂停和恢复Trigger，并运行四次后自动停止，请看控制台输出信息"
}
```

执行结果

```
在Tue May 11 15:20:43 CST 2021触发了jobAdded方法！
在Tue May 11 15:20:43 CST 2021触发了jobScheduled方法！
在Tue May 11 15:20:43 CST 2021触发了schedulerStarting方法！
2021-05-11 15:20:43.269  INFO 18040 --- [nio-8080-exec-3] org.quartz.core.QuartzScheduler          : Scheduler sls6_$_NON_CLUSTERED started.
在Tue May 11 15:20:43 CST 2021触发了schedulerStarted方法！
time is 2021/05/11 15:20:43
在Tue May 11 15:20:45 CST 2021触发了triggerPaused方法！
time is 2021/05/11 15:20:45
在Tue May 11 15:20:47 CST 2021触发了triggerResumed方法！
time is 2021/05/11 15:20:47
time is 2021/05/11 15:20:49
time is 2021/05/11 15:20:51
time is 2021/05/11 15:20:53
time is 2021/05/11 15:20:55
在Tue May 11 15:20:55 CST 2021触发了triggerFinalized方法！
在Tue May 11 15:20:55 CST 2021触发了jobDeleted方法！
```

通过JobKey暂停和恢复Job

```kotlin
@RequestMapping("/sls7")
fun sls7():String{
    //获取一个调度器
    val scheduler = SchedulerHelper.getScheduler("sls7")
    //获取一个Job
    var job = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("job","group1")
        .build()
    //获取一个Trigger
    var trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger","group1")
        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
            .withIntervalInSeconds(2)
            .withRepeatCount(6))
        .build()
    //注册监听接口
    if(scheduler?.listenerManager?.schedulerListeners?.isEmpty() == true){
        scheduler?.listenerManager?.addSchedulerListener(MySchedulerListener())
    }
    scheduler?.scheduleJob(job,trigger)
    scheduler?.start()
    Thread.sleep(2000)
    scheduler?.pauseJob(JobKey.jobKey("job","group1"))
    Thread.sleep(2000)
    scheduler?.resumeJob(JobKey.jobKey("job","group1"))
    return "暂停和恢复Job，并运行四次后自动停止，请看控制台输出信息"
}
```

执行结果

```
在Tue May 11 15:28:30 CST 2021触发了jobAdded方法！
在Tue May 11 15:28:30 CST 2021触发了jobScheduled方法！
在Tue May 11 15:28:30 CST 2021触发了schedulerStarting方法！
2021-05-11 15:28:30.151  INFO 18864 --- [nio-8080-exec-3] org.quartz.core.QuartzScheduler          : Scheduler sls7_$_NON_CLUSTERED started.
在Tue May 11 15:28:30 CST 2021触发了schedulerStarted方法！
time is 2021/05/11 15:28:30
在Tue May 11 15:28:32 CST 2021触发了jobPaused方法！
time is 2021/05/11 15:28:32
在Tue May 11 15:28:34 CST 2021触发了jobResumed方法！
time is 2021/05/11 15:28:34
time is 2021/05/11 15:28:36
time is 2021/05/11 15:28:38
time is 2021/05/11 15:28:40
time is 2021/05/11 15:28:42
```

关闭调度器

```kotlin
@RequestMapping("/sls8")
fun sls8():String{
    //获取一个调度器
    val scheduler = SchedulerHelper.getScheduler("sls8")
    //获取一个Job
    var job = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("job","group1")
        .build()
    //获取一个Trigger
    var trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger","group1")
        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
            .withIntervalInSeconds(1)
            .repeatForever())
        .build()
    //注册监听接口
    if(scheduler?.listenerManager?.schedulerListeners?.isEmpty() == true){
        scheduler?.listenerManager?.addSchedulerListener(MySchedulerListener())
    }
    scheduler?.scheduleJob(job,trigger)
    scheduler?.start()
    Thread.sleep(3000)
    scheduler?.shutdown()
    return "关闭调度器，请看控制台输出信息"
}
```

执行结果

```
在Tue May 11 16:10:27 CST 2021触发了jobAdded方法！
在Tue May 11 16:10:27 CST 2021触发了jobScheduled方法！
在Tue May 11 16:10:27 CST 2021触发了schedulerStarting方法！
2021-05-11 16:10:27.313  INFO 9228 --- [nio-8080-exec-3] org.quartz.core.QuartzScheduler          : Scheduler sls8_$_NON_CLUSTERED started.
在Tue May 11 16:10:27 CST 2021触发了schedulerStarted方法！
time is 2021/05/11 16:10:27
time is 2021/05/11 16:10:28
time is 2021/05/11 16:10:29
time is 2021/05/11 16:10:30
2021-05-11 16:10:30.328  INFO 9228 --- [nio-8080-exec-3] org.quartz.core.QuartzScheduler          : Scheduler sls8_$_NON_CLUSTERED shutting down.
2021-05-11 16:10:30.328  INFO 9228 --- [nio-8080-exec-3] org.quartz.core.QuartzScheduler          : Scheduler sls8_$_NON_CLUSTERED paused.
在Tue May 11 16:10:30 CST 2021触发了schedulerInStandbyMode方法！
在Tue May 11 16:10:30 CST 2021触发了schedulerShuttingdown方法！
在Tue May 11 16:10:30 CST 2021触发了schedulerShutdown方法！
2021-05-11 16:10:30.329  INFO 9228 --- [nio-8080-exec-3] org.quartz.core.QuartzScheduler          : Scheduler sls8_$_NON_CLUSTERED shutdown complete.
```

清空调度器

```kotlin
@RequestMapping("/sls9")
fun sls9():String{
    //获取一个调度器
    val scheduler = SchedulerHelper.getScheduler("sls9")
    //获取一个Job
    var job = JobBuilder.newJob(TriggerJob::class.java)
        .withIdentity("job","group1")
        .build()
    //获取一个Trigger
    var trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger","group1")
        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
            .withIntervalInSeconds(1)
            .repeatForever())
        .build()
    //注册监听接口
    if(scheduler?.listenerManager?.schedulerListeners?.isEmpty() == true){
        scheduler?.listenerManager?.addSchedulerListener(MySchedulerListener())
    }
    scheduler?.scheduleJob(job,trigger)
    scheduler?.clear()
    return "清空调度器，请看控制台输出信息"
}
```

执行结果

```
在Tue May 11 16:21:37 CST 2021触发了jobAdded方法！
在Tue May 11 16:21:37 CST 2021触发了jobScheduled方法！
在Tue May 11 16:21:37 CST 2021触发了jobDeleted方法！
在Tue May 11 16:21:37 CST 2021触发了schedulingDataCleared方法！
```

## Job Store

> JobStore负责跟踪提供给调度程序的所有的工作数据，包括：Jobs，triggers,日历等信息。其中quartz的存储方式有分为RAM和JDBC两种方式。

### 常用的JobStore

- RAMJobStore

  RAMJobStore是使用最简单的JobStore，它也是性能最高的（在CPU时间方面）。RAMJobStore以其明显的方式获取其名称：它将其所有数据保存在RAM中。这就是为什么它是闪电般快的，也是为什么这么简单的配置。缺点是当您的应用程序结束（或崩溃）时，所有调度信息都将丢失 - 这意味着RAMJobStore无法履行作业和triggers上的“非易失性”设置。对于某些应用程序，这是可以接受的 - 甚至是所需的行为，但对于其他应用程序，这可能是灾难性的。

  quartz框架默认的方式采用RAM的配置方式，具体的配置文件在 [quartz-core/src/main/resources/org/quartz/quartz.properties](https://github.com/quartz-scheduler/quartz/blob/d42fb7770f287afbf91f6629d90e7698761ad7d8/quartz-core/src/main/resources/org/quartz/quartz.properties)中有一条如下：

  ```java
  org.quartz.jobStore.class: org.quartz.simpl.RAMJobStore
  ```

- JDBC JobStore

  JDBCJobStore也被恰当地命名 - 它通过JDBC将其所有数据保存在数据库中。因此，配置比RAMJobStore要复杂一点，而且也不是那么快。但是，性能下降并不是很糟糕，特别是如果您在主键上构建具有索引的数据库表。

  JDBCJobStore几乎与任何数据库一起使用，已被广泛应用于Oracle，PostgreSQL，MySQL，MS SQLServer，HSQLDB和DB2。要使用JDBCJobStore，必须首先创建一组数据库表以供Quartz使用。

### 数据库表

#### MySQL的脚本

```sql
DROP TABLE IF EXISTS QRTZ_FIRED_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_PAUSED_TRIGGER_GRPS;
DROP TABLE IF EXISTS QRTZ_SCHEDULER_STATE;
DROP TABLE IF EXISTS QRTZ_LOCKS;
DROP TABLE IF EXISTS QRTZ_SIMPLE_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_SIMPROP_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_CRON_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_BLOB_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_TRIGGERS;
DROP TABLE IF EXISTS QRTZ_JOB_DETAILS;
DROP TABLE IF EXISTS QRTZ_CALENDARS;

CREATE TABLE QRTZ_JOB_DETAILS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  JOB_NAME  VARCHAR(200) NOT NULL,
  JOB_GROUP VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(250) NULL,
  JOB_CLASS_NAME   VARCHAR(250) NOT NULL,
  IS_DURABLE VARCHAR(1) NOT NULL,
  IS_NONCONCURRENT VARCHAR(1) NOT NULL,
  IS_UPDATE_DATA VARCHAR(1) NOT NULL,
  REQUESTS_RECOVERY VARCHAR(1) NOT NULL,
  JOB_DATA BLOB NULL,
  PRIMARY KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_TRIGGERS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  JOB_NAME  VARCHAR(200) NOT NULL,
  JOB_GROUP VARCHAR(200) NOT NULL,
  DESCRIPTION VARCHAR(250) NULL,
  NEXT_FIRE_TIME BIGINT(13) NULL,
  PREV_FIRE_TIME BIGINT(13) NULL,
  PRIORITY INTEGER NULL,
  TRIGGER_STATE VARCHAR(16) NOT NULL,
  TRIGGER_TYPE VARCHAR(8) NOT NULL,
  START_TIME BIGINT(13) NOT NULL,
  END_TIME BIGINT(13) NULL,
  CALENDAR_NAME VARCHAR(200) NULL,
  MISFIRE_INSTR SMALLINT(2) NULL,
  JOB_DATA BLOB NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,JOB_NAME,JOB_GROUP)
      REFERENCES QRTZ_JOB_DETAILS(SCHED_NAME,JOB_NAME,JOB_GROUP)
);

CREATE TABLE QRTZ_SIMPLE_TRIGGERS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  REPEAT_COUNT BIGINT(7) NOT NULL,
  REPEAT_INTERVAL BIGINT(12) NOT NULL,
  TIMES_TRIGGERED BIGINT(10) NOT NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
      REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CRON_TRIGGERS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  CRON_EXPRESSION VARCHAR(200) NOT NULL,
  TIME_ZONE_ID VARCHAR(80),
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
      REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_SIMPROP_TRIGGERS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  STR_PROP_1 VARCHAR(512) NULL,
  STR_PROP_2 VARCHAR(512) NULL,
  STR_PROP_3 VARCHAR(512) NULL,
  INT_PROP_1 INT NULL,
  INT_PROP_2 INT NULL,
  LONG_PROP_1 BIGINT NULL,
  LONG_PROP_2 BIGINT NULL,
  DEC_PROP_1 NUMERIC(13,4) NULL,
  DEC_PROP_2 NUMERIC(13,4) NULL,
  BOOL_PROP_1 VARCHAR(1) NULL,
  BOOL_PROP_2 VARCHAR(1) NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
  REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_BLOB_TRIGGERS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  BLOB_DATA BLOB NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP),
  FOREIGN KEY (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
      REFERENCES QRTZ_TRIGGERS(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
);

CREATE TABLE QRTZ_CALENDARS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  CALENDAR_NAME  VARCHAR(200) NOT NULL,
  CALENDAR BLOB NOT NULL,
  PRIMARY KEY (SCHED_NAME,CALENDAR_NAME)
);

CREATE TABLE QRTZ_PAUSED_TRIGGER_GRPS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  TRIGGER_GROUP  VARCHAR(200) NOT NULL,
  PRIMARY KEY (SCHED_NAME,TRIGGER_GROUP)
);
  
CREATE TABLE QRTZ_FIRED_TRIGGERS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  ENTRY_ID VARCHAR(95) NOT NULL,
  TRIGGER_NAME VARCHAR(200) NOT NULL,
  TRIGGER_GROUP VARCHAR(200) NOT NULL,
  INSTANCE_NAME VARCHAR(200) NOT NULL,
  FIRED_TIME BIGINT(13) NOT NULL,
  SCHED_TIME BIGINT(13) NOT NULL,
  PRIORITY INTEGER NOT NULL,
  STATE VARCHAR(16) NOT NULL,
  JOB_NAME VARCHAR(200) NULL,
  JOB_GROUP VARCHAR(200) NULL,
  IS_NONCONCURRENT VARCHAR(1) NULL,
  REQUESTS_RECOVERY VARCHAR(1) NULL,
  PRIMARY KEY (SCHED_NAME,ENTRY_ID)
);
  
CREATE TABLE QRTZ_SCHEDULER_STATE
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  INSTANCE_NAME VARCHAR(200) NOT NULL,
  LAST_CHECKIN_TIME BIGINT(13) NOT NULL,
  CHECKIN_INTERVAL BIGINT(13) NOT NULL,
  PRIMARY KEY (SCHED_NAME,INSTANCE_NAME)
);

CREATE TABLE QRTZ_LOCKS
(
  SCHED_NAME VARCHAR(120) NOT NULL,
  LOCK_NAME  VARCHAR(40) NOT NULL,
    PRIMARY KEY (SCHED_NAME,LOCK_NAME)
);

commit;
```

####  各个表作用说明：

  - qrtz_blob_triggers
  
    自定义的triggers使用blog类型进行存储，非自定义的triggers不会存放在此表中，Quartz提供的triggers包括：CronTrigger，CalendarIntervalTrigger，
  
    DailyTimeIntervalTrigger以及SimpleTrigger，这几个trigger信息会保存在后面的几张表中；
  
  - qrtz_cron_triggers
  
    存储CronTrigger，这也是我们使用最多的触发器。

- qrtz_simple_triggers

  存储SimpleTrigger

- qrtz_simprop_triggers

  存储CalendarIntervalTrigger和DailyTimeIntervalTrigger两种类型的触发器

- qrtz_fired_triggers

  存储已经触发的trigger相关信息，trigger随着时间的推移状态发生变化，直到最后trigger执行完成，从表中被删除

- qrtz_triggers

  存储定义的trigger

- qrtz_job_details

  存储jobDetails信息，相关信息在定义的时候指定

- qrtz_calendars

  Quartz为我们提供了日历的功能，可以自己定义一个时间段，可以控制触发器在这个时间段内触发或者不触发；现在提供6种类型：AnnualCalendar，CronCalendar，DailyCalendar，HolidayCalendar，MonthlyCalendar，WeeklyCalenda

- qrtz_paused_trigger_grps

  存放暂停掉的触发器（以组为单位的暂停才会存放在这里）

- qrtz_scheduler_state

  存储所有节点的scheduler，会定期检查scheduler是否失效

- qrtz_locks

  Quartz提供的锁表，为多个节点调度提供分布式锁，实现分布式调度，默认有2个锁

  - STATE_ACCESS主要用在scheduler定期检查是否失效的时候，保证只有一个节点去处理已经失效的scheduler
  - TRIGGER_ACCESS主要用在TRIGGER被调度的时候，保证只有一个节点去执行调度

#### 配置文件

建立quartz.properties文件并添加配置信息

```properties
#============================================================================
# 基础配置
#============================================================================

# 设置调度器的实例名(instanceName) 和实例ID (instanceId)
org.quartz.scheduler.instanceName: MyScheduler
#如果使用集群，instanceId必须唯一，设置成AUTO
org.quartz.scheduler.instanceId = AUTO

#============================================================================
# 调度器线程池配置
#============================================================================

org.quartz.threadPool.class: org.quartz.simpl.SimpleThreadPool
# 指定多少个工作者线程被创建用来处理 Job
org.quartz.threadPool.threadCount: 20
# 设置工作者线程的优先级（最大值10，最小值1，常用值5）
org.quartz.threadPool.threadPriority: 5


#============================================================================
# Configure JobStore 作业存储配置
#============================================================================

# 持久化配置（存储方式使用JobStoreTX，也就是数据库）
org.quartz.jobStore.class:org.quartz.impl.jdbcjobstore.JobStoreTX

#数据库中quartz表的表名前缀
org.quartz.jobStore.tablePrefix:qrtz_
org.quartz.jobStore.misfireThreshold: 5000

#是否使用集群（如果项目只部署到 一台服务器，就不用了）
org.quartz.jobStore.isClustered = false

#数据库别名
org.quartz.jobStore.dataSource : qzDS

#设置数据源 mysql
org.quartz.dataSource.qzDS.driver:com.mysql.cj.jdbc.Driver
org.quartz.dataSource.qzDS.URL:jdbc:mysql://localhost:3306/quartz
org.quartz.dataSource.qzDS.user:root
org.quartz.dataSource.qzDS.password:root
org.quartz.dataSource.qzDS.maxConnections:10
```

application.properties

```properties
spring.datasource.name=qzDS
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/quartz?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8
spring.datasource.username=root
spring.datasource.password=lttc12345$  
```

### 演示实例代码

#### Config类

```kotlin
@Configuration
open class SchedulerConfig {
    @Bean
    @Throws(IOException::class)
    open fun schedulerFactoryBean(dataSource: DataSource?): SchedulerFactoryBean {
        val factory = SchedulerFactoryBean()
        factory.setDataSource(dataSource)

        // quartz参数
        factory.setQuartzProperties(quartzProperties())
        factory.setSchedulerName("MyScheduler")

        // 延时启动
        factory.setStartupDelay(1)
        factory.setApplicationContextSchedulerContextKey("applicationContextKey")

        // 可选，QuartzScheduler
        // 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        factory.setOverwriteExistingJobs(true)

        // 设置是否自动启动，默认为true
        factory.isAutoStartup = false

        return factory
    }

    /**
     * 加载Quartz配置
     *
     * @return
     * @throws IOException
     */
    @Bean
    @Throws(IOException::class)
    open fun quartzProperties(): Properties {
        //使用Spring的PropertiesFactoryBean对属性配置文件进行管理
        val propertiesFactoryBean = PropertiesFactoryBean()
        //注意：quartz的配置文件从指定系统目录中获取，而不是从classpath中获取
        propertiesFactoryBean.setLocation(ClassPathResource("/quartz.properties"))
        //propertiesFactoryBean.setLocation(new FileSystemResource(propertiesPath));
        //重要：保证其初始化
        propertiesFactoryBean.afterPropertiesSet()
        return propertiesFactoryBean.getObject()
    }
}
```

#### 启动任务

```kotlin
@RequestMapping("/jobstore")
fun jobStore(): String {
    if (sd?.checkExists(JobKey.jobKey("jobstore","group1"))!!){
        sd?.start();
        return "";
    }
    //创建一个Trigger
    val trigger: Trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1")
        .withSchedule(
            CronScheduleBuilder.cronSchedule("0/10 * * * * ?")
                .withMisfireHandlingInstructionFireAndProceed()
        )
        .build()
    //创建一个job
    val job = JobBuilder.newJob(SchedulerJob::class.java)
        .withIdentity("jobstore", "group1")
        .build()
    //注册trigger并启动scheduler
    sd?.scheduleJob(job, trigger)
    sd?.start()
    return "jobStore演示，开启job，请查看相关数据库表数据<br>qrtz_cron_triggers<br>qrtz_job_details<br>qrtz_locks<br>qrtz_triggers"
}
```

#### 暂停Trigger

```kotlin
@RequestMapping("/pausetrigger")
fun pausetrigger(): String {
    //注册trigger并启动scheduler
    //sd?.shutdown(true)
    //sd?.standby()
    sd?.pauseTriggers(GroupMatcher.anyGroup())
    return "jobStore演示，暂停job，请查看相关数据库表数据<br>qrtz_paused_trigger_grps"
}
```

#### 恢复Trigger

```kotlin
@RequestMapping("/resumeTrigger")
fun resumeTrigger(): String {
    sd?.resumeTriggers(GroupMatcher.anyGroup())
    return "jobStore演示，恢复Trigger，请查看相关数据库表数据<br>qrtz_paused_trigger_grps"
}
```

## 集群

> 后续研究



## 插件

> 后续研究