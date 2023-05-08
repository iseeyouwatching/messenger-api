create table public.notification
(
    id           uuid not null
        primary key,
    read_time    timestamp,
    receive_time timestamp,
    status       integer,
    text         varchar(255),
    type         integer,
    user_id      uuid
);

alter table public.notification
    owner to hits;