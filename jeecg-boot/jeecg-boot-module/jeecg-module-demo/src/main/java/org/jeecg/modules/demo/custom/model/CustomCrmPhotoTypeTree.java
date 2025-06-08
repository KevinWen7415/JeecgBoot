package org.jeecg.modules.demo.custom.model;

import lombok.Data;
import org.jeecg.modules.demo.custom.entity.CustomCrmPhotoType;

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

    /** 创建人 */
    private String createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新人 */
    private String updateBy;

    /** 更新时间 */
    private Date updateTime;

    /** 租户ID */
    private Integer tenantId;

    /** 子节点列表 */
    private List<CustomCrmPhotoTypeTree> children;

    private Boolean hasChildren;



}
