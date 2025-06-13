package org.jeecg.modules.demo.custom.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.demo.custom.entity.CustomCrmPhotoType;
import org.jeecg.modules.demo.custom.mapper.CustomCrmPhotoTypeMapper;
import org.jeecg.modules.demo.custom.model.CustomCrmPhotoTypeTree;
import org.jeecg.modules.demo.custom.service.ICustomCrmPhotoTypeService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.io.Serializable;
import java.util.*;

/**
 * @Description: custom_crm_photo_type
 * @Author: jeecg-boot
 * @Date:   2025-06-03
 * @Version: V1.0
 */
@Service
public class CustomCrmPhotoTypeServiceImpl extends ServiceImpl<CustomCrmPhotoTypeMapper, CustomCrmPhotoType> implements ICustomCrmPhotoTypeService {

    @Override
    public boolean save(CustomCrmPhotoType entity) {
        //新增默认是叶子节点
        entity.setIsLeaf(true);
        String parentId = entity.getParentId();
        if (StrUtil.isNotEmpty(parentId)){
            CustomCrmPhotoType parent = getById(parentId);
            if(Objects.nonNull(parent)){
                parent.setIsLeaf(false);
                //当前节点级别+1
                entity.setTypeLevel(parent.getTypeLevel()+1);
                //修改父级叶子节点类型
                baseMapper.updateById(parent);
            }
        }
        return super.save(entity);
    }

    @Override
    public boolean updateById(CustomCrmPhotoType entity) {
        //处理父级变动
        String id = entity.getId();
        CustomCrmPhotoType currentData = getById(id);
        //父级发生改变
        if (!Objects.equals(currentData.getParentId(), entity.getParentId())){
            //原父级类型处理
            String orgParentId = currentData.getParentId();
            if (StrUtil.isNotEmpty(orgParentId)){
                CustomCrmPhotoType orgParent = getById(orgParentId);
                if (Objects.nonNull(orgParent)){
                    List<CustomCrmPhotoTypeTree> orgAllBros = listSub(orgParentId);
                    if (CollUtil.isNotEmpty(orgAllBros)){
                        List<CustomCrmPhotoTypeTree> list = orgAllBros.stream().filter(bro -> !Objects.equals(bro.getId(), id)).toList();
                        if (CollUtil.isEmpty(list)){
                            orgParent.setIsLeaf(true);
                            baseMapper.updateById(orgParent);
                        }
                    }
                }
            }
            String newParentId = entity.getParentId();
            if (StrUtil.isNotEmpty(newParentId)){
                CustomCrmPhotoType newParent = getById(newParentId);
                if (Objects.nonNull(newParent)){
                    //父级级别变成新父级的级别+1
                    entity.setTypeLevel(newParent.getTypeLevel() + 1);
                    if (Boolean.TRUE.equals(newParent.getIsLeaf())){
                        newParent.setIsLeaf(false);
                        baseMapper.updateById(newParent);
                    }
                }
            }
        }
        return super.updateById(entity);
    }

