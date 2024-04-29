package com.ssafy.devway.domain.block;

import com.ssafy.devway.domain.element.Element;

public interface Block<T extends Element> {
    void addElement(T element);
    void displayElements();
    void connect(Block<?> other);
}

