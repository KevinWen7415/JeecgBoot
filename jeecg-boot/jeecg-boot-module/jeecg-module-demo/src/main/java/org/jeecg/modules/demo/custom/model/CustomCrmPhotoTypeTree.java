package org.jeecg.modules.demo.custom.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.modules.demo.custom.entity.CustomCrmPhotoType;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: 树形结构模型 - CustomCrmPhotoTypeTreeModel
 */
@Data
public class CustomCrmPhotoTypeTree implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键id */
    private String id;

    /** 照片类型名称 */
    private String photoTypeName;

    /** 类型级别 */
    private Integer typeLevel;

    /** 父级id */
    private String parentId;

    /** 是否叶子节点 */
    private Boolean isLeaf;

    /** 排序 */
    private Integer sortOrder;

    /** 状态 */
    private String status;

    /** 描述 */
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

    /** 子节点列表 */
    private List<CustomCrmPhotoTypeTree> children;

    private Boolean hasChildren;



}
