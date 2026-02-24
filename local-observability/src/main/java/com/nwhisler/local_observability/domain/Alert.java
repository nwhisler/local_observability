package com.nwhisler.local_observability.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.Inherited;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id 
    private UUID id;
    public UUID getId() {return this.id;}
    public void setId(UUID id) {this.id = id;}

    @NotNull
    private String name;
    public String getName() {return this.name;}
    public void setName(String name) {this.name = name;}

    private Boolean enabled = true;
    public Boolean isEnabled() {return this.enabled;}
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

    @NotNull
    private int thresholdCount;
    public int getThresholdCount() {return this.thresholdCount;}
    public void setThresholdCount(int thresholdCount) {this.thresholdCount = thresholdCount;}

    @NotNull
    private int windowSeconds;
    public int getWindowSeconds() {return this.windowSeconds;}
    public void setWindowSeconds(int windowSeconds) {this.windowSeconds = windowSeconds;} 
    
    @NotNull
    private int cooldownSeconds = 0;
    public int getCooldownSeconds() {return this.cooldownSeconds;}
    public void setCooldownSeconds(int cooldownSeconds) {this.cooldownSeconds = cooldownSeconds;}   
    
    private OffsetDateTime lastTriggered = null;
    public OffsetDateTime getLastTriggered() {return this.lastTriggered;}
    public void setLastTriggered(OffsetDateTime lastTriggered) {this.lastTriggered = lastTriggered;}

    @NotNull
    private OffsetDateTime created;
    public OffsetDateTime getCreated() {return this.created;}
    public void setCreated(OffsetDateTime created) {this.created = created;}    

    @NotNull
    private OffsetDateTime updated;
    public OffsetDateTime getUpdated() {return this.updated;}
    public void setUpdated(OffsetDateTime updated) {this.updated = updated;} 
    
    

}