package com.nwhisler.local_observability.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.Objects;

public class AlertEvaluation {
    
    private final boolean triggered;
    private final long matchingEventCount;
    
    @NotNull
    private final OffsetDateTime windowStart;

    @NotNull
    private final OffsetDateTime windowEnd;

    public AlertEvaluation(
            Boolean triggered,
            long matchingEventCount,
            OffsetDateTime windowStart,
            OffsetDateTime windowEnd
    ) {
        this.triggered = triggered;
        this.matchingEventCount = matchingEventCount;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
    }

    public Boolean isTriggered() {
        return triggered;
    }

    public long getMatchingEventCount() {
        return matchingEventCount;
    }

    public OffsetDateTime getWindowStart() {
        return windowStart;
    }

    public OffsetDateTime getWindowEnd() {
        return windowEnd;
    }

}
