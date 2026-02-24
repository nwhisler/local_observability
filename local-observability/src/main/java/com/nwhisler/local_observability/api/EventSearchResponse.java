package com.nwhisler.local_observability.api;

import org.springframework.data.domain.Page;

import com.nwhisler.local_observability.domain.Event;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class EventSearchResponse {

    public EventSearchResponse() {}
    
    public List<Event> items;
    public List<Event> getItems() {return this.items;}
    public void setItems(List<Event> items) {this.items = items;}

    public int page;
    public int getPage() {return this.page;}
    public void setPage(int page) {this.page = page;} 

    public int size;
    public int getSize() {return this.size;}
    public void setSize(int size) {this.size = size;} 

    public long totalItems;
    public long getTotalItems() {return this.totalItems;}
    public void setTotalItems(long totalItems) {this.totalItems = totalItems;} 

    public int totalPages;
    public int getTotalPages() {return this.totalPages;}
    public void setTotalPages(int totalPages) {this.totalPages = totalPages;} 

    public boolean first;
    public boolean isFirst() {return this.first;}
    public void setFirst(boolean first) {this.first = first;} 

    public boolean last;
    public boolean isLast() {return this.last;}
    public void setLast(boolean last) {this.last = last;} 

    public int numberOfElements;
    public int getNumberOfElements() {return this.numberOfElements;}
    public void setNumberOfElements(int numberOfElements) {this.numberOfElements = numberOfElements;} 

    public static EventSearchResponse from(Page<Event> p) { 
        EventSearchResponse resp = new EventSearchResponse();

        resp.setItems(p.getContent());
        resp.setPage(p.getNumber());
        resp.setSize(p.getSize());
        resp.setTotalItems(p.getTotalElements());
        resp.setTotalPages(p.getTotalPages());
        resp.setFirst(p.isFirst());
        resp.setLast(p.isLast());
        resp.setNumberOfElements(p.getNumberOfElements());

        return resp;
    }

}