create table game_archive
(
    id                  integer primary key autoincrement,
    game_name           varchar(32)                                      not null,
    md5                 varchar(128)                                     not null,
    uid                 INTEGER                                          not null,
    archive_path        varchar(256)                                     not null,
    archive_file_status integer   default 0                              not null,
    save_time           timestamp                                        not null,
    upload_time         timestamp default (datetime('now', 'localtime')) not null
);

create table account
(
    id          integer
        primary key autoincrement,
    username    text                                not null
        constraint uk_username
            unique,
    password    text                                not null,
    email       text,
    create_time timestamp default CURRENT_TIMESTAMP not null
);

create table system_setting
(
    id          integer
        primary key autoincrement,
    code        integer                             not null
        constraint uk_code
            unique,
    name        text                                not null,
    description text,
    value       text,
    create_time timestamp default CURRENT_TIMESTAMP not null
);