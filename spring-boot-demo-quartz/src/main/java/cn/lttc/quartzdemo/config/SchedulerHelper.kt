package cn.lttc.quartzdemo.config

import org.quartz.Scheduler
import org.quartz.impl.StdSchedulerFactory
import org.quartz.SchedulerException
import java.util.*

/**
 * SchedulerHelper
 *
 * @author 王清洁
 * @create 2021-05-11
 * @modify
 */
object SchedulerHelper {

    /**
     * 获取一个scheduler
     */
    fun getScheduler(strName: String): Scheduler? {
        try {
            val sf = StdSchedulerFactory()
            val props = Properties()
            props["org.quartz.scheduler.instanceName"] = strName
            props["org.quartz.threadPool.threadCount"] = "10"

            sf.initialize(props)
            return sf.scheduler
        } catch (e: SchedulerException) {
            e.printStackTrace()
        }
        return null
    }
}