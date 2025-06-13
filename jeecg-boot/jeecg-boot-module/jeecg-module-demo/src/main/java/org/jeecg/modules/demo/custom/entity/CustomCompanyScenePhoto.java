package org.jeecg.modules.demo.custom.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.annotation.*;
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
 * @Description: custom_company_scene_photo
 * @Author: jeecg-boot
 * @Date:   2025-06-12
 * @Version: V1.0
 */
@Data
@TableName("custom_company_scene_photo")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description="custom_company_scene_photo")
public class CustomCompanyScenePhoto implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键id*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键id")
    private String id;
	/**公司现场id*/
	@Excel(name = "公司现场id", width = 15)
    @Schema(description = "公司现场id")
    private String companySceneId;
	/**照片类型名称*/
	@Excel(name = "照片类型名称", width = 15)
    @Schema(description = "照片类型名称")
    private String photoTypeName;
    /**照片类型id*/
    @Excel(name = "照片类型id", width = 15)
    @Schema(description = "照片类型id")
    private String photoTypeId;
	/**类型级别*/
	@Excel(name = "类型级别", width = 15)
    @Schema(description = "类型级别")
    private Integer typeLevel;
	/**父级id*/
	@Excel(name = "父级id", width = 15)
    @Schema(description = "父级id")
    private String parentId;
	/**是否叶子节点*/
	@Excel(name = "是否叶子节点", width = 15)
    @Schema(description = "是否叶子节点")
    private Boolean isLeaf;
	/**排序*/
	@Excel(name = "排序", width = 15)
    @Schema(description = "排序")
    private Integer sortOrder;
	/**文件路径*/
	@Excel(name = "文件路径", width = 15)
    @Schema(description = "文件路径")
    private String filePath;
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

    @TableField(exist = false)
    private Boolean hasChildren;

    @TableField(exist = false)
    private List<CustomCompanyScenePhoto> children;

    public Boolean getHasChildren() {
        return this.isLeaf == null || !this.isLeaf;
    }

}