    @Override
    public List<CustomCrmPhotoTypeTree> listSub(String pid) {
        LambdaQueryWrapper<CustomCrmPhotoType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CustomCrmPhotoType::getParentId, pid);
        queryWrapper.orderBy(true,true,CustomCrmPhotoType::getSortOrder);
        List<CustomCrmPhotoType> list = baseMapper.selectList(queryWrapper);
        List<CustomCrmPhotoTypeTree> typeTrees = BeanUtil.copyToList(list, CustomCrmPhotoTypeTree.class);
        return typeTrees;
    }

    @Override
    public Long countSubs(String pid) {
        if (StrUtil.isEmpty(pid)){
            return 0L;
        }
        LambdaQueryWrapper<CustomCrmPhotoType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CustomCrmPhotoType::getParentId, pid);
        return baseMapper.selectCount(queryWrapper);
    }

    @Override
    public List<CustomCrmPhotoTypeTree> listTreeAll(Page<CustomCrmPhotoType> page, QueryWrapper<CustomCrmPhotoType> queryWrapper) {
        page.setSize(Integer.MAX_VALUE);
        queryWrapper.eq("type_level",0);
        Page<CustomCrmPhotoType> typePage = page(page, queryWrapper);
        List<CustomCrmPhotoType> records = typePage.getRecords();
        List<CustomCrmPhotoTypeTree> result = records.stream().filter(Objects::nonNull).map(customCrmPhotoType -> {
            CustomCrmPhotoTypeTree crmPhotoTypeTree = BeanUtil.copyProperties(customCrmPhotoType, CustomCrmPhotoTypeTree.class);
            if (!crmPhotoTypeTree.getIsLeaf()) {
                findAllChildren(crmPhotoTypeTree);
            }
            return crmPhotoTypeTree;
        }).toList();

        return result;
    }

    @Override
    public List<CustomCrmPhotoTypeTree> listTreeEnabled() {
        List<CustomCrmPhotoType> topEnabledTypes = list(new LambdaQueryWrapper<CustomCrmPhotoType>().eq(CustomCrmPhotoType::getTypeLevel, 0).eq(CustomCrmPhotoType::getStatus, 1));
        List<CustomCrmPhotoTypeTree> topEnabledTypeTrees = BeanUtil.copyToList(topEnabledTypes, CustomCrmPhotoTypeTree.class);
        List<CustomCrmPhotoTypeTree> results = topEnabledTypeTrees.stream().filter(Objects::nonNull).map(customCrmPhotoTypeTree -> {
            if (!customCrmPhotoTypeTree.getIsLeaf()) {
                findAllEnabledChildren(customCrmPhotoTypeTree);
            }
            return customCrmPhotoTypeTree;
        }).toList();
        return results;
    }

    private CustomCrmPhotoTypeTree findAllChildren(CustomCrmPhotoTypeTree crmPhotoTypeTree) {
        if (Objects.isNull(crmPhotoTypeTree)){
            return crmPhotoTypeTree;
        }
        if (!Boolean.TRUE.equals(crmPhotoTypeTree.getIsLeaf())){
            List<CustomCrmPhotoTypeTree> children = listSub(crmPhotoTypeTree.getId());
            if (CollUtil.isNotEmpty(children)){
                for (CustomCrmPhotoTypeTree child : children) {
                    findAllChildren(child);
                }
                crmPhotoTypeTree.setHasChildren(true);
                crmPhotoTypeTree.setChildren(children);
            }
        }
        return crmPhotoTypeTree;
    }


    private CustomCrmPhotoTypeTree findAllEnabledChildren(CustomCrmPhotoTypeTree crmPhotoTypeTree) {
        if (Objects.isNull(crmPhotoTypeTree)){
            return crmPhotoTypeTree;
        }
        if (!Boolean.TRUE.equals(crmPhotoTypeTree.getIsLeaf())){
            List<CustomCrmPhotoTypeTree> children = listSub(crmPhotoTypeTree.getId());
            //筛选出可用的子级
            children = Optional.ofNullable(children).map(lis->{
                return lis.stream().filter(child -> Objects.equals(child.getStatus(), "1")).toList();
            }).orElse(new ArrayList<>());
            //保存子列表
            if (CollUtil.isNotEmpty(children)){
                for (CustomCrmPhotoTypeTree child : children) {
                    findAllEnabledChildren(child);
                }
                crmPhotoTypeTree.setHasChildren(true);
                crmPhotoTypeTree.setChildren(children);
            }
        }
        return crmPhotoTypeTree;
    }


    @Override
    public boolean removeById(Serializable id) {
        CustomCrmPhotoType photoType = getById(id);
        String parentId = photoType.getParentId();
        if (StrUtil.isNotEmpty(parentId)){
            CustomCrmPhotoType parent = getById(parentId);
            if (Objects.nonNull(parent)){
                Long subCounts = countSubs(parentId);
                if (NumberUtil.equals(subCounts, Long.valueOf(0L))) {
                    parent.setIsLeaf(true);
                }
                updateById(parent);
            }
        }
        return super.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<?> list) {
        if (CollUtil.isNotEmpty(list)){
            for (Object o : list) {
                removeById((Serializable) o);
            }
        }
        return true;
    }

}
