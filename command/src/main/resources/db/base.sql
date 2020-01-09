drop table car;
drop table owner;

create table owner(
    id varchar(20) not null,
    name varchar(100) not null,
    surname varchar(100) not null,
    age integer check(age >= 12) check(age <= 130),
    CONSTRAINT "owner_id_pk" PRIMARY KEY ("id")
) WITH (oids = false);

create table car(
    registration varchar(20) not null,
    owner varchar(20) not null  REFERENCES owner(id),
    brand varchar(30) not null,
    model varchar(60) not null,
    color varchar(40) not null,
    year integer check(year >= 1950) check(year <= 2100),
    CONSTRAINT "car_registration_pk" PRIMARY KEY ("registration")
) WITH (oids = false);