package cn.lttc.swaggerdemo.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

/**
 * 用户模型
 *
 * @author：谢  鑫
 * @create：2021-04-17
 */

@ApiModel(value = "用户实体", description = "表示用户对象信息")
class UserEntity {

    //用户ID
    @ApiModelProperty(value = "用户Id", required = true)
    var id: Int? = null

    //用户名
    @ApiModelProperty(value = "用户名", required = true, example = "张三")
    var userName: String? = null

    //性别
    @ApiModelProperty(value = "性别", required = false, example = "男")
    var sex: String? = null

    //年龄
    @ApiModelProperty(value = "年龄", required = true, dataType = "java.lang.Double")
    var age: Int? = null

    //地址
    @ApiModelProperty(value = "地址", required = false, dataType = "java.lang.String")
    var address: String? = null

    //联系方式
    @ApiModelProperty(value = "联系方式", name = "phone", hidden = false)
    var phoneNumber: String? = null

    //备注
    @ApiModelProperty(value = "备注", hidden = false)
    var note: String? = null
}