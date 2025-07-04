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
import org.jeecg.modules.demo.custom.entity.CustomCompanyScenePhoto;
import org.jeecg.modules.demo.custom.service.ICustomCompanyScenePhotoService;

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
 * @Description: custom_company_scene_photo
 * @Author: jeecg-boot
 * @Date:   2025-06-12
 * @Version: V1.0
 */
@Tag(name="custom_company_scene_photo")
@RestController
@RequestMapping("/custom/customCompanyScenePhoto")
@Slf4j
public class CustomCompanyScenePhotoController extends JeecgController<CustomCompanyScenePhoto, ICustomCompanyScenePhotoService> {
	@Autowired
	private ICustomCompanyScenePhotoService customCompanyScenePhotoService;
	
	/**
	 * 分页列表查询
	 *
	 * @param customCompanyScenePhoto
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "custom_company_scene_photo-分页列表查询")
	@Operation(summary="custom_company_scene_photo-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CustomCompanyScenePhoto>> queryPageList(CustomCompanyScenePhoto customCompanyScenePhoto,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<CustomCompanyScenePhoto> queryWrapper = QueryGenerator.initQueryWrapper(customCompanyScenePhoto, req.getParameterMap());
		Page<CustomCompanyScenePhoto> page = new Page<CustomCompanyScenePhoto>(pageNo, pageSize);
		IPage<CustomCompanyScenePhoto> pageList = customCompanyScenePhotoService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param customCompanyScenePhoto
	 * @return
	 */
	@AutoLog(value = "custom_company_scene_photo-添加")
	@Operation(summary="custom_company_scene_photo-添加")
	@RequiresPermissions("custom:custom_company_scene_photo:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CustomCompanyScenePhoto customCompanyScenePhoto) {
		customCompanyScenePhotoService.save(customCompanyScenePhoto);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param customCompanyScenePhoto
	 * @return
	 */
	@AutoLog(value = "custom_company_scene_photo-编辑")
	@Operation(summary="custom_company_scene_photo-编辑")
	@RequiresPermissions("custom:custom_company_scene_photo:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CustomCompanyScenePhoto customCompanyScenePhoto) {
		customCompanyScenePhotoService.updateById(customCompanyScenePhoto);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "custom_company_scene_photo-通过id删除")
	@Operation(summary="custom_company_scene_photo-通过id删除")
	@RequiresPermissions("custom:custom_company_scene_photo:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		customCompanyScenePhotoService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "custom_company_scene_photo-批量删除")
	@Operation(summary="custom_company_scene_photo-批量删除")
	@RequiresPermissions("custom:custom_company_scene_photo:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.customCompanyScenePhotoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "custom_company_scene_photo-通过id查询")
	@Operation(summary="custom_company_scene_photo-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CustomCompanyScenePhoto> queryById(@RequestParam(name="id",required=true) String id) {
		CustomCompanyScenePhoto customCompanyScenePhoto = customCompanyScenePhotoService.getById(id);
		if(customCompanyScenePhoto==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(customCompanyScenePhoto);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param customCompanyScenePhoto
    */
    @RequiresPermissions("custom:custom_company_scene_photo:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CustomCompanyScenePhoto customCompanyScenePhoto) {
        return super.exportXls(request, customCompanyScenePhoto, CustomCompanyScenePhoto.class, "custom_company_scene_photo");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("custom:custom_company_scene_photo:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CustomCompanyScenePhoto.class);
    }

}
