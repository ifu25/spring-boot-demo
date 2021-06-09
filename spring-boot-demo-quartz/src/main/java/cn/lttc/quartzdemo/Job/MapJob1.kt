package cn.lttc.quartzdemo.Job

import org.quartz.*
import kotlin.Throws

/**
 * JobDataMap演示类一
 *
 * @author 王清洁
 * @create 2021-05-07
 */
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