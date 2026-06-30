package com.xzcit.duanfengheng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xzcit.duanfengheng.dto.SetmealDto;
import com.xzcit.duanfengheng.entity.Setmeal;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    // 新增套餐，同时保存套餐菜品
    void saveWithDish(SetmealDto setmealDto);
    // 套餐分页查询（带分类名称）
    Page<SetmealDto> pageSetmeal(int page, int pageSize, String name);
    // 根据id查询套餐及关联菜品（修改回显）
    SetmealDto getByIdWithDish(Long id);
    // 修改套餐及关联菜品
    void updateWithDish(SetmealDto setmealDto);
    // 批量删除套餐，同时删除关联菜品，在售不可删
    void removeWithDish(List<Long> ids);
}