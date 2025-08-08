USE toduck;

CREATE TABLE users
(
    -- TODO: 3. users 테이블  oauth 사용 시 필요한 사용자 식별값 → oauth 에서 전화번호 수집이 가능한지 다시 확인 필요
    id           BIGINT PRIMARY KEY auto_increment,
    nickname     VARCHAR(100)                               NULL,
    phone_number VARCHAR(50)                                NULL,
    login_id     VARCHAR(100)                               NULL,
    password     VARCHAR(255)                               NULL,
    email        VARCHAR(100)                               NULL,
    image_url    VARCHAR(1024)                              NULL,
    role         ENUM ('ADMIN', 'USER')                     NOT NULL,
    provider     ENUM ('KAKAO', 'NAVER', 'GOOGLE', 'APPLE') NULL,
    created_at   DATETIME                                   NOT NULL,
    updated_at   DATETIME                                   NOT NULL,
    deleted_at   DATETIME                                   NULL,
    CONSTRAINT users_nickname_unique UNIQUE (nickname)
);

CREATE TABLE routine
(
    id               BIGINT PRIMARY KEY auto_increment,
    user_id          BIGINT                                              NOT NULL,
    category     ENUM ('COMPUTER', 'FOOD', 'PENCIL', 'RED_BOOK', 'YELLOW_BOOK', 'SLEEP', 'POWER', 'PEOPLE', 'MEDICINE', 'TALK', 'HEART', 'VEHICLE', 'NONE') DEFAULT NULL,
-- TODO: 4. routine 테이블의 color enum 값 필요
    color            VARCHAR(10)                                         NULL,
    time             TIME                                                NULL,
    days_of_week     TINYINT UNSIGNED                                    NOT NULL,
    title            CHAR(100)                                           NOT NULL,
    is_public        BOOLEAN                                             NOT NULL,
    reminder_minutes INT UNSIGNED                                        NULL,
    memo             TEXT                                                NULL,
    schedule_modified_at DATETIME                                        NOT NULL,
    shared_count     INT UNSIGNED       DEFAULT 0                        NOT NULL,
    created_at       DATETIME                                            NOT NULL,
    updated_at       DATETIME                                            NOT NULL,
    deleted_at       DATETIME                                            NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE social
(
    id           BIGINT PRIMARY KEY auto_increment,
    user_id      BIGINT       NOT NULL,
    routine_id   BIGINT       NULL,
    title        VARCHAR(100) NULL,
    content      VARCHAR(500) NOT NULL,
    is_anonymous BOOLEAN      NOT NULL,
    like_count   int          NOT NULL DEFAULT 0,
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NOT NULL,
    deleted_at   DATETIME     NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (routine_id) REFERENCES routine (id)
);

CREATE TABLE routine_record
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    routine_id   BIGINT   NULL,
    record_at    DATETIME NOT NULL,
    is_all_day   BOOLEAN  NOT NULL,
    is_completed BOOLEAN  NOT NULL DEFAULT false,
    created_at   DATETIME NOT NULL,
    updated_at   DATETIME NOT NULL,
    deleted_at   DATETIME NULL,
    FOREIGN KEY (routine_id) REFERENCES routine (id) ON DELETE SET NULL
);

CREATE TABLE comment
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT   NOT NULL,
    social_id  BIGINT   NOT NULL,
    parent_id  BIGINT   NULL,
    content    TEXT     NOT NULL,
    like_count int      NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted_at DATETIME NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (social_id) REFERENCES social (id),
    FOREIGN KEY (parent_id) REFERENCES comment (id)
);

CREATE TABLE comment_likes
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT   NOT NULL,
    comment_id BIGINT   NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted_at DATETIME NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (comment_id) REFERENCES comment (id)
);

CREATE TABLE comment_image_file
(
    id         BIGINT PRIMARY KEY auto_increment,
    comment_id BIGINT        NOT NULL,
    url        VARCHAR(1024) NOT NULL,
    created_at DATETIME      NOT NULL,
    updated_at DATETIME      NOT NULL,
    deleted_at DATETIME      NULL,
    FOREIGN KEY (comment_id) REFERENCES comment (id) ON DELETE CASCADE
);

