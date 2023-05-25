drop table public.friend;
drop table public.blacklist;

create table public.blacklist
(
    id                uuid         not null
        primary key,
    added_date        date         not null,
    blocked_user_id   uuid         not null,
    blocked_user_name varchar(255) not null,
    deleted_date      date,
    is_deleted        boolean      not null,
    target_user_id    uuid         not null
);

alter table public.blacklist
    owner to postgres;

create table public.friend
(
    id             uuid         not null
        primary key,
    added_date     date         not null,
    added_user_id  uuid         not null,
    deleted_date   date,
    friend_name    varchar(255) not null,
    is_deleted     boolean      not null,
    target_user_id uuid         not null
);

alter table public.friend
    owner to postgres;