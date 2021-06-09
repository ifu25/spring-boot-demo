package cn.lttc.quartzdemo.listeners

import org.quartz.JobExecutionContext
import org.quartz.Trigger
import org.quartz.TriggerListener
import java.util.*

/**
 * 文档描述
 *
 * @author 王清洁
 * @create 2021-05-10
 * @modify
 */
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