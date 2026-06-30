package com.xzcit.duanfengheng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzcit.duanfengheng.entity.Category;
import com.xzcit.duanfengheng.mapper.CategoryMapper;
import com.xzcit.duanfengheng.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}