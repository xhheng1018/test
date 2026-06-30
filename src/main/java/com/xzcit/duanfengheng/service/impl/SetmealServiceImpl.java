package com.xzcit.duanfengheng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzcit.duanfengheng.dto.SetmealDto;
import com.xzcit.duanfengheng.entity.Category;
import com.xzcit.duanfengheng.entity.Setmeal;
import com.xzcit.duanfengheng.entity.SetmealDish;
import com.xzcit.duanfengheng.mapper.SetmealDishMapper;
import com.xzcit.duanfengheng.mapper.SetmealMapper;
import com.xzcit.duanfengheng.service.CategoryService;
import com.xzcit.duanfengheng.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Resource
    private CategoryService categoryService;

    @Resource
    private SetmealDishMapper setmealDishMapper;

    @Override
    public Page<SetmealDto> pageSetmeal(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Setmeal::getName, name);
        }
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        this.page(setmealPage, wrapper);

        List<Setmeal> setmealList = setmealPage.getRecords();
        List<SetmealDto> dtoList = new ArrayList<>();
        for (Setmeal setmeal : setmealList) {
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, dto);
            Category category = categoryService.getById(setmeal.getCategoryId());
            String cateName = category == null ? "无分类" : category.getName();
            dto.setCategoryName(cateName);
            dtoList.add(dto);
        }

        Page<SetmealDto> dtoPage = new Page<>();
        BeanUtils.copyProperties(setmealPage, dtoPage, "records");
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        Long setmealId = setmealDto.getId();
        List<SetmealDish> dishList = setmealDto.getSetmealDishes();
        if (dishList != null && !dishList.isEmpty()) {
            dishList.forEach(item -> item.setSetmealId(setmealId));
            setmealDishMapper.insertBatchSomeColumn(dishList);
        }
    }

    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);
        if (setmeal == null) {
            return null;
        }
        SetmealDto dto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, dto);

        LambdaQueryWrapper<SetmealDish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> dishList = setmealDishMapper.selectList(dishWrapper);
        dto.setSetmealDishes(dishList);
        return dto;
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        Long setmealId = setmealDto.getId();
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> delWrapper = new LambdaQueryWrapper<>();
        delWrapper.eq(SetmealDish::getSetmealId, setmealId);
        setmealDishMapper.delete(delWrapper);

        List<SetmealDish> dishList = setmealDto.getSetmealDishes();
        if (dishList != null && !dishList.isEmpty()) {
            dishList.forEach(item -> item.setSetmealId(setmealId));
            setmealDishMapper.insertBatchSomeColumn(dishList);
        }
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> saleWrapper = new LambdaQueryWrapper<>();
        saleWrapper.in(Setmeal::getId, ids);
        saleWrapper.eq(Setmeal::getStatus, 1);
        long count = this.count(saleWrapper);
        if (count > 0) {
            throw new RuntimeException("在售套餐无法删除，请先下架！");
        }
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishMapper.delete(dishWrapper);
    }
}