CREATE TABLE likes
(
    id         BIGINT PRIMARY KEY auto_increment,
    user_id    BIGINT   NOT NULL,
    social_id  BIGINT   NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted_at DATETIME NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (social_id) REFERENCES social (id)
);

CREATE TABLE shared_routine
(
    id         BIGINT PRIMARY KEY auto_increment,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted_at DATETIME NULL,
    user_id    BIGINT   NOT NULL,
    social_id  BIGINT   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (social_id) REFERENCES routine (id)
);

CREATE TABLE social_category
(
    id         BIGINT PRIMARY KEY auto_increment,
    name       VARCHAR(20) NOT NULL,
    created_at DATETIME    NOT NULL,
    updated_at DATETIME    NOT NULL,
    deleted_at DATETIME    NULL
);

CREATE TABLE social_image_file
(
    id         BIGINT PRIMARY KEY auto_increment,
    social_id  BIGINT        NOT NULL,
    url        VARCHAR(1024) NOT NULL,
    created_at DATETIME      NOT NULL,
    updated_at DATETIME      NOT NULL,
    deleted_at DATETIME      NULL,
    FOREIGN KEY (social_id) REFERENCES social (id)
);

-- Schedule 테이블
CREATE TABLE schedule
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    title        VARCHAR(100) NOT NULL,
    category     ENUM ('COMPUTER', 'FOOD', 'PENCIL', 'RED_BOOK', 'YELLOW_BOOK', 'SLEEP', 'POWER', 'PEOPLE', 'MEDICINE', 'TALK', 'HEART', 'VEHICLE', 'NONE') DEFAULT NULL,
    color        VARCHAR(100)                                                                                                                               DEFAULT NULL,
    start_date   DATE         NOT NULL,
    end_date     DATE         NOT NULL,
    is_all_day   BOOLEAN      NOT NULL,
    time         TIME                                                                                                                                       DEFAULT NULL,
    days_of_week TINYINT                                                                                                                                    DEFAULT NULL,
    alarm        ENUM ('TEN_MINUTE', 'THIRTY_MINUTE', 'ONE_DAY')                                                                                                 DEFAULT NULL,
    location     VARCHAR(255)                                                                                                                               DEFAULT NULL,
    memo         VARCHAR(255)                                                                                                                               DEFAULT NULL,
    user_id      BIGINT       NOT NULL,
    created_at   DATETIME     NOT NULL,
    updated_at   DATETIME     NOT NULL,
    deleted_at   DATETIME                                                                                                                                   DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);


CREATE TABLE schedule_record
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    is_completed BOOLEAN  NOT NULL,
    record_date  DATE     NOT NULL,
    schedule_id  BIGINT   NOT NULL,
    created_at   DATETIME NOT NULL,
    updated_at   DATETIME NOT NULL,
    deleted_at   DATETIME NULL,
    FOREIGN KEY (schedule_id) REFERENCES schedule (id) ON DELETE CASCADE
);


CREATE TABLE record
(
    id         BIGINT PRIMARY KEY auto_increment,
    user_id    BIGINT                                                     NOT NULL,
    time       TIME                                                       NOT NULL,
    diary      VARCHAR(2048)                                              NULL,
    emotion    ENUM ('HAPPY', 'CALM', 'SAD', 'ANGRY', 'ANXIOUS', 'TIRED') NOT NULL,
    created_at DATETIME                                                   NOT NULL,
    updated_at DATETIME                                                   NOT NULL,
    deleted_at DATETIME                                                   NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE follow
(
    id          BIGINT PRIMARY KEY auto_increment,
    created_at  DATETIME NOT NULL,
    updated_at  DATETIME NOT NULL,
    deleted_at  DATETIME NULL,
    followed_id BIGINT   NOT NULL,
    follower_id BIGINT   NOT NULL,
    FOREIGN KEY (followed_id) REFERENCES users (id),
    FOREIGN KEY (follower_id) REFERENCES users (id)
);

CREATE TABLE social_category_link
(
    id                 BIGINT PRIMARY KEY auto_increment,
    created_at         DATETIME NOT NULL,
    updated_at         DATETIME NOT NULL,
    deleted_at         DATETIME NULL,
    social_id          BIGINT   NOT NULL,
    social_category_id BIGINT   NOT NULL,
    FOREIGN KEY (social_id) REFERENCES social (id),
    FOREIGN KEY (social_category_id) REFERENCES social_category (id)
);

INSERT INTO social_category (name, created_at, updated_at)
VALUES ('CONCENTRATION', NOW(), NOW()),
       ('MEMORY', NOW(), NOW()),
       ('IMPULSE', NOW(), NOW()),
       ('ANXIETY', NOW(), NOW()),
       ('SLEEP', NOW(), NOW()),
       ('GENERAL', NOW(), NOW());

CREATE TABLE block
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    blocker_id BIGINT   NOT NULL,
    blocked_id BIGINT   NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted_at DATETIME NULL,
    FOREIGN KEY (blocker_id) REFERENCES users (id),
    FOREIGN KEY (blocked_id) REFERENCES users (id)
);

