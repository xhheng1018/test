package com.xzcit.duanfengheng.dto;

import com.xzcit.duanfengheng.entity.Setmeal;
import com.xzcit.duanfengheng.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    // 套餐绑定的菜品集合
    private List<SetmealDish> setmealDishes;
    // 页面展示用：套餐分类名称
    private String categoryName;
}