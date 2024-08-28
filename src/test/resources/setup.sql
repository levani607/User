
create table user_service.application_user
(
    id          bigint
        constraint application_user_pk
            primary key,
    username    varchar(256),
    firstname   varchar(256),
    password    varchar(1024),
    email       varchar(256),
    status user_service.user_status,
    role user_service.user_role
);


