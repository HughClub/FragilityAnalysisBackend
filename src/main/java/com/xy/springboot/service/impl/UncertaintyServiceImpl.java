package com.xy.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xy.springboot.model.entity.Uncertainty;
import com.xy.springboot.service.UncertaintyService;
import com.xy.springboot.mapper.UncertaintyMapper;
import org.springframework.stereotype.Service;

/**
* @author XY
* @description 针对表【uncertainty(Uncertainty参数配置)】的数据库操作Service实现
* @createDate 2024-06-30 17:49:43
*/
@Service
public class UncertaintyServiceImpl extends ServiceImpl<UncertaintyMapper, Uncertainty>
    implements UncertaintyService{

}




