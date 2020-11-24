CREATE TABLE IF NOT EXISTS "rvstore_cars"."w_cars" (
      "car_id" INT NOT NULL PRIMARY KEY,
      "name" VARCHAR(255) NOT NULL,
      "description" VARCHAR(255) NOT NULL,
      "uploader_id" VARCHAR(255) NOT NULL,
      "uploader_name" VARCHAR(255) NOT NULL,
      "create_date" TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
      "update_date" TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL
);
CREATE SEQUENCE IF NOT EXISTS "rvstore_cars"."w_cars_car_id_seq";

CREATE TABLE IF NOT EXISTS "rvstore_cars"."w_car_authors"
(
    "author_id" INT NOT NULL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "car_id" INT NOT NULL REFERENCES w_cars
);

CREATE SEQUENCE IF NOT EXISTS "rvstore_cars"."w_car_authors_author_id_seq";

COMMIT;
