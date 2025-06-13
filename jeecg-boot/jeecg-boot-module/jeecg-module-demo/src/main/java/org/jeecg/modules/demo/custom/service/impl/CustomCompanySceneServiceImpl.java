package org.jeecg.modules.demo.custom.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.demo.custom.entity.CustomCompanyScene;
import org.jeecg.modules.demo.custom.entity.CustomCompanyScenePhoto;
import org.jeecg.modules.demo.custom.mapper.CustomCompanySceneMapper;
import org.jeecg.modules.demo.custom.model.CustomCompanyScenePhotoTree;
import org.jeecg.modules.demo.custom.service.ICustomCompanyScenePhotoService;
import org.jeecg.modules.demo.custom.service.ICustomCompanySceneService;
import org.jeecg.modules.demo.custom.service.ICustomCrmPhotoTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Description: custom_company_scene
 * @Author: jeecg-boot
 * @Date:   2025-06-09
 * @Version: V1.0
 */
@Service
public class CustomCompanySceneServiceImpl extends ServiceImpl<CustomCompanySceneMapper, CustomCompanyScene> implements ICustomCompanySceneService {


    @Autowired
    private ICustomCrmPhotoTypeService customCrmPhotoTypeService;

    @Autowired
    private ICustomCompanyScenePhotoService customCompanyScenePhotoService;


    @Override
    public boolean save(CustomCompanyScene entity) {
        return super.save(entity);
    }


    @Override
    public boolean updateById(CustomCompanyScene entity) {
        List<CustomCompanyScenePhotoTree> photoTrees = entity.getPhotoTrees();
        if (CollUtil.isNotEmpty(photoTrees)){
//            List<CustomCompanyScenePhotoTree> scenePhotoTrees = photoTrees.stream().map(this::treeToList).flatMap(Collection::stream).toList();

            Integer photoCounts = savePhotoTeeList(photoTrees, entity.getId());
            entity.setPhotoCount(photoCounts);
           /*
            List<CustomCompanyScenePhoto> companyScenePhotos = BeanUtil.copyToList(scenePhotoTrees, CustomCompanyScenePhoto.class);
            List<CustomCompanyScenePhoto> photoList = companyScenePhotos.stream().map(companyScenePhoto -> {
                companyScenePhoto.setCompanySceneId(entity.getId());
                return companyScenePhoto;
            }).toList();
            entity.setPhotoCount(photoList.size());
            customCompanyScenePhotoService.saveOrUpdateBatch(photoList);*/
        }
        return super.updateById(entity);
    }

    /**
     * 存储照片树
     * @param scenePhotoTrees
     * @param companySceneId
     * @return
     */
    private Integer savePhotoTeeList(List<CustomCompanyScenePhotoTree> scenePhotoTrees, String companySceneId) {
        if (CollUtil.isEmpty(scenePhotoTrees) || StrUtil.isEmpty(companySceneId)){
            return 0;
        }
        AtomicInteger count = new AtomicInteger(0);
        scenePhotoTrees.forEach(photoTree -> {
            savePhotoTreeNode(photoTree, companySceneId, count);
        });
        return count.get();
    }

    /**
     * 存储照片树节点
     * @param photoTree
     * @param companySceneId
     * @param count
     */
    private void savePhotoTreeNode(CustomCompanyScenePhotoTree photoTree, String companySceneId, AtomicInteger count ) {
        if (Objects.isNull(photoTree) || StrUtil.isEmpty(companySceneId)){
            return;
        }
        CustomCompanyScenePhoto customCompanyScenePhoto = BeanUtil.copyProperties(photoTree, CustomCompanyScenePhoto.class);
        customCompanyScenePhoto.setCompanySceneId(companySceneId);
        customCompanyScenePhotoService.saveOrUpdate(customCompanyScenePhoto);
        //存储一次进行计数
        count.incrementAndGet();
        if (CollUtil.isEmpty(photoTree.getChildren())){
            return;
        }
        photoTree.getChildren().forEach(child -> {
            //记录子节点的父级id
            child.setParentId(customCompanyScenePhoto.getId());
            savePhotoTreeNode(child, companySceneId,  count);
        });
    }

    @Override
    public CustomCompanyScene getById(Serializable id) {
        CustomCompanyScene companyScene = super.getById(id);
        List<CustomCompanyScenePhoto> companyScenePhotos = customCompanyScenePhotoService.getBaseMapper().selectList(new LambdaQueryWrapper<>(CustomCompanyScenePhoto.class).eq(CustomCompanyScenePhoto::getCompanySceneId, id));
        List<CustomCompanyScenePhotoTree> companyScenePhotoTrees =  covertPhotoListToTree(companyScenePhotos);
        companyScene.setPhotoTrees(companyScenePhotoTrees);
        return companyScene;
    }



    /**
     * 照片列表转树结构
     * @param companyScenePhotos
     * @return
     */
    private List<CustomCompanyScenePhotoTree> covertPhotoListToTree(List<CustomCompanyScenePhoto> companyScenePhotos) {
        if (CollUtil.isEmpty(companyScenePhotos)){
            return new ArrayList<>();
        }
        List<CustomCompanyScenePhotoTree> treeList = BeanUtil.copyToList(companyScenePhotos, CustomCompanyScenePhotoTree.class);
        List<CustomCompanyScenePhotoTree> rootList = treeList.stream().filter(photo -> Objects.equals(photo.getTypeLevel(), 0)).toList();
        if (CollUtil.isEmpty(rootList)){
            return new ArrayList<>();
        }
        Map<String, List<CustomCompanyScenePhotoTree>> childrenCollect = treeList.stream().filter(photo -> StrUtil.isNotEmpty(photo.getParentId())).collect(Collectors.groupingBy(CustomCompanyScenePhotoTree::getParentId));
        return rootList.stream().map(root -> {
            return findChildren(root, childrenCollect);
        }).toList();
    }

    /**
     * 递归查找子节点
     * @param parent
     * @param childrenCollect
     * @return
     */
    private CustomCompanyScenePhotoTree findChildren(CustomCompanyScenePhotoTree parent, Map<String, List<CustomCompanyScenePhotoTree>> childrenCollect) {
        //clone 一个新对象进行处理，避免原对象缓存覆盖问题
        CustomCompanyScenePhotoTree newClone = ObjUtil.clone(parent);
        if (parent.getIsLeaf()) {
            return newClone;
        }
        List<CustomCompanyScenePhotoTree> children = childrenCollect.get(parent.getId());
        if (CollUtil.isEmpty(children)){
            return newClone;
        }
        List<CustomCompanyScenePhotoTree> treeChildren = children.stream().map(child -> {
            return findChildren(child, childrenCollect);
        }).toList();
        newClone.setChildren(treeChildren);
        return newClone;
    }

    /**
     * 树结构转列表结构
     * @param tree
     * @return
     */
    private List<CustomCompanyScenePhotoTree> treeToList(CustomCompanyScenePhotoTree tree){
    	List<CustomCompanyScenePhotoTree> list = new ArrayList<>();
    	list.add(tree);
    	if(CollUtil.isNotEmpty(tree.getChildren())){
    		for(CustomCompanyScenePhotoTree child : tree.getChildren()){
    			list.addAll(treeToList(child));
    		}
    	}
    	return list;
    }

}
