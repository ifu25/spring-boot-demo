package cn.lttc.mybatisplusdemo.service.impl;

import cn.lttc.mybatisplusdemo.entity.Demo;
import cn.lttc.mybatisplusdemo.mapper.DemoMapper;
import cn.lttc.mybatisplusdemo.service.DemoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yangshuangling
 * @since 2021-04-21
 */
@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper, Demo> implements DemoService {

}
