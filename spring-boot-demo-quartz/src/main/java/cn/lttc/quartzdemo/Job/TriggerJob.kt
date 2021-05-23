package cn.lttc.quartzdemo.Job

import org.quartz.Job
import kotlin.Throws
import org.quartz.JobExecutionException
import org.quartz.JobExecutionContext
import org.quartz.JobDataMap
import java.text.SimpleDateFormat
import java.util.*

/**
 * TriggerJob
 *
 * @author 王清洁
 * @create 2021-05-07
 * @modify
 */
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