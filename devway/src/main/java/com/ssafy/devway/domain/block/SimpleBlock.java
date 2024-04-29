package com.ssafy.devway.domain.block;

import com.ssafy.devway.domain.element.Element;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SimpleBlock<T extends Element> implements Block<T> {

    private List<T> elements = new ArrayList<>();

    @Override
    public void addElement(T element) {
        elements.add(element);
    }

    @Override
    public void displayElements() {
        for (T element : elements) {
            element.display();
        }
    }

    @Override
    public void connect(Block<?> other) {
        // Implement connection logic
        System.out.println("Connecting blocks...");
    }
}
