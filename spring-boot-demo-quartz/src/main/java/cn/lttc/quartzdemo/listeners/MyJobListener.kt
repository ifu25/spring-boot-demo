package cn.lttc.quartzdemo.listeners

import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.quartz.JobListener
import java.util.*

/**
 * MyJobListener
 *
 * @author 王清洁
 * @create 2021-05-10
 * @modify
 */
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