create table public._user
(
    id                uuid not null
        primary key,
    avatar            uuid,
    birth_date        date,
    city              varchar(255),
    email             varchar(255)
        constraint email_constraint
            unique,
    full_name         varchar(255),
    login             varchar(255)
        constraint login_constraint
            unique,
    password          varchar(255),
    phone_number      varchar(255),
    registration_date timestamp
);

alter table public._user
    owner to postgres;

