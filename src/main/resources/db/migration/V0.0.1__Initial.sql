-- Table Definition ----------------------------------------------

CREATE TABLE "w_cars" (
    "car_id" serial PRIMARY KEY,
    "name" text NOT NULL,
    "description" text NOT NULL
);

-- Indices -------------------------------------------------------

CREATE SEQUENCE cars_seq;