-----------
-- TABLE --
-----------
CREATE TABLE settings
(
    user_id         varchar(100) not null,
    namespace       varchar(200) null,
    skey            varchar(200) not null,
    value           varchar(2000) not null
);

CREATE TABLE reply
(
    reply_id        INTEGER NOT NULL IDENTITY,
    topic_id       integer  not null,
    comment         varchar(4000) not null,
    by_user_id      integer not null,
    last_updated    datetime(0) DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user
(
    user_id         INTEGER NOT NULL IDENTITY,
    password        varchar(1000),
    first_name      varchar(100),
    last_name       varchar(100),
    email           varchar(100),
    login_attempt   smallint,
    last_login      datetime(0) DEFAULT CURRENT_TIMESTAMP,
    last_updated    datetime(0) DEFAULT CURRENT_TIMESTAMP
);