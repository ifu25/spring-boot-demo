package cn.lttc.quartzdemo.controller

import cn.lttc.quartzdemo.Job.*
import cn.lttc.quartzdemo.Job.DemoJob
import cn.lttc.quartzdemo.config.SchedulerHelper
import cn.lttc.quartzdemo.listeners.MyJobListener
import cn.lttc.quartzdemo.listeners.MySchedulerListener
import cn.lttc.quartzdemo.listeners.MyTriggerListener
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.naming.Context
import org.quartz.impl.matchers.GroupMatcher

import org.quartz.JobKey
import org.quartz.impl.matchers.KeyMatcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import kotlin.concurrent.thread


/**
 * 控制器
 *
 * @author 王清洁
 * @create 2021-05-08
 */
@RestController
class HomeController {

    @Qualifier("schedulerFactoryBean")
    @Autowired
    private var sd: Scheduler? = null

    //region ===========================演示实例======================================

    /**
     * 简单的演示实例
     */
    @RequestMapping("/demo")
    fun demo(): String {

        //创建一个scheduler
        val scheduler = SchedulerHelper.getScheduler("demo")

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
        scheduler?.scheduleJob(job, trigger)
        scheduler?.start()

        return "简单实例，请到控制台查看输出信息"
    }

    //endregion

    //region ===========================JobDataMap======================================

    @RequestMapping("/map1")
    fun map1(): String {

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

    @RequestMapping("/map2")
    fun map2(): String {

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

    //endregion

    //region ===========================状态和并发======================================

    /**
     * 并发演示，没有任何注解
     */
    @RequestMapping("/status1")
    fun status1(): String {

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
        return "并发演示(没有注解)，请到控制台查看输出信息"
    }

    /**
     * @DisallowConcurrentExecution注解演示
     */
    @RequestMapping("/status2")
    fun status2(): String {

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

        return "并发演示(@DisallowConcurrentExecution)，请到控制台查看输出信息"
    }

    /**
     * 状态演示，没有@PersistJobDataAfterExecution注解
     */
    @RequestMapping("/status3")
    fun status3(): String {

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
        return "状态演示(没有@PersistJobDataAfterExecution)，请到控制台查看输出信息"
    }

    /**
     * 状态演示，有@PersistJobDataAfterExecution注解
     */
    @RequestMapping("/status4")
    fun status4(): String {

        //创建一个scheduler
        val scheduler = SchedulerHelper.getScheduler("status4")

        //创建一个Trigger
        val trigger: Trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger1", "group1")
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1)
                    .withRepeatCount(3)
            ).build()

        //创建一个job
        val job = JobBuilder.newJob(StatusJob4::class.java)
            .usingJobData("count", 1)
            .withIdentity("myjob", "mygroup").build()

        //注册trigger并启动scheduler
        scheduler?.scheduleJob(job, trigger)
        scheduler?.start()
        return "状态演示(有@PersistJobDataAfterExecution)，请到控制台查看输出信息"
    }

    //endregion

    //region ===========================SimpleTrigger======================================

    /**
     * StartAt演示
     */
    @RequestMapping("/simple1")
    fun simaple1(@RequestParam d: String): String {

        //创建一个scheduler
        val scheduler = SchedulerHelper.getScheduler("simple1")
        var df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        var date = Date()
        date = df.parse(d)

        //创建一个Trigger
        val trigger: Trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger1", "group1")
            .withSchedule(SimpleScheduleBuilder.simpleSchedule())
            .startAt(date)
            .build()

        //创建一个job
        val job = JobBuilder.newJob(TriggerJob::class.java)
            .withIdentity("myjob", "mygroup").build()

        //注册trigger并启动scheduler
        scheduler?.scheduleJob(job, trigger)
        scheduler?.start()

        return "指定时间点执行，请到控制台查看输出信息"
    }

