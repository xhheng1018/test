package com.xzcit.duanfengheng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzcit.duanfengheng.dto.DishDto;
import com.xzcit.duanfengheng.entity.Category;
import com.xzcit.duanfengheng.entity.Dish;
import com.xzcit.duanfengheng.entity.DishFlavor;
import com.xzcit.duanfengheng.mapper.DishMapper;
import com.xzcit.duanfengheng.service.CategoryService;
import com.xzcit.duanfengheng.service.DishFlavorService;
import com.xzcit.duanfengheng.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品+批量保存口味
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品基础信息
        this.save(dishDto);
        Long dishId = dishDto.getId();
        // 给口味绑定菜品ID
        List<DishFlavor> flavorList = dishDto.getFlavors().stream()
                .map(flavor -> {
                    flavor.setDishId(dishId);
                    return flavor;
                }).collect(Collectors.toList());
        // 批量插入口味
        dishFlavorService.saveBatch(flavorList);
    }

    /**
     * 根据ID查询菜品+口味+分类名
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        // 查询关联口味
        LambdaQueryWrapper<DishFlavor> flavorWrapper = new LambdaQueryWrapper<>();
        flavorWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(flavorWrapper);
        dishDto.setFlavors(flavors);

        // 查询分类名称
        Category category = categoryService.getById(dish.getCategoryId());
        if (category != null) {
            dishDto.setCategoryName(category.getName());
        }
        return dishDto;
    }

    /**
     * 修改菜品，同步更新口味（先删旧口味，再新增）
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新菜品基础数据
        this.updateById(dishDto);

        // 删除原有口味
        LambdaQueryWrapper<DishFlavor> delWrapper = new LambdaQueryWrapper<>();
        delWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(delWrapper);

        // 新增新口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(flavor -> flavor.setDishId(dishDto.getId()));
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 批量删除菜品，校验在售菜品拦截
     */
    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        // 校验是否存在启售中菜品
        LambdaQueryWrapper<Dish> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.in(Dish::getId, ids).eq(Dish::getStatus, 1);
        long sellingCount = this.count(checkWrapper);
        if (sellingCount > 0) {
            throw new RuntimeException("存在正在售卖的菜品，无法删除！");
        }

        // 先删除关联口味
        LambdaQueryWrapper<DishFlavor> flavorWrapper = new LambdaQueryWrapper<>();
        flavorWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(flavorWrapper);

        // 再删除菜品主表
        this.removeByIds(ids);
    }
}