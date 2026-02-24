package com.nwhisler.local_observability.api;

import java.time.OffsetDateTime;
import com.nwhisler.local_observability.domain.AlertEvaluation;

import jakarta.validation.constraints.NotNull;

public class AlertEvaluationResponse {
    
    private boolean triggered;
    public boolean isTriggered() {return this.triggered;}
    
    private long count;
    public long getCount() {return this.count;}

    @NotNull
    OffsetDateTime windowStart;
    public OffsetDateTime getWindowStart() {return this.windowStart;}

    @NotNull
    OffsetDateTime windowEnd;
    public OffsetDateTime getWindowEnd() {return this.windowEnd;}

    public AlertEvaluationResponse(boolean triggered, long count, OffsetDateTime windowStart, OffsetDateTime windowEnd) {
        this.triggered = triggered;
        this.count = count;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
    }

    public static AlertEvaluationResponse from(AlertEvaluation ae) {
        
        boolean triggered = ae.isTriggered();
        long count = ae.getMatchingEventCount();
        OffsetDateTime windowStart = ae.getWindowStart();
        OffsetDateTime windowEnd = ae.getWindowEnd();

        return new AlertEvaluationResponse(triggered, count, windowStart, windowEnd);

    }

}
