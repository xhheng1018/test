package com.xzcit.duanfengheng.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzcit.duanfengheng.common.R;
import com.xzcit.duanfengheng.dto.DishDto;
import com.xzcit.duanfengheng.entity.Category;
import com.xzcit.duanfengheng.entity.Dish;
import com.xzcit.duanfengheng.entity.DishFlavor;
import com.xzcit.duanfengheng.service.CategoryService;
import com.xzcit.duanfengheng.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    // 读取配置文件图片存储路径
    @Value("${reggie.path}")
    private String baseUploadPath;

    /**
     * 跳转菜品列表页面 + 分页 + 菜品名称模糊搜索
     */
    @GetMapping
    public String toPage(@RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "10") Integer pageSize,
                         String name,
                         Model model) {
        IPage<Dish> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Dish::getName, name);
        }
        wrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, wrapper);

        // 查询所有菜品分类，下拉框使用
        LambdaQueryWrapper<Category> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(Category::getType, 1);
        List<Category> categoryList = categoryService.list(categoryWrapper);

        model.addAttribute("pageData", pageInfo);
        model.addAttribute("pageName", name);
        model.addAttribute("categoryList", categoryList);
        return "backend/page/dish";
    }

    /**
     * 分页JSON接口（备用，前后端分离场景）
     */
    @ResponseBody
    @GetMapping("/page")
    public R<IPage<Dish>> page(Integer page, Integer pageSize, String name) {
        IPage<Dish> pageModel = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Dish::getName, name);
        }
        wrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageModel, wrapper);
        return R.success(pageModel);
    }

    /**
     * 【套餐弹窗专用】根据分类ID查询上架菜品（完全匹配课程文档）
     */
    @ResponseBody
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        // 有分类ID则按分类筛选
        if (dish.getCategoryId() != null) {
            wrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        }
        // 只查询起售菜品
        wrapper.eq(Dish::getStatus, 1);
        // 排序
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(wrapper);
        return R.success(dishList);
    }

    /**
     * 跳转到新增菜品页面，携带所有分类下拉数据
     */
    @GetMapping("/toAdd")
    public String toAdd(Model model) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getType, 1);
        List<Category> categoryList = categoryService.list(wrapper);
        model.addAttribute("categoryList", categoryList);
        return "backend/page/dish-add";
    }

    /**
     * 图片上传接口
     */
    @PostMapping("/upload")
    @ResponseBody
    public R<String> upload(MultipartFile file) {
        // 获取原始文件名
        String originalName = file.getOriginalFilename();
        // 截取后缀
        String suffix = originalName.substring(originalName.lastIndexOf("."));
        // 生成唯一文件名
        String fileName = System.currentTimeMillis() + suffix;
        // 完整保存路径
        File saveFile = new File(baseUploadPath + fileName);
        // 不存在文件夹自动创建
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }
        try {
            file.transferTo(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error("图片上传失败");
        }
        // 返回文件名存入数据库image字段
        return R.success(fileName);
    }

    /**
     * 新增菜品（页面提交地址 /dish/add）
     * 参数改为flavorStr，避开和DishDto内部List<DishFlavor> flavors重名冲突
     */
    @PostMapping("/add")
    public String save(DishDto dishDto, String name, String flavorStr) {
        // 解析前端传递的口味字符串
        List<DishFlavor> flavorList = parseFlavorStr(flavorStr);
        dishDto.setFlavors(flavorList);
        dishService.saveWithFlavor(dishDto);
        // 修复：不带中文参数跳转，解决URL问号乱码
        return "redirect:/dish";
    }

    /**
     * 解析前端口味字符串，转为菜品口味实体集合
     */
    private List<DishFlavor> parseFlavorStr(String flavorStr) {
        List<DishFlavor> list = new ArrayList<>();
        if (!StringUtils.hasText(flavorStr)) {
            return list;
        }
        // 拆分多组口味
        String[] groupArray = flavorStr.split("\\|");
        for (String group : groupArray) {
            String[] kv = group.split(":");
            if (kv.length != 2) {
                continue;
            }
            String flavorName = kv[0];
            String[] valueArr = kv[1].split(",");
            for (String val : valueArr) {
                DishFlavor df = new DishFlavor();
                df.setName(flavorName);
                df.setValue(val);
                list.add(df);
            }
        }
        return list;
    }

    /**
     * 跳转到编辑菜品页面，回显菜品+口味数据
     */
    @GetMapping("/edit")
    public String edit(Long id, String name, Model model) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        model.addAttribute("item", dishDto);
        model.addAttribute("pageName", name);

        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getType, 1);
        List<Category> categoryList = categoryService.list(wrapper);
        model.addAttribute("categoryList", categoryList);
        return "backend/page/dish-edit";
    }

    /**
     * 修改菜品（同步更新口味）
     */
    @PostMapping("/update")
    public String update(DishDto dishDto, String name) {
        dishService.updateWithFlavor(dishDto);
        return "redirect:/dish";
    }

    /**
     * 菜品启售/停售状态切换
     */
    @PostMapping("/status")
    public String status(Long id, Integer status, String name) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishService.updateById(dish);
        return "redirect:/dish";
    }

    /**
     * 删除菜品（异步AJAX返回JSON，弹窗提示）
     */
    @ResponseBody
    @GetMapping("/delete")
    public R<String> delete(@RequestParam List<Long> ids) {
        dishService.deleteByIds(ids);
        return R.success("删除成功");
    }
}