package cn.lttc.quartzdemo.Job

import org.quartz.Job
import kotlin.Throws
import org.quartz.JobExecutionException
import org.quartz.JobExecutionContext
import java.text.SimpleDateFormat
import java.lang.InterruptedException
import java.util.*

/**
 * StatusJob1
 *
 * @author 王清洁
 * @create 2021-05-07
 * @modify
 */
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