package com.nwhisler.local_observability.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.Map;

public class EventRequest {

    @NotBlank
    private String level;
    public String getLevel() {return this.level;}
    public void setLevel(String level) {this.level = level;}

    @NotBlank
    private String service;
    public String getService() {return this.service;}
    public void setService(String service) {this.service = service;}

    @NotBlank
    private String message;
    public String getMessage() {return this.message;}
    public void setMessage(String message) {this.message = message;}

    private Map<String, Object> attrs;
    public Map<String, Object> getAttrs() {return this.attrs;}
    public void setAttrs(Map<String, Object> attrs) {this.attrs = attrs;}
}