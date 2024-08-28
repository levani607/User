create schema if not exists user_service;

create sequence if not exists user_service.application_user_id_seq;

alter sequence user_service.application_user_id_seq owner to postgres;

create type  user_service.user_status  as enum ('ACTIVE', 'DELETED');

alter type user_service.user_status owner to postgres;
create type user_service.user_role as enum ('ROLE_ADMIN', 'ROLE_USER');

alter type user_service.user_role owner to postgres;