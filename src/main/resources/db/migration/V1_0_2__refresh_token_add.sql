create table refresh_token
(
    email varchar(255) not null unique,
    token varchar(255) not null unique,
    primary key (email)
);
