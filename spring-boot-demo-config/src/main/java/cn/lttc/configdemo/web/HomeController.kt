package cn.lttc.configdemo.web

import cn.lttc.configdemo.component.PersonValue
import cn.lttc.configdemo.component.SourceValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired


/**
 * 首页控制器
 *
 * @author：谢  鑫
 * @create：2021-04-24
 */
@RestController
class HomeController {

    @GetMapping("/")
    fun index(): Any {
        return "Hello ConfigDemo is Success"
    }

    //region =========== 配置使用方式一：@Value ===========

    /**
     * 配置文件中的简单类型：名称
     */
    @Value("\${person.name}")
    val name: String? = null

    /**
     * 支持SpEL表达式， 格式为 #{...}，大括号内为SpEL表达式
     */
    @Value("#{11*4/2}")
    val age: String? = null

    /**
     * 配置文件中的简单类型：出生日期
     */
    @Value("\${person.birth}")
    val birth: LocalDate? = null

    /**
     * 配置文件中的list类型
     */
    @Value("\${person.lists}")
    val lists: List<String>? = null

    /**
     * 不支持复杂类型
     */
    //@Value("\${person.maps}")
    //val maps: Map<String, String>? = null

    /**
     * 演示：@Value注解读取配置文件中的配置项
     */
    @GetMapping("/readByValue")
    fun readByValue(): Any {
        val sb = StringBuilder()
        sb.append("@Value形式获取配置参数：")
        sb.append(
            java.lang.String.format(
                "name=%s, age=%s, birth=%s, lists=%s ",
                name, age, birth, lists
            )
        )
        return sb.toString()
    }

    //endregion

    //region =========== 配置使用方式二：配置类 @ConfigurationProperties ===========

    @Autowired
    lateinit var personValue: PersonValue

    /**
     * 演示：@ConfigurationProperties注解读取配置文件中的配置项
     */
    @GetMapping("/readByClass")
    fun readByClass(): Any {
        val sb = StringBuilder()
        sb.append("@ConfigurationProperties形式获取配置参数：\n")
        sb.append(
            java.lang.String.format(
                "name=%s, age=%s, birth=%s, lists=%s, maps=%s ",
                personValue.name,
                personValue.age,
                personValue.birth,
                personValue.lists,
                personValue.maps
            )
        )

        return sb.toString()
    }

    //endregion

    //region =========== 配置使用方式三：@PropertySource + @Value ===========

    /**
     * 用户名
     */
    @Value("\${com.example.user-name}")
    val userName: String? = null

    /**
     * 密码
     */
    @Value("\${com.example.pass-word}")
    val passWord: String? = null

    /**
     * 演示："@PropertySource + @Value注解读取指定配置文件中的配置项
     */
    @GetMapping("/readBySourceValue")
    fun readBySourceValue(): Any {
        val sb = StringBuilder()
        sb.append("【@PropertySource+@Value】的形式获取指定配置文件的配置参数：")
        sb.append(
            java.lang.String.format(
                "username=%s, password=%s ",
                userName, passWord
            )
        )
        return sb.toString()
    }

    //endregion

    //region =========== 配置使用方式四：@PropertySource + @ConfigurationProperties ===========

    @Autowired
    lateinit var sourceValue: SourceValue

    /**
     * 演示："@PropertySource + @ConfigurationProperties注解读取指定配置文件中的配置项
     */
    @GetMapping("/readBySourceClass")
    fun readBySourceClass(): Any {
        val sb = StringBuilder()
        sb.append("【@PropertySource+@ConfigurationProperties】的形式获取指定配置文件的配置参数：\n")
        sb.append(
            java.lang.String.format(
                "userName=%s, passWord=%s ",
                sourceValue.userName,
                sourceValue.passWord
            )
        )

        return sb.toString()
    }

    //endregion
}