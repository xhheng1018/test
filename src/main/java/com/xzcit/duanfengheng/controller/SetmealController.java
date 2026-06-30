package com.xzcit.duanfengheng.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xzcit.duanfengheng.common.R;
import com.xzcit.duanfengheng.dto.SetmealDto;
import com.xzcit.duanfengheng.entity.Setmeal;
import com.xzcit.duanfengheng.service.SetmealService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理接口
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    private final SetmealService setmealService;

    // 构造器注入
    public SetmealController(SetmealService setmealService) {
        this.setmealService = setmealService;
    }

    /**
     * 套餐分页查询
     * @param page 页码
     * @param pageSize 每页条数
     * @param name 套餐名称模糊搜索
     * @return 分页数据（携带分类名称）
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        Page<SetmealDto> pageInfo = setmealService.pageSetmeal(page, pageSize, name);
        return R.success(pageInfo);
    }

    /**
     * 新增套餐（同时保存套餐绑定菜品）
     * @param setmealDto 套餐+菜品数据
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 根据ID查询套餐（编辑页面回显，携带绑定菜品）
     * @param id 套餐id
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
        SetmealDto dto = setmealService.getByIdWithDish(id);
        return R.success(dto);
    }

    /**
     * 修改套餐（同步更新绑定菜品）
     * @param setmealDto 套餐+新菜品数据
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success("修改套餐成功");
    }

    /**
     * 批量删除套餐（在售套餐禁止删除）
     * @param ids 套餐id集合
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 批量启售/停售套餐
     * @param status 状态 1起售 0停售
     * @param ids 套餐id集合
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        List<Setmeal> setmealList = ids.stream()
                .map(id -> {
                    Setmeal setmeal = new Setmeal();
                    setmeal.setId(id);
                    setmeal.setStatus(status);
                    return setmeal;
                }).collect(Collectors.toList());
        setmealService.updateBatchById(setmealList);
        return R.success("状态修改成功");
    }
}