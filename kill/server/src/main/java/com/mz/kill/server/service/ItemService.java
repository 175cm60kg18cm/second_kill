package com.mz.kill.server.service;

import com.mz.kill.model.entity.ItemKill;

import java.util.List;

public interface ItemService {
    List<ItemKill> getItemKill();
    ItemKill getItemDetail(Integer id);
}
