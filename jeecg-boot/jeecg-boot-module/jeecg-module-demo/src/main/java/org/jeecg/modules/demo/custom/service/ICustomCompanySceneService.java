package org.jeecg.modules.demo.custom.service;

import org.jeecg.modules.demo.custom.entity.CustomCompanyScene;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: custom_company_scene
 * @Author: jeecg-boot
 * @Date:   2025-06-09
 * @Version: V1.0
 */
public interface ICustomCompanySceneService extends IService<CustomCompanyScene> {

    Boolean uploadPhoto(CustomCompanyScene customCompanyScene);
}
