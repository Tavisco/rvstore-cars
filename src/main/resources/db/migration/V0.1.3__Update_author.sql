ALTER TABLE w_car_authors
  ALTER COLUMN car_id TYPE INTEGER
  USING car_id::integer;

-- ALTER TABLE w_car_authors ALTER COLUMN car_id DROP NOT NULL;