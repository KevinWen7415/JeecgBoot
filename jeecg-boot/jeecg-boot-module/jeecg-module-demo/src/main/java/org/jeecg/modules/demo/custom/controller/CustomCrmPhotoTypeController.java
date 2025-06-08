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

import cn.hutool.core.util.StrUtil;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.custom.entity.CustomCrmPhotoType;
import org.jeecg.modules.demo.custom.model.CustomCrmPhotoTypeTree;
import org.jeecg.modules.demo.custom.service.ICustomCrmPhotoTypeService;

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
 * @Description: custom_crm_photo_type
 * @Author: jeecg-boot
 * @Date:   2025-06-03
 * @Version: V1.0
 */
@Tag(name="custom_crm_photo_type")
@RestController
@RequestMapping("/custom/customCrmPhotoType")
@Slf4j
public class CustomCrmPhotoTypeController extends JeecgController<CustomCrmPhotoType, ICustomCrmPhotoTypeService> {
	@Autowired
	private ICustomCrmPhotoTypeService customCrmPhotoTypeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param customCrmPhotoType
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "custom_crm_photo_type-分页列表查询")
	@Operation(summary="custom_crm_photo_type-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CustomCrmPhotoType>> queryPageList(CustomCrmPhotoType customCrmPhotoType,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		customCrmPhotoType.setTypeLevel(0);
        QueryWrapper<CustomCrmPhotoType> queryWrapper = QueryGenerator.initQueryWrapper(customCrmPhotoType, req.getParameterMap());
		Page<CustomCrmPhotoType> page = new Page<CustomCrmPhotoType>(pageNo, pageSize);
		IPage<CustomCrmPhotoType> pageList = customCrmPhotoTypeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}


	 @Operation(summary="custom_crm_photo_type-分页列表查询")
	 @GetMapping(value = "/listTreeAll")
	 public Result<List<CustomCrmPhotoTypeTree>> listTreeAll(CustomCrmPhotoType customCrmPhotoType,
															@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
															@RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
															HttpServletRequest req) {
		 customCrmPhotoType.setTypeLevel(0);
		 QueryWrapper<CustomCrmPhotoType> queryWrapper = QueryGenerator.initQueryWrapper(customCrmPhotoType, req.getParameterMap());
		 Page<CustomCrmPhotoType> page = new Page<CustomCrmPhotoType>(pageNo, pageSize);
		 List<CustomCrmPhotoTypeTree> treeAll = customCrmPhotoTypeService.listTreeAll(page, queryWrapper);



		 return Result.OK(treeAll);
	 }


	 @Operation(summary="custom_crm_photo_type-异步树加载查询")
	 @GetMapping(value = "/listSub")
	 public Result<List<CustomCrmPhotoTypeTree>> listSub(@RequestParam(name="pid") String pid){
		if (StrUtil.isEmpty(pid)){
			return Result.error("参数错误");
		}
		List<CustomCrmPhotoTypeTree> list = customCrmPhotoTypeService.listSub(pid);
		return Result.OK(list);
	 }

	
	/**
	 *   添加
	 *
	 * @param customCrmPhotoType
	 * @return
	 */
	@AutoLog(value = "custom_crm_photo_type-添加")
	@Operation(summary="custom_crm_photo_type-添加")
	@RequiresPermissions("custom:custom_crm_photo_type:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody CustomCrmPhotoType customCrmPhotoType) {
		customCrmPhotoTypeService.save(customCrmPhotoType);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param customCrmPhotoType
	 * @return
	 */
	@AutoLog(value = "custom_crm_photo_type-编辑")
	@Operation(summary="custom_crm_photo_type-编辑")
	@RequiresPermissions("custom:custom_crm_photo_type:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody CustomCrmPhotoType customCrmPhotoType) {
		customCrmPhotoTypeService.updateById(customCrmPhotoType);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "custom_crm_photo_type-通过id删除")
	@Operation(summary="custom_crm_photo_type-通过id删除")
	@RequiresPermissions("custom:custom_crm_photo_type:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		customCrmPhotoTypeService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "custom_crm_photo_type-批量删除")
	@Operation(summary="custom_crm_photo_type-批量删除")
	@RequiresPermissions("custom:custom_crm_photo_type:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.customCrmPhotoTypeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "custom_crm_photo_type-通过id查询")
	@Operation(summary="custom_crm_photo_type-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CustomCrmPhotoType> queryById(@RequestParam(name="id",required=true) String id) {
		CustomCrmPhotoType customCrmPhotoType = customCrmPhotoTypeService.getById(id);
		if(customCrmPhotoType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(customCrmPhotoType);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param customCrmPhotoType
    */
    @RequiresPermissions("custom:custom_crm_photo_type:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CustomCrmPhotoType customCrmPhotoType) {
        return super.exportXls(request, customCrmPhotoType, CustomCrmPhotoType.class, "custom_crm_photo_type");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("custom:custom_crm_photo_type:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, CustomCrmPhotoType.class);
    }

}
