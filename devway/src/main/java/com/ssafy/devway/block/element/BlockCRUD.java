package com.ssafy.devway.block.element;

import com.ssafy.devway.block.block.Block;
import java.util.List;

public interface BlockCRUD<T extends BlockElement> {
  List<Block<T>> getList();

  void addList(Block<T> block);

  void deleteList();
}
