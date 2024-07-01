package com.xy.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.springboot.model.entity.Condition;
import com.xy.springboot.service.ConditionService;
import com.xy.springboot.mapper.ConditionMapper;
import org.springframework.stereotype.Service;

/**
* @author XY
* @description 针对表【condition(Condition参数配置)】的数据库操作Service实现
* @createDate 2024-07-01 21:59:18
*/
@Service
public class ConditionServiceImpl extends ServiceImpl<ConditionMapper, Condition>
    implements ConditionService{

}




