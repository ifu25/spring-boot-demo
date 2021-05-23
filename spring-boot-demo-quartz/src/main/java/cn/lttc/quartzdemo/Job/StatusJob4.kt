package cn.lttc.quartzdemo.Job

import org.quartz.*
import kotlin.Throws
import java.text.SimpleDateFormat
import java.lang.InterruptedException
import java.util.*

/**
 * StatusJob4
 *
 * @author 王清洁
 * @create 2021-05-07
 * @modify
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
class StatusJob4 : Job {
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