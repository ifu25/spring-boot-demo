package cn.lttc.quartzdemo.listeners

import org.quartz.*
import java.util.*

/**
 * MySchedulerListener
 *
 * @author 王清洁
 * @create 2021-05-11
 * @modify
 */
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