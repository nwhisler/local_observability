CREATE TABLE IF NOT EXISTS events (
  id UUID PRIMARY KEY,
  ts TIMESTAMPTZ NOT NULL,
  level VARCHAR(16) NOT NULL,
  service VARCHAR(64) NOT NULL,
  message TEXT NOT NULL,
  attrs JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX IF NOT EXISTS idx_events_ts ON events (ts);
CREATE INDEX IF NOT EXISTS idx_events_service_ts ON events (service, ts);
CREATE INDEX IF NOT EXISTS idx_events_level_ts ON events (level, ts);