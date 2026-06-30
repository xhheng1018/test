package com.xzcit.duanfengheng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xzcit.duanfengheng.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
    /**
     * 批量插入套餐菜品关联数据
     */
    void insertBatchSomeColumn(@Param("list") List<SetmealDish> list);
}