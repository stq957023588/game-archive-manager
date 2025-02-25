create table game_archive
(
    id                  int unsigned auto_increment primary key,
    game_name           varchar(32)                         not null,
    md5                 varchar(128)                        not null,
    uid                 varchar(32)                         not null,
    archive_path        varchar(256)                        not null,
    archive_file_status tinyint   default 0                 not null,
    save_time           timestamp                           not null comment '保存时间',
    upload_time         timestamp default CURRENT_TIMESTAMP not null comment '上传时间'
);