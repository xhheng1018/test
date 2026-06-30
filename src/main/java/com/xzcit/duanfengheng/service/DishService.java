package com.xzcit.duanfengheng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xzcit.duanfengheng.dto.DishDto;
import com.xzcit.duanfengheng.entity.Dish;
import java.util.List;

/**
 * 菜品业务接口
 */
public interface DishService extends IService<Dish> {

    /**
     * 新增菜品，同步保存菜品关联口味
     * @param dishDto 菜品封装对象（携带口味集合）
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 根据菜品ID查询菜品完整信息（包含口味、分类名称）
     * @param id 菜品主键
     * @return 菜品Dto
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * 修改菜品信息，同步更新关联口味
     * @param dishDto 菜品封装对象
     */
    void updateWithFlavor(DishDto dishDto);

    /**
     * 批量删除菜品
     * 业务限制：存在启售中菜品禁止删除
     * @param ids 待删除菜品ID集合
     */
    void deleteByIds(List<Long> ids);
}