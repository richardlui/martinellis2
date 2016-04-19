CREATE TABLE db.reply
(
    reply_id        INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    topic_id        integer  not null,
    comment         varchar(4000) not null,
    by_user_id      integer not null,
    last_updated    datetime(0) DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE db.topic
(
    topic_id        INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    by_user_id      integer not null,
    last_updated    datetime(0) DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE db.user
(
    user_id         INTEGER UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE,
    password        varchar(1000),
    first_name      varchar(100),
    last_name       varchar(100),
    email           varchar(100),
    login_attempt   smallint,
    last_login      datetime(0) DEFAULT CURRENT_TIMESTAMP,
    last_updated    datetime(0) DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);