    /**
     * 间隔固定时间多次执行
     */
    @RequestMapping("/simple2")
    fun simaple2(@RequestParam d: String): String {

        //创建一个scheduler
        val scheduler = SchedulerHelper.getScheduler("simple2")
        var df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        var date = Date()
        date = df.parse(d)

        //创建一个Trigger
        val trigger: Trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger1", "group1")
            .withSchedule(
                SimpleScheduleBuilder
                    .simpleSchedule()
                    .withIntervalInSeconds(1)
                    .withRepeatCount(10)
            )
            .startAt(date)
            .build()

        //创建一个job
        val job = JobBuilder.newJob(TriggerJob::class.java)
            .withIdentity("myjob", "mygroup").build()

        //注册trigger并启动scheduler
        scheduler?.scheduleJob(job, trigger)
        scheduler?.start()

        return "指定时间点执行并间隔一定时间多次执行，请到控制台查看输出信息"
    }

    /**
     * 指定固定时间后执行
     */
    @RequestMapping("/simple3")
    fun simaple3(): String {

        //创建一个scheduler
        val scheduler = SchedulerHelper.getScheduler("simple3")

        val df: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

        println("begin time is " + df.format(Date()))

        //创建一个Trigger
        val trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger1", "group1")
            .startAt(DateBuilder.futureDate(5, DateBuilder.IntervalUnit.MINUTE))
            .build()

        //创建一个job
        val job = JobBuilder.newJob(TriggerJob::class.java)
            .withIdentity("myjob", "mygroup").build()

        //注册trigger并启动scheduler
        scheduler?.scheduleJob(job, trigger)
        scheduler?.start()
        return "五分钟后执行，请到控制台查看输出信息"
    }

    @RequestMapping("/simple4")
    fun simaple4(@RequestParam d: String): String {

        //创建一个scheduler
        val scheduler = SchedulerHelper.getScheduler("simple4")
        var df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        var date = Date()
        date = df.parse(d)

        //创建一个Trigger
        val trigger: Trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger1", "group1")
            .withSchedule(
                SimpleScheduleBuilder
                    .simpleSchedule()
                    .withIntervalInMinutes(1)
                    .repeatForever()
            )
            .endAt(date)
            .build()

        //创建一个job
        val job = JobBuilder.newJob(TriggerJob::class.java)
            .withIdentity("myjob", "mygroup").build()

        //注册trigger并启动scheduler
        scheduler?.scheduleJob(job, trigger)
        scheduler?.start()

        return "执行到指定时间，请到控制台查看输出信息"
    }

    //endregion

    //region ===========================CronTrigger======================================

    /**
     * Cron表达式演示
     */
    @RequestMapping("/cron")
    fun cron(): String {

        //创建一个scheduler
        val scheduler = SchedulerHelper.getScheduler("cron")

        //创建一个Trigger
        val trigger: Trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger1", "group1")
            .withSchedule(
                CronScheduleBuilder
                    .cronSchedule("0 0/2 8-17 * * ?")
            )
            .build()

        //创建一个job
        val job = JobBuilder.newJob(TriggerJob::class.java)
            .withIdentity("myjob", "mygroup").build()

        //注册trigger并启动scheduler
        scheduler?.scheduleJob(job, trigger)
        scheduler?.start()

        return "八点到十七点，每两分钟执行一次，请到控制台查看输出信息"
    }

    //endregion

    //region ===========================TriggerListener======================================

    /**
     * 不中断任务演示
     */
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
        scheduler?.scheduleJob(jobDetail, trigger)
        scheduler?.start()