CREATE TABLE social_report
(
    id          BIGINT PRIMARY KEY auto_increment,
    user_id     BIGINT                                                                                                        NOT NULL,
    social_id   BIGINT                                                                                                        NOT NULL,
    report_type ENUM ('NOT_RELATED_TO_SERVICE', 'PRIVACY_RISK', 'COMMERCIAL_ADVERTISEMENT', 'INAPPROPRIATE_CONTENT', 'OTHER') NOT NULL,
    reason      VARCHAR(255)                                                                                                  NULL,
    created_at  DATETIME                                                                                                      NOT NULL,
    updated_at  DATETIME                                                                                                      NOT NULL,
    deleted_at  DATETIME                                                                                                      NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (social_id) REFERENCES social (id)
);

CREATE TABLE comment_report
(
    id           BIGINT PRIMARY KEY auto_increment,
    user_id      BIGINT                                                                                                        NOT NULL,
    comment_id   BIGINT                                                                                                        NOT NULL,
    report_type  ENUM ('NOT_RELATED_TO_SERVICE', 'PRIVACY_RISK', 'COMMERCIAL_ADVERTISEMENT', 'INAPPROPRIATE_CONTENT', 'OTHER') NOT NULL,
    reason       VARCHAR(255)                                                                                                  NULL,
    created_at   DATETIME                                                                                                      NOT NULL,
    updated_at   DATETIME                                                                                                      NOT NULL,
    deleted_at   DATETIME                                                                                                      NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (comment_id) REFERENCES comment (id)
);

