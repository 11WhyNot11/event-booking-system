WITH dups AS (
    SELECT id
    FROM (
             SELECT id,
                    ROW_NUMBER() OVER (
                   PARTITION BY name, start_time, location
                   ORDER BY id
               ) AS rn
             FROM events
         ) t
    WHERE t.rn > 1
)
DELETE FROM events e
    USING dups d
WHERE e.id = d.id;

ALTER TABLE events
    ADD CONSTRAINT uq_events_name_start_location
        UNIQUE (name, start_time, location);