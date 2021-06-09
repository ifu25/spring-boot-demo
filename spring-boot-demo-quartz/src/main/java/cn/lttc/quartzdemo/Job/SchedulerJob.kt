package cn.lttc.quartzdemo.Job

import org.quartz.Job
import org.quartz.JobExecutionContext

/**
 * 文档描述
 *
 * @author 王清洁
 * @create 2021-05-14
 * @modify
 */
class SchedulerJob:Job {
    /**
     * JobStore演示任务Job
     */
    override fun execute(context: JobExecutionContext?) {
        // 执行任务
        println("-------执行吃饭任务----------")
        println("吃饭中。。。。。。。。。。。")
        Thread.sleep(3000)
        println("吃饱了。。。。。。。。。")
        println("吃饱了,那就睡觉吧。。。。。。。。。")
    }
}