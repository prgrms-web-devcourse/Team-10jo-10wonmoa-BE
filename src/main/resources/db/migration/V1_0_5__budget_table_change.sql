alter table budget drop column start_time;
alter table budget drop column end_time;

alter table budget modify amount bigint not null;
alter table budget modify register_date mediumint not null;
alter table budget modify user_id bigint not null;
alter table budget modify user_category_id bigint not null;

alter table budget add constraint fk_budget_user foreign key (user_id) references user (id);
alter table budget add constraint fk_budget_user_category foreign key (user_category_id) references user_category (id);
