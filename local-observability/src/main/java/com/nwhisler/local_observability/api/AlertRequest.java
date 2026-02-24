package com.nwhisler.local_observability.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;


public class AlertRequest {

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


}