package ru.drobyazko.fooddeliveryservice;

import java.util.List;

public class TestPageResponse<T> {
    private List<T> content;

    protected TestPageResponse() {
    }

    public List<T> getContent() {
        return content;
    }
}