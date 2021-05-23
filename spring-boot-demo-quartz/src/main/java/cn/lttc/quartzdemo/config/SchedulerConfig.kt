package cn.lttc.quartzdemo.config

import kotlin.Throws
import java.io.IOException
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.beans.factory.config.PropertiesFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.util.*
import javax.sql.DataSource

/**
 * SchedulerConfig
 *
 * @author 王清洁
 * @create 2021-05-14
 * @modify
 */
@Configuration
open class SchedulerConfig {
    @Bean
    @Throws(IOException::class)
    open fun schedulerFactoryBean(dataSource: DataSource?): SchedulerFactoryBean {
        val factory = SchedulerFactoryBean()
        factory.setDataSource(dataSource)

        // quartz参数
        factory.setQuartzProperties(quartzProperties())
        factory.setSchedulerName("MyScheduler")

        // 延时启动
        factory.setStartupDelay(1)
        factory.setApplicationContextSchedulerContextKey("applicationContextKey")

        // 可选，QuartzScheduler
        // 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        factory.setOverwriteExistingJobs(true)

        // 设置是否自动启动，默认为true
        factory.isAutoStartup = false

        return factory
    }

    /**
     * 加载Quartz配置
     *
     * @return
     * @throws IOException
     */
    @Bean
    @Throws(IOException::class)
    open fun quartzProperties(): Properties {
        //使用Spring的PropertiesFactoryBean对属性配置文件进行管理
        val propertiesFactoryBean = PropertiesFactoryBean()
        //注意：quartz的配置文件从指定系统目录中获取，而不是从classpath中获取
        propertiesFactoryBean.setLocation(ClassPathResource("/quartz.properties"))
        //propertiesFactoryBean.setLocation(new FileSystemResource(propertiesPath));
        //重要：保证其初始化
        propertiesFactoryBean.afterPropertiesSet()
        return propertiesFactoryBean.getObject()
    }
}