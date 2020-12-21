package com.mz.kill.server.service.impl;

import com.mz.kill.model.entity.ItemKill;
import com.mz.kill.model.mapper.ItemKillMapper;
import com.mz.kill.server.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ItemServiceImpl implements ItemService {
    private Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);
    @Autowired
    private ItemKillMapper itemKillMapper;

    @Override
    public List<ItemKill> getItemKill() {
        return itemKillMapper.selectAll();
    }

    @Override
    public ItemKill getItemDetail(Integer id) {
        return itemKillMapper.selectById(id);
    }
}
