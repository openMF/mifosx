package org.mifosplatform.paginationutil;

import com.google.gson.Gson;

import java.util.List;

public class Page<E> {

    private List<E> pageItems;

    public void setPageItems(List<E> pageItems) {
        this.pageItems = pageItems;
    }

    public List<E> getPageItems() {
        return pageItems;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}