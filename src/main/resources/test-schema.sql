create table if not exists users(
  login varchar primary key,
  fullName text not null,
  birthDate date not null,
  phone text,
  locked boolean not null default false
);