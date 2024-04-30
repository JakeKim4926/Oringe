package com.ssafy.devway.block.block;

import com.ssafy.devway.block.element.BlockCRUD;
import com.ssafy.devway.block.element.BlockElement;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class Block<T extends BlockElement> implements BlockCRUD {

    T data;
    List<Block<T>> list = new ArrayList<>();


    @Override
    public List<Block<T>> getList() {
        return list;
    }

    @Override
    public void addList(Block block) {
        list.add(block);
    }

    @Override
    public void deleteList() {
        list.clear();
    }
}

