create table if not exists POST (
id serial primary key,
name text,
text text,
link varchar(300) unique,
created timestamp
);