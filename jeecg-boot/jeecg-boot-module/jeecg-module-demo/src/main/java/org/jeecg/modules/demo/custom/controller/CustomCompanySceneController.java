package org.jeecg.modules.demo.custom.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.custom.entity.CustomCompanyScene;
import org.jeecg.modules.demo.custom.service.ICustomCompanySceneService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: custom_company_scene
 * @Author: jeecg-boot
 * @Date:   2025-06-09
 * @Version: V1.0
 */
@Tag(name="custom_company_scene")
@RestController
@RequestMapping("/custom/customCompanyScene")
@Slf4j
public class CustomCompanySceneController extends JeecgController<CustomCompanyScene, ICustomCompanySceneService> {
	@Autowired
	private ICustomCompanySceneService customCompanySceneService;
	
	/**
	 * 分页列表查询
	 *
	 * @param customCompanyScene
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "custom_company_scene-分页列表查询")
	@Operation(summary="custom_company_scene-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CustomCompanyScene>> queryPageList(CustomCompanyScene customCompanyScene,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<CustomCompanyScene> queryWrapper = QueryGenerator.initQueryWrapper(customCompanyScene, req.getParameterMap());
		Page<CustomCompanyScene> page = new Page<CustomCompanyScene>(pageNo, pageSize);
		IPage<CustomCompanyScene> pageList = customCompanySceneService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param customCompanyScene
	 * @return
	 */
	@AutoLog(value = "custom_company_scene-添加")
	@Operation(summary="custom_company_scene-添加")
	@RequiresPermissions("custom:custom_company_scene:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CustomCompanyScene customCompanyScene) {
		customCompanySceneService.save(customCompanyScene);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param customCompanyScene
	 * @return
	 */
	@AutoLog(value = "custom_company_scene-编辑")
	@Operation(summary="custom_company_scene-编辑")
	@RequiresPermissions("custom:custom_company_scene:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CustomCompanyScene customCompanyScene) {
		customCompanySceneService.updateById(customCompanyScene);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "custom_company_scene-通过id删除")
	@Operation(summary="custom_company_scene-通过id删除")
	@RequiresPermissions("custom:custom_company_scene:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		customCompanySceneService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "custom_company_scene-批量删除")
	@Operation(summary="custom_company_scene-批量删除")
	@RequiresPermissions("custom:custom_company_scene:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.customCompanySceneService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "custom_company_scene-通过id查询")
	@Operation(summary="custom_company_scene-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CustomCompanyScene> queryById(@RequestParam(name="id",required=true) String id) {
		CustomCompanyScene customCompanyScene = customCompanySceneService.getById(id);
		if(customCompanyScene==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(customCompanyScene);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param customCompanyScene
    */
    @RequiresPermissions("custom:custom_company_scene:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CustomCompanyScene customCompanyScene) {
        return super.exportXls(request, customCompanyScene, CustomCompanyScene.class, "custom_company_scene");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("custom:custom_company_scene:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CustomCompanyScene.class);
    }

}
