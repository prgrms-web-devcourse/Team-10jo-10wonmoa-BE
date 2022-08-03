create table refresh_token
(
    id    bigint       not null auto_increment,
    email varchar(255) not null unique,
    token varchar(255) not null unique,
    primary key (id)
);
