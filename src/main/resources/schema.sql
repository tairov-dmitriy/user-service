create table if not exists users(
  login text primary key,
  fullName text not null,
  birthDate date not null,
  phone text,
  locked boolean not null default false
);

create index if not exists ix_user_login on users(login);