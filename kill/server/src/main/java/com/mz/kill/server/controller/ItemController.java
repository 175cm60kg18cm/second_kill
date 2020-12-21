package com.mz.kill.server.controller;

import com.mz.kill.model.entity.ItemKill;
import com.mz.kill.server.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Controller
public class ItemController {
    private static final Logger logger =LoggerFactory.getLogger(ItemController.class);
    private static final String prefix="item";
    @Autowired
    ItemService itemService;
    @GetMapping(value = {"/","index",prefix+"/index.html",prefix+"/list"})
    public String list(ModelMap modelMap){
        try {
            List<ItemKill> itemKill = itemService.getItemKill();
            modelMap.put("list",itemKill);
            logger.info("收到商品列表"+itemKill);
        }catch (Exception e){
            logger.error("获取商品列表失败...");
            return "error";
        }
        return "list";
    }
    @GetMapping("item/detail/{id}")
    public String info(@PathVariable Integer id,ModelMap modelMap){
        ItemKill itemDetail = itemService.getItemDetail(id);
        modelMap.put("detail",itemDetail);
        return "info";
    }
}
