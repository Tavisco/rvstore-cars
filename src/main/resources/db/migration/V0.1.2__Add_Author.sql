-- Table Definition ----------------------------------------------

CREATE TABLE "w_car_authors" (
    "author_id" serial PRIMARY KEY,
    "name" text NOT NULL,
    "car_id" serial REFERENCES w_cars(car_id) ON DELETE CASCADE ON UPDATE CASCADE
);