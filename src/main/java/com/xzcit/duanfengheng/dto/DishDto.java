package com.xzcit.duanfengheng.dto;

import com.xzcit.duanfengheng.entity.Dish;
import com.xzcit.duanfengheng.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    // 当前菜品绑定的所有口味
    private List<DishFlavor> flavors = new ArrayList<>();
    // 页面展示：分类名称
    private String categoryName;
}