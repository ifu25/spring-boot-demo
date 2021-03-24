package cn.lttc.shirodemo.mapper

import cn.lttc.shirodemo.entity.UserEntity
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.springframework.stereotype.Repository

/****************************************
 * 用户 Mapper
 * 作者：XingGang
 * 日期：2021-03-20
 * 网址：https://weiku.co
 * 邮箱：xinggang.china@gmail.com
 ****************************************/
@Repository
interface UserMapper : BaseMapper<UserEntity> {
}