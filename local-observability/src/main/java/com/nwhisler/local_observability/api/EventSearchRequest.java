package com.nwhisler.local_observability.api;

import java.time.OffsetDateTime;

public class EventSearchRequest {

    private OffsetDateTime from;
    private OffsetDateTime to;
    private String service;
    private String level;
    private String q;
    private int page = 0;
    private int size = 50;

    public EventSearchRequest() {}

    public OffsetDateTime getFrom() { return from; }
    public void setFrom(OffsetDateTime from) { this.from = from; }

    public OffsetDateTime getTo() { return to; }
    public void setTo(OffsetDateTime to) { this.to = to; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

}