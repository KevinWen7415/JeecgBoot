package org.jeecg.modules.demo.custom.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: custom_company_scene
 * @Author: jeecg-boot
 * @Date:   2025-06-09
 * @Version: V1.0
 */
@Data
@TableName("custom_company_scene")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="custom_company_scene")
public class CustomCompanyScene implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键id")
    private String id;
	/**公司名称*/
	@Excel(name = "公司名称", width = 15)
    @Schema(description = "公司名称")
    private String companyName;
	/**记录生成时间*/
	@Excel(name = "记录生成时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "记录生成时间")
    private Date generationTime;
	/**上次上传时间*/
	@Excel(name = "上次上传时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "上次上传时间")
    private Date lastUploadTime;
	/**上次上传状态*/
	@Excel(name = "上次上传状态", width = 15)
    @Schema(description = "上次上传状态")
    private String lastUploadStatus;
	/**照片数量*/
	@Excel(name = "照片数量", width = 15)
    @Schema(description = "照片数量")
    private Integer photoCount;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @Schema(description = "状态")
    private String status;
	/**描述*/
	@Excel(name = "描述", width = 15)
    @Schema(description = "描述")
    private String description;
	/**创建人*/
    @Schema(description = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;
	/**更新人*/
    @Schema(description = "更新人")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;
	/**租户ID*/
	@Excel(name = "租户ID", width = 15)
    @Schema(description = "租户ID")
    private Integer tenantId;
}
