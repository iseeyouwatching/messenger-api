create table public.chat
(
    id            uuid not null
        primary key,
    admin_id      uuid,
    avatar_id     uuid,
    chat_type     integer,
    creation_date date,
    name          varchar(255),
    receiver_id   uuid,
    sender_id     uuid
);

alter table public.chat
    owner to postgres;

create table public.chat_user
(
    id      uuid not null
        primary key,
    chat_id uuid,
    user_id uuid
);

alter table public.chat_user
    owner to postgres;

create table public.message
(
    id               uuid not null
        primary key,
    message_text     varchar(500),
    send_date        timestamp,
    sender_avatar_id uuid,
    sender_id        uuid,
    sender_name      varchar(255),
    chat_id          uuid
        constraint chat_id_constraint
            references public.chat
);

alter table public.message
    owner to postgres;

create table public.attachment
(
    id         uuid not null
        primary key,
    file_id    uuid,
    file_name  varchar(255),
    message_id uuid
        constraint message_id_constraint
            references public.message
);

alter table public.attachment
    owner to postgres;

