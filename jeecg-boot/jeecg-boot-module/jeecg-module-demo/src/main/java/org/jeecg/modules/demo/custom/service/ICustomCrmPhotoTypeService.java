package org.jeecg.modules.demo.custom.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.demo.custom.entity.CustomCrmPhotoType;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.demo.custom.model.CustomCrmPhotoTypeTree;

import java.util.List;

/**
 * @Description: custom_crm_photo_type
 * @Author: jeecg-boot
 * @Date:   2025-06-03
 * @Version: V1.0
 */
public interface ICustomCrmPhotoTypeService extends IService<CustomCrmPhotoType> {

    List<CustomCrmPhotoTypeTree> listSub(String pid);

    Long countSubs(String pid);

    List<CustomCrmPhotoTypeTree> listTreeAll(Page<CustomCrmPhotoType> page, QueryWrapper<CustomCrmPhotoType> queryWrapper);
}