CREATE TABLE diary
(
    id          BIGINT PRIMARY KEY auto_increment,
    user_id     BIGINT                                                                              NOT NULL,
    diary_date  DATE                                                                                NOT NULL,
    emotion     ENUM ('HAPPY', 'GOOD', 'SAD', 'ANGRY', 'ANXIOUS', 'TIRED', 'SICK', 'SOSO', 'LOVE')  NOT NULL,
    title       VARCHAR(50)                                                                         NULL,
    memo        VARCHAR(2048)                                                                       NULL,
    created_at  DATETIME                                                                            NOT NULL,
    updated_at  DATETIME                                                                            NOT NULL,
    deleted_at  DATETIME                                                                            NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE diary_image_file
(
    id          BIGINT PRIMARY KEY auto_increment,
    diary_id    BIGINT                              NOT NULL,
    url         VARCHAR(512)                        NOT NULL,
    created_at  DATETIME                            NOT NULL,
    updated_at  DATETIME                            NOT NULL,
    deleted_at  DATETIME                            NULL,
    FOREIGN KEY (diary_id) REFERENCES diary (id) ON DELETE CASCADE
);

CREATE TABLE concentration
(
    id                  BIGINT PRIMARY KEY auto_increment,
    user_id             BIGINT                              NOT NULL,
    concentration_date  DATE                                NOT NULL,
    target_count        INT                                 NOT NULL,
    setting_count       INT                                 NOT NULL,
    concentration_time  INT                                 NOT NULL,
    created_at          DATETIME                            NOT NULL,
    updated_at          DATETIME                            NOT NULL,
    deleted_at          DATETIME                            NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE account_deletion_log
(
    id          BIGINT PRIMARY KEY auto_increment,
    user_id     BIGINT                                                                                      NOT NULL,
    reason_code ENUM ('HARD_TO_USE', 'NO_FEATURES', 'MANY_ERRORS', 'BETTER_APP', 'SWITCH_ACCOUNT', 'OTHER') NOT NULL,
    reason_text VARCHAR(130)                                                                                NOT NULL,
    created_at  DATETIME                                                                                    NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE device_token (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              user_id BIGINT NOT NULL,
                              token VARCHAR(512) NOT NULL,
                              device_type ENUM('IOS') NOT NULL,
                              created_at DATETIME NOT NULL,
                              updated_at DATETIME NOT NULL,
                              deleted_at DATETIME DEFAULT NULL,
                              FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE notification_setting (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      user_id BIGINT NOT NULL,
                                      all_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                      notification_method ENUM('SOUND_ONLY', 'VIBRATION_ONLY') NOT NULL DEFAULT 'SOUND_ONLY',
                                      notice_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                      home_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                      concentration_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                      diary_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                      social_enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                      created_at DATETIME NOT NULL,
                                      updated_at DATETIME NOT NULL,
                                      deleted_at DATETIME DEFAULT NULL,
                                      FOREIGN KEY (user_id) REFERENCES users (id),
                                      UNIQUE KEY notification_setting_user_id_unique (user_id)
);

CREATE TABLE notification (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              user_id BIGINT NOT NULL,
                              sender_id BIGINT DEFAULT NULL,
                              type ENUM('COMMENT', 'REPLY', 'REPLY_ON_MY_POST', 'LIKE_POST', 'LIKE_COMMENT', 'FOLLOW',
             'SCHEDULE_REMINDER', 'ROUTINE_REMINDER', 'DIARY_REMINDER', 'INACTIVITY_REMINDER',
             'ROUTINE_SHARE_MILESTONE') NOT NULL,
                              in_app_title VARCHAR(100) NOT NULL,
                              in_app_body VARCHAR(500) NOT NULL,
                              push_title VARCHAR(100) NOT NULL,
                              push_body VARCHAR(500) NOT NULL,
                              action_url VARCHAR(1024) DEFAULT NULL,
                              data JSON DEFAULT NULL,
                              is_read BOOLEAN NOT NULL DEFAULT FALSE,
                              is_in_app_shown BOOLEAN NOT NULL DEFAULT FALSE,
                              is_sent BOOLEAN NOT NULL DEFAULT FALSE,
                              created_at DATETIME NOT NULL,
                              updated_at DATETIME NOT NULL,
                              deleted_at DATETIME DEFAULT NULL,
                              FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE qrtz_job_details (
                                  sched_name VARCHAR(120) NOT NULL,
                                  job_name VARCHAR(190) NOT NULL,
                                  job_group VARCHAR(190) NOT NULL,
                                  description VARCHAR(250) NULL,
                                  job_class_name VARCHAR(250) NOT NULL,
                                  is_durable VARCHAR(1) NOT NULL,
                                  is_nonconcurrent VARCHAR(1) NOT NULL,
                                  is_update_data VARCHAR(1) NOT NULL,
                                  requests_recovery VARCHAR(1) NOT NULL,
                                  job_data BLOB NULL,
                                  PRIMARY KEY (sched_name, job_name, job_group)
) ENGINE=InnoDB;

CREATE TABLE qrtz_triggers (
                               sched_name VARCHAR(120) NOT NULL,
                               trigger_name VARCHAR(190) NOT NULL,
                               trigger_group VARCHAR(190) NOT NULL,
                               job_name VARCHAR(190) NOT NULL,
                               job_group VARCHAR(190) NOT NULL,
                               description VARCHAR(250) NULL,
                               next_fire_time BIGINT(13) NULL,
                               prev_fire_time BIGINT(13) NULL,
                               priority INTEGER NULL,
                               trigger_state VARCHAR(16) NOT NULL,
                               trigger_type VARCHAR(8) NOT NULL,
                               start_time BIGINT(13) NOT NULL,
                               end_time BIGINT(13) NULL,
                               calendar_name VARCHAR(190) NULL,
                               misfire_instr SMALLINT(2) NULL,
                               job_data BLOB NULL,
                               PRIMARY KEY (sched_name, trigger_name, trigger_group),
                               FOREIGN KEY (sched_name, job_name, job_group)
                                   REFERENCES qrtz_job_details(sched_name, job_name, job_group)
) ENGINE=InnoDB;

CREATE TABLE qrtz_simple_triggers (
                                      sched_name VARCHAR(120) NOT NULL,
                                      trigger_name VARCHAR(190) NOT NULL,
                                      trigger_group VARCHAR(190) NOT NULL,
                                      repeat_count BIGINT(7) NOT NULL,
                                      repeat_interval BIGINT(12) NOT NULL,
                                      times_triggered BIGINT(10) NOT NULL,
                                      PRIMARY KEY (sched_name, trigger_name, trigger_group),
                                      FOREIGN KEY (sched_name, trigger_name, trigger_group)
                                          REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group)
) ENGINE=InnoDB;

CREATE TABLE qrtz_cron_triggers (
                                    sched_name VARCHAR(120) NOT NULL,
                                    trigger_name VARCHAR(190) NOT NULL,
                                    trigger_group VARCHAR(190) NOT NULL,
                                    cron_expression VARCHAR(120) NOT NULL,
                                    time_zone_id VARCHAR(80),
                                    PRIMARY KEY (sched_name, trigger_name, trigger_group),
                                    FOREIGN KEY (sched_name, trigger_name, trigger_group)
                                        REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group)
) ENGINE=InnoDB;

CREATE TABLE qrtz_simprop_triggers (
                                       sched_name VARCHAR(120) NOT NULL,
                                       trigger_name VARCHAR(190) NOT NULL,
                                       trigger_group VARCHAR(190) NOT NULL,
                                       str_prop_1 VARCHAR(512) NULL,
                                       str_prop_2 VARCHAR(512) NULL,
                                       str_prop_3 VARCHAR(512) NULL,
                                       int_prop_1 INT NULL,
                                       int_prop_2 INT NULL,
                                       long_prop_1 BIGINT NULL,
                                       long_prop_2 BIGINT NULL,
                                       dec_prop_1 NUMERIC(13,4) NULL,
                                       dec_prop_2 NUMERIC(13,4) NULL,
                                       bool_prop_1 VARCHAR(1) NULL,
                                       bool_prop_2 VARCHAR(1) NULL,
                                       PRIMARY KEY (sched_name, trigger_name, trigger_group),
                                       FOREIGN KEY (sched_name, trigger_name, trigger_group)
                                           REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group)
) ENGINE=InnoDB;

CREATE TABLE qrtz_blob_triggers (
                                    sched_name VARCHAR(120) NOT NULL,
                                    trigger_name VARCHAR(190) NOT NULL,
                                    trigger_group VARCHAR(190) NOT NULL,
                                    blob_data BLOB NULL,
                                    PRIMARY KEY (sched_name, trigger_name, trigger_group),
                                    INDEX (sched_name, trigger_name, trigger_group),
                                    FOREIGN KEY (sched_name, trigger_name, trigger_group)
                                        REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group)
) ENGINE=InnoDB;

CREATE TABLE qrtz_calendars (
                                sched_name VARCHAR(120) NOT NULL,
                                calendar_name VARCHAR(190) NOT NULL,
                                calendar BLOB NOT NULL,
                                PRIMARY KEY (sched_name, calendar_name)
) ENGINE=InnoDB;

CREATE TABLE qrtz_paused_trigger_grps (
                                          sched_name VARCHAR(120) NOT NULL,
                                          trigger_group VARCHAR(190) NOT NULL,
                                          PRIMARY KEY (sched_name, trigger_group)
) ENGINE=InnoDB;

CREATE TABLE qrtz_fired_triggers (
                                     sched_name VARCHAR(120) NOT NULL,
                                     entry_id VARCHAR(95) NOT NULL,
                                     trigger_name VARCHAR(190) NOT NULL,
                                     trigger_group VARCHAR(190) NOT NULL,
                                     instance_name VARCHAR(190) NOT NULL,
                                     fired_time BIGINT(13) NOT NULL,
                                     sched_time BIGINT(13) NOT NULL,
                                     priority INTEGER NOT NULL,
                                     state VARCHAR(16) NOT NULL,
                                     job_name VARCHAR(190) NULL,
                                     job_group VARCHAR(190) NULL,
                                     is_nonconcurrent VARCHAR(1) NULL,
                                     requests_recovery VARCHAR(1) NULL,
                                     PRIMARY KEY (sched_name, entry_id)
) ENGINE=InnoDB;

CREATE TABLE qrtz_scheduler_state (
                                      sched_name VARCHAR(120) NOT NULL,
                                      instance_name VARCHAR(190) NOT NULL,
                                      last_checkin_time BIGINT(13) NOT NULL,
                                      checkin_interval BIGINT(13) NOT NULL,
                                      PRIMARY KEY (sched_name, instance_name)
) ENGINE=InnoDB;

CREATE TABLE qrtz_locks (
                            sched_name VARCHAR(120) NOT NULL,
                            lock_name VARCHAR(40) NOT NULL,
                            PRIMARY KEY (sched_name, lock_name)
) ENGINE=InnoDB;

-- 인덱스 생성
CREATE INDEX idx_qrtz_j_req_recovery ON qrtz_job_details(sched_name, requests_recovery);
CREATE INDEX idx_qrtz_j_grp ON qrtz_job_details(sched_name, job_group);

CREATE INDEX idx_qrtz_t_j ON qrtz_triggers(sched_name, job_name, job_group);
CREATE INDEX idx_qrtz_t_jg ON qrtz_triggers(sched_name, job_group);
CREATE INDEX idx_qrtz_t_c ON qrtz_triggers(sched_name, calendar_name);
CREATE INDEX idx_qrtz_t_g ON qrtz_triggers(sched_name, trigger_group);
CREATE INDEX idx_qrtz_t_state ON qrtz_triggers(sched_name, trigger_state);
CREATE INDEX idx_qrtz_t_n_state ON qrtz_triggers(sched_name, trigger_name, trigger_group, trigger_state);
CREATE INDEX idx_qrtz_t_n_g_state ON qrtz_triggers(sched_name, trigger_group, trigger_state);
CREATE INDEX idx_qrtz_t_next_fire_time ON qrtz_triggers(sched_name, next_fire_time);
CREATE INDEX idx_qrtz_t_nft_st ON qrtz_triggers(sched_name, trigger_state, next_fire_time);
CREATE INDEX idx_qrtz_t_nft_misfire ON qrtz_triggers(sched_name, misfire_instr, next_fire_time);
CREATE INDEX idx_qrtz_t_nft_st_misfire ON qrtz_triggers(sched_name, misfire_instr, next_fire_time, trigger_state);
CREATE INDEX idx_qrtz_t_nft_st_misfire_grp ON qrtz_triggers(sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);

CREATE INDEX idx_qrtz_ft_trig_inst_name ON qrtz_fired_triggers(sched_name, instance_name);
CREATE INDEX idx_qrtz_ft_inst_job_req_rcvry ON qrtz_fired_triggers(sched_name, instance_name, requests_recovery);
CREATE INDEX idx_qrtz_ft_j_g ON qrtz_fired_triggers(sched_name, job_name, job_group);
CREATE INDEX idx_qrtz_ft_jg ON qrtz_fired_triggers(sched_name, job_group);
CREATE INDEX idx_qrtz_ft_t_g ON qrtz_fired_triggers(sched_name, trigger_name, trigger_group);
CREATE INDEX idx_qrtz_ft_tg ON qrtz_fired_triggers(sched_name, trigger_group);

-- 루틴 알림 작업 기록 테이블
CREATE TABLE routine_reminder_job (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      routine_id BIGINT NOT NULL,
                                      user_id BIGINT NOT NULL,
                                      reminder_date DATE NOT NULL,
                                      reminder_time TIME NOT NULL,
                                      job_key VARCHAR(255) NOT NULL,
                                      created_at DATETIME NOT NULL,
                                      updated_at DATETIME NOT NULL,
                                      deleted_at DATETIME NULL,
                                      FOREIGN KEY (routine_id) REFERENCES routine(id),
                                      FOREIGN KEY (user_id) REFERENCES users(id),
                                      UNIQUE KEY uk_routine_reminder_date_time (routine_id, reminder_date, reminder_time),
                                      INDEX idx_routine_reminder_date (reminder_date),
                                      INDEX idx_routine_reminder_job_key (job_key)
);
