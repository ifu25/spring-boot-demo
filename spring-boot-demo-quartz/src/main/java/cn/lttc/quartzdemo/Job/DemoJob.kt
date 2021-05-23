package cn.lttc.quartzdemo.Job

import org.quartz.Job
import kotlin.Throws
import org.quartz.JobExecutionException
import org.quartz.JobExecutionContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * 演示实例2Job类
 *
 * @author 王清洁
 * @create 2021-05-05
 */
internal class DemoJob : Job {

    /**
     * 重写方法
     */
    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext) {
        var df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        println("当前时间是:" + df.format(Date()))
    }
}