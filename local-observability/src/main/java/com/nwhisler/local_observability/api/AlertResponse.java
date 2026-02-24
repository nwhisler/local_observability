package com.nwhisler.local_observability.api;

import com.nwhisler.local_observability.domain.Alert;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class AlertResponse {

    private UUID id;
    public UUID getId() {return this.id;}
    public void setId(UUID id) {this.id = id;}

    private String name;
    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}   

    private boolean enabled = true;
    public boolean isEnabled() {return this.enabled;}
    public void setEnabled(boolean enabled) {this.enabled = enabled;}

    private String service = null;
    public String getService() {return this.service;}
    public void setService(String service) {this.service = service;}

    private String level = null;
    public String getLevel() {return this.level;}
    public void setLevel(String level) {this.level = level;}

    private String q = null;
    public String getQ() {return this.q;}
    public void setQ(String q) {this.q = q;}

    private int thresholdCount;
    public int getThresholdCount() {return this.thresholdCount;}
    public void setThresholdCount(int thresholdCount) {this.thresholdCount = thresholdCount;}

    private int windowSeconds;
    public int getWindowSeconds() {return this.windowSeconds;}
    public void setWindowSeconds(int windowSeconds) {this.windowSeconds = windowSeconds;} 

    private int cooldownSeconds = 0;
    public int getCooldownSeconds() {return this.cooldownSeconds;}
    public void setCooldownSeconds(int cooldownSeconds) {this.cooldownSeconds = cooldownSeconds;} 

    private OffsetDateTime lastTriggered = null;
    public OffsetDateTime getLastTriggered() {return this.lastTriggered;}
    public void setLastTriggered(OffsetDateTime lastTriggered) {this.lastTriggered = lastTriggered;}

    private OffsetDateTime created;
    public OffsetDateTime getCreated() {return this.created;}
    public void setCreated(OffsetDateTime created) {this.created = created;}    

    private OffsetDateTime updated;
    public OffsetDateTime getUpdated() {return this.updated;}
    public void setUpdated(OffsetDateTime updated) {this.updated = updated;} 


    public static AlertResponse from(Alert alert) {
        
        AlertResponse resp = new AlertResponse();
        resp.setId(alert.getId());
        resp.setName(alert.getName());
        resp.setEnabled(alert.isEnabled());
        resp.setService(alert.getService());
        resp.setLevel(alert.getLevel());
        resp.setQ(alert.getQ());
        resp.setThresholdCount(alert.getThresholdCount());
        resp.setWindowSeconds(alert.getWindowSeconds());
        resp.setCooldownSeconds(alert.getCooldownSeconds());
        resp.setLastTriggered(alert.getLastTriggered());
        resp.setCreated(alert.getCreated());
        resp.setUpdated(alert.getUpdated());

        return resp;

    }
    
}