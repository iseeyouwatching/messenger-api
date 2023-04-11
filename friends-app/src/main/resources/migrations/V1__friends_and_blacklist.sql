create table public.blacklist
(
    id                uuid         not null
        primary key,
    added_date        timestamp    not null,
    blocked_user_id   uuid         not null,
    blocked_user_name varchar(255) not null,
    deleted_date      timestamp,
    target_user_id    uuid         not null
);

alter table public.blacklist
    owner to hits;

create table public.friend
(
    id             uuid         not null
        primary key,
    added_date     timestamp    not null,
    added_user_id  uuid         not null,
    deleted_date   timestamp,
    friend_name    varchar(255) not null,
    target_user_id uuid         not null
);

alter table public.friend
    owner to hits;

