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

CREATE TABLE IF NOT EXISTS alerts (
  id UUID PRIMARY KEY,
  name VARCHAR(128) NOT NULL,
  enabled BOOLEAN NOT NULL,

  service VARCHAR(64),
  level VARCHAR(16),
  q TEXT,

  threshold_count INTEGER NOT NULL,
  window_seconds INTEGER NOT NULL,
  cooldown_seconds INTEGER NOT NULL,

  last_triggered TIMESTAMPTZ,

  created TIMESTAMPTZ NOT NULL,
  updated TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_alerts_service ON alerts(service);
CREATE INDEX IF NOT EXISTS idx_alerts_level ON alerts(level);
CREATE INDEX IF NOT EXISTS idx_alerts_enabled ON alerts(enabled);