package cn.lttc.quartzdemo.Job

import org.quartz.*
import kotlin.Throws

/**
 * JobMap演示类二
 *
 * @author 王清洁
 * @create 2021-05-07
 * @modify
 */
class MapJob3 : Job {
    var str: String? = null
    var fl = 0f

    /**
     * 重写Job接口方法
     *
     * @param context 传递的上下文内容
     * @throws JobExecutionException 异常信息
     */
    @Throws(JobExecutionException::class)
    override fun execute(context: JobExecutionContext) {

        //获取JobDetail的key，包括名称和组信息
        val key = context.jobDetail.key

        //获取合并的数据信息
        val map = context.mergedJobDataMap

        //输出到日志窗口
        println("job name is " + key.name + " str is " + str + " float is " + fl)
    }

    /**
     * str的set方法
     * @param str 传递的string值
     */
    @JvmName("setStr1")
    fun setStr(str: String?) {
        this.str = str
    }

    /**
     * fl的set方法
     * @param fl 传递float值
     */
    @JvmName("setFl1")
    fun setFl(fl: Float) {
        this.fl = fl
    }
}