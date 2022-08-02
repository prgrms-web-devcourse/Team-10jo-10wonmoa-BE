create table budget
(
    id         bigint not null auto_increment,
    amount     bigint,
    end_time   date,
    start_time date,
    user_id    bigint,
    primary key (id)
);

create table category
(
    id            bigint      not null auto_increment,
    category_kind varchar(255),
    name          varchar(20) not null,
    primary key (id)
);

create table expenditure
(
    id               bigint      not null auto_increment,
    amount           bigint      not null,
    category_name    varchar(20) not null,
    content          varchar(50),
    register_date    datetime(6) not null,
    user_id          bigint,
    user_category_id bigint,
    primary key (id)
);

create table income
(
    id               bigint      not null auto_increment,
    amount           bigint      not null,
    category_name    varchar(20) not null,
    content          varchar(50),
    register_date    datetime(6) not null,
    user_id          bigint,
    user_category_id bigint,
    primary key (id)
);

create table user
(
    id       bigint       not null auto_increment,
    email    varchar(255) not null,
    password varchar(255) not null,
    username varchar(255) not null,
    primary key (id)
);

create table user_category
(
    id          bigint not null auto_increment,
    category_id bigint not null,
    user_id     bigint,
    primary key (id)
);

alter table user
    add constraint user_email_unique_const unique (email);

alter table budget
    add constraint budget_user_id_fk_const
        foreign key (user_id)
            references user (id);

alter table expenditure
    add constraint expenditure_user_id_fk_const
        foreign key (user_id)
            references user (id);

alter table expenditure
    add constraint expenditure_user_category_id_fk_const
        foreign key (user_category_id)
            references user_category (id);

alter table income
    add constraint income_user_id_fk_const
        foreign key (user_id)
            references user (id);

alter table income
    add constraint income_user_category_id_fk_const
        foreign key (user_category_id)
            references user_category (id);

alter table user_category
    add constraint user_category_category_id_fk_const
        foreign key (category_id)
            references category (id);

alter table user_category
    add constraint user_category_user_id_fk_const
        foreign key (user_id)
            references user (id);