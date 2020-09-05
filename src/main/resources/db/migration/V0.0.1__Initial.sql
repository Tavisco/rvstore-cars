DROP TABLE IF EXISTS w_cars;
CREATE TABLE w_cars (
      "car_id" int not null PRIMARY KEY,
      "name" varchar(255) not null,
      "description" text NOT NULL
);
DROP SEQUENCE IF EXISTS w_cars_car_id_seq;
CREATE SEQUENCE w_cars_car_id_seq;

DROP TABLE IF EXISTS w_car_authors;
create table w_car_authors
(
    author_id int not null primary key,
    name varchar(255) not null,
    car_id int not null references w_cars
);
DROP SEQUENCE IF EXISTS w_car_authors_author_id_seq;
CREATE SEQUENCE w_car_authors_author_id_seq;