package com.xy.springboot.service;

import com.xy.springboot.model.entity.Uncertainty;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author XY
* @description 针对表【uncertainty(Uncertainty参数配置)】的数据库操作Service
* @createDate 2024-06-30 17:49:43
*/
public interface UncertaintyService extends IService<Uncertainty> {
    /**
     * 插入或更新
     * @param uncertainty 不确定性
     * @return 插入或更新结果
     */
    int createOrUpdateUncertainty(Uncertainty uncertainty);
}
