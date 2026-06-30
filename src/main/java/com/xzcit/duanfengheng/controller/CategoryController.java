package com.xzcit.duanfengheng.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzcit.duanfengheng.common.R;
import com.xzcit.duanfengheng.entity.Category;
import com.xzcit.duanfengheng.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 跳转分类页面 + 分页 + 名称模糊搜索
     */
    @GetMapping
    public String toPage(@RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "10") Integer pageSize,
                         String name,
                         Model model) {
        // 分页对象
        IPage<Category> pageInfo = new Page<>(page, pageSize);
        // 模糊查询条件
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Category::getName, name);
        }
        categoryService.page(pageInfo, wrapper);
        // 传给页面：分页数据、搜索关键词（回显用）
        model.addAttribute("pageData", pageInfo);
        model.addAttribute("pageName", name);
        return "backend/page/category";
    }

    /**
     * 分页查询接口（备用JSON接口）
     */
    @ResponseBody
    @GetMapping("/page")
    public R<IPage<Category>> page(Integer page, Integer pageSize, String name) {
        IPage<Category> pageModel = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Category::getName, name);
        }
        categoryService.page(pageModel, wrapper);
        return R.success(pageModel);
    }

    /**
     * 【新增】前端套餐下拉专用接口 /category/list?type=2
     */
    @ResponseBody
    @GetMapping("/list")
    public R<List<Category>> list(@RequestParam Integer type) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getType, type);
        List<Category> list = categoryService.list(wrapper);
        return R.success(list);
    }

    /**
     * 新增分类：先校验名称是否重复，重复则留在页面提示，不跳转报错页
     */
    @PostMapping("/save")
    public String save(Category category, String name, Model model) {
        // 1. 构造查询条件，精确匹配分类名称
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, category.getName());
        // 统计同名分类数量
        long count = categoryService.count(wrapper);

        if (count > 0) {
            // 2. 存在同名，传递错误信息回页面
            model.addAttribute("errorMsg", "分类名称【" + category.getName() + "】已存在，不能重复添加");
            // 重新加载分页数据，保证列表正常显示
            IPage<Category> pageInfo = new Page<>(1, 10);
            LambdaQueryWrapper<Category> pageWrapper = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(name)) {
                pageWrapper.like(Category::getName, name);
            }
            categoryService.page(pageInfo, pageWrapper);
            model.addAttribute("pageData", pageInfo);
            model.addAttribute("pageName", name);
            // 返回分类页面，弹窗保留、展示红色提示
            return "backend/page/category";
        }

        // 3. 名称不重复，执行新增
        categoryService.save(category);
        // 新增成功，重定向回列表，保留搜索条件
        return "redirect:/category?name=" + name;
    }

    /**
     * 删除分类，携带搜索条件回退
     */
    @GetMapping("/delete")
    public String delete(Long id, String name) {
        categoryService.removeById(id);
        return "redirect:/category?name=" + name;
    }

    /**
     * 编辑回显页面，携带搜索条件
     */
    @GetMapping("/edit")
    public String edit(Long id, String name, Model model) {
        Category category = categoryService.getById(id);
        model.addAttribute("item", category);
        model.addAttribute("pageName", name);
        return "backend/page/category-edit";
    }

    /**
     * 修改提交，携带搜索条件回退
     */
    @PostMapping("/update")
    public String update(Category category, String name) {
        categoryService.updateById(category);
        return "redirect:/category?name=" + name;
    }
}