        return "不中断任务的演示实例，输出结果请看控制台"
    }

    /**
     * 中断任务演示
     */
    @RequestMapping("/tls2")
    fun tls2(): String {

        //获取一个默认的scheduler
        val scheduler = SchedulerHelper.getScheduler("tls2")

        //获取一个Trigger
        var trigger = TriggerBuilder.newTrigger()
            .withIdentity("myTrigger", "group1")
            .usingJobData("isCancel", true)
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
        scheduler?.scheduleJob(jobDetail, trigger)
        scheduler?.start()

        return "中断任务的演示实例，输出结果请看控制台"
    }

    //endregion

    //region ===========================JobListener======================================

    /**
     * 不中断任务演示
     */
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
        scheduler?.scheduleJob(jobDetail, trigger)
        scheduler?.start()

        return "不中断任务的演示实例，输出结果请看控制台"
    }

    /**
     * 中断任务演示
     */
    @RequestMapping("/jls2")
    fun jls2(): String {

        //获取一个默认的scheduler
        val scheduler = SchedulerHelper.getScheduler("jls2")

        //获取一个Trigger
        var trigger = TriggerBuilder.newTrigger()
            .withIdentity("myTrigger", "group1")
            .usingJobData("isCancel", true)
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
        scheduler?.scheduleJob(jobDetail, trigger)
        scheduler?.start()

        return "中断任务的演示实例，输出结果请看控制台"
    }

    //endregion

    //region ===========================JobListener======================================

    /**
     * 添加一个任务
     */
    @RequestMapping("/sls1")
    fun sls1(): String {

        //获取一个调度器
        val scheduler = SchedulerHelper.getScheduler("sls1")

        //进行判断，防止重复添加Listener
        if (scheduler?.listenerManager?.schedulerListeners?.isEmpty() == true) {
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

        return "添加一个job，请看控制台输出信息"
    }

    /**
     * 删除一个Job
     */
    @RequestMapping("/sls2")
    fun sls2(): String {

        val scheduler = SchedulerHelper.getScheduler("sls1")
        scheduler?.deleteJob(JobKey.jobKey("slsJob", "groupSls"))

        return "删除一个job，请看控制台输出信息"
    }

    /**
     * 部署一个job
     */
    @RequestMapping("/sls3")
    fun sls3(): String {
        //获取一个调度器
        val scheduler = SchedulerHelper.getScheduler("sls3")

        //获取一个Job
        var job = JobBuilder.newJob(TriggerJob::class.java)
            .withIdentity("job", "group1")
            .build()

        //获取一个Trigger
        var trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger", "group1")
            .build()

        //注册监听接口
        if (scheduler?.listenerManager?.schedulerListeners?.isEmpty() == true) {
            scheduler?.listenerManager?.addSchedulerListener(MySchedulerListener())
        }

        scheduler?.scheduleJob(job, trigger)

        return "部署一个job，请看控制台输出信息"
    }

    /**
     * 卸载一个job
     */
    @RequestMapping("/sls4")
    fun sls4(): String {
        //获取一个调度器
        val scheduler = SchedulerHelper.getScheduler("sls3")

        scheduler?.unscheduleJob(TriggerKey.triggerKey("trigger", "group1"))

        return "卸载一个job，请看控制台输出信息"
    }

    /**
     * 启动调度器
     */
    @RequestMapping("/sls5")
    fun sls5(): String {
        //获取一个调度器
        val scheduler = SchedulerHelper.getScheduler("sls5")

        //获取一个Job
        var job = JobBuilder.newJob(TriggerJob::class.java)
            .withIdentity("job", "group1")
            .build()

        //获取一个Trigger
        var trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger", "group1")
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(2)
                    .withRepeatCount(3)
            )
            .build()

        //注册监听接口
        if (scheduler?.listenerManager?.schedulerListeners?.isEmpty() == true) {
            scheduler?.listenerManager?.addSchedulerListener(MySchedulerListener())
        }

        scheduler?.scheduleJob(job, trigger)
        scheduler?.start()

        return "启动一个job，并运行四次后自动停止，请看控制台输出信息"
    }

    /**
     * 通过TriggerKey暂停恢复Trigger
     */
    @RequestMapping("/sls6")
    fun sls6(): String {
        //获取一个调度器
        val scheduler = SchedulerHelper.getScheduler("sls6")

        //获取一个Job
        var job = JobBuilder.newJob(TriggerJob::class.java)
            .withIdentity("job", "group1")
            .build()

        //获取一个Trigger
        var trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger", "group1")
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(2)
                    .withRepeatCount(6)
            )
            .build()

        //注册监听接口
        if (scheduler?.listenerManager?.schedulerListeners?.isEmpty() == true) {
            scheduler?.listenerManager?.addSchedulerListener(MySchedulerListener())
        }

        scheduler?.scheduleJob(job, trigger)
        scheduler?.start()

        Thread.sleep(2000)
        scheduler?.pauseTrigger(TriggerKey.triggerKey("trigger", "group1"))
        Thread.sleep(2000)
        scheduler?.resumeTrigger(TriggerKey.triggerKey("trigger", "group1"))
        return "暂停和恢复Trigger，请看控制台输出信息"
    }

    /**
     * 暂停恢复Job
     */
    @RequestMapping("/sls7")
    fun sls7(): String {
        //获取一个调度器
        val scheduler = SchedulerHelper.getScheduler("sls7")

        //获取一个Job
        var job = JobBuilder.newJob(TriggerJob::class.java)
            .withIdentity("job", "group1")
            .build()

        //获取一个Trigger
        var trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger", "group1")
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(2)
                    .withRepeatCount(6)
            )
            .build()

        //注册监听接口
        if (scheduler?.listenerManager?.schedulerListeners?.isEmpty() == true) {
            scheduler?.listenerManager?.addSchedulerListener(MySchedulerListener())
        }

        scheduler?.scheduleJob(job, trigger)
        scheduler?.start()

        Thread.sleep(2000)
        scheduler?.pauseJob(JobKey.jobKey("job", "group1"))
        Thread.sleep(2000)
        scheduler?.resumeJob(JobKey.jobKey("job", "group1"))
        return "暂停和恢复Job，请看控制台输出信息"
    }


    /**
     * 关闭调度器
     */
    @RequestMapping("/sls8")
    fun sls8(): String {
        //获取一个调度器
        val scheduler = SchedulerHelper.getScheduler("sls8")

        //获取一个Job
        var job = JobBuilder.newJob(TriggerJob::class.java)
            .withIdentity("job", "group1")
            .build()

        //获取一个Trigger
        var trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger", "group1")
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(1)
                    .repeatForever()
            )
            .build()

        //注册监听接口
        if (scheduler?.listenerManager?.schedulerListeners?.isEmpty() == true) {
            scheduler?.listenerManager?.addSchedulerListener(MySchedulerListener())
        }

        scheduler?.scheduleJob(job, trigger)
        scheduler?.start()
        Thread.sleep(3000)
        scheduler?.shutdown()
        return "关闭调度器，请看控制台输出信息"
    }

    /**
     * 清空调度器
     */
    @RequestMapping("/sls9")
    fun sls9(): String {
        //获取一个调度器
        val scheduler = SchedulerHelper.getScheduler("sls9")

        //获取一个Job
        var job = JobBuilder.newJob(TriggerJob::class.java)
            .withIdentity("job", "group1")
            .build()

        //获取一个Trigger
        var trigger = TriggerBuilder.newTrigger()
            .withIdentity("trigger", "group1")
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(1)
                    .repeatForever()
            )
            .build()

        //注册监听接口
        if (scheduler?.listenerManager?.schedulerListeners?.isEmpty() == true) {
            scheduler?.listenerManager?.addSchedulerListener(MySchedulerListener())
        }

        scheduler?.scheduleJob(job, trigger)

        scheduler?.clear()

        return "清空调度器，请看控制台输出信息"
    }

    //endregion

    //region ===========================JobStore======================================

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

    @RequestMapping("/pausetrigger")
    fun pausetrigger(): String {
        sd?.pauseTriggers(GroupMatcher.anyGroup())
        Thread.sleep(20000)
        return "jobStore演示，暂停Trigger，请查看相关数据库表数据<br>qrtz_paused_trigger_grps"
    }

    @RequestMapping("/resumeTrigger")
    fun resumeTrigger(): String {
        sd?.resumeTriggers(GroupMatcher.anyGroup())
        return "jobStore演示，恢复Trigger，请查看相关数据库表数据<br>qrtz_paused_trigger_grps"
    }

    //endregion
}