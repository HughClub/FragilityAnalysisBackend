package com.xy.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.springboot.model.entity.Condition;
import com.xy.springboot.service.ConditionService;
import com.xy.springboot.mapper.ConditionMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
* @author XY
* @description 针对表【condition(Condition参数配置)】的数据库操作Service实现
* @createDate 2024-07-01 21:59:18
*/
@Service
public class ConditionServiceImpl extends ServiceImpl<ConditionMapper, Condition>
    implements ConditionService{
    @Override
    public int createOrUpdateCondition(Condition condition){
        QueryWrapper<Condition> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("taskId", condition.getTaskId());
        Condition conditionExist = this.getOne(queryWrapper);
        if (Objects.isNull(conditionExist)) {
            return this.baseMapper.insert(condition);
        } else {
            condition.setId(conditionExist.getId());
            return this.baseMapper.updateById(condition);
        }

    }
}




