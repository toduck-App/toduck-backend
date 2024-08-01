USE toduck;

CREATE TABLE users
(
    -- TODO: 3. users 테이블  oauth 사용 시 필요한 사용자 식별값 → oauth 에서 전화번호 수집이 가능한지 다시 확인 필요
    id           BIGINT PRIMARY KEY auto_increment,
    nickname     VARCHAR(100)                               NOT NULL,
    phone_number VARCHAR(50)                                NOT NULL,
    password     VARCHAR(255)                               NULL,
    role         ENUM ('ADMIN', 'USER')                     NULL,
    provider     ENUM ('KAKAO', 'NAVER', 'GOOGLE', 'APPLE') NULL,
    created_at   DATETIME                                   NOT NULL,
    updated_at   DATETIME                                   NOT NULL,
    deleted_at   DATETIME                                   NULL
);

CREATE TABLE social
(
    id           BIGINT PRIMARY KEY auto_increment,
    user_id      BIGINT                             NOT NULL,
    content      VARCHAR(255)                       NOT NULL,
    tag          ENUM ('COMMUNICATION', 'QUESTION') NOT NULL,
    is_anonymous BOOLEAN                            NOT NULL,
    created_at   DATETIME                           NOT NULL,
    updated_at   DATETIME                           NOT NULL,
    deleted_at   DATETIME                           NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE routine
(
    id          BIGINT PRIMARY KEY auto_increment,
    social_id   BIGINT                         NOT NULL,
    user_id     BIGINT                         NOT NULL,
    -- TODO: 8. routine 의 이모지 필드 enum 값 필요
    emoji       ENUM ('SMILE')                 NOT NULL,
    -- TODO: 4. routine 테이블의 color enum 값 필요
    color       ENUM ('RED')                   NOT NULL,
    is_complete BOOLEAN                        NOT NULL,
    time        TIME                           NOT NULL,
    title       CHAR(100)                      NOT NULL,
    location    VARCHAR(255)                   NOT NULL,
    is_public   BOOLEAN                        NOT NULL,
    alarm       ENUM ('10', '30', '60', 'OFF') NOT NULL,
    memo        TEXT                           NULL,
    created_at  DATETIME                       NOT NULL,
    updated_at  DATETIME                       NOT NULL,
    deleted_at  DATETIME                       NULL,
    FOREIGN KEY (social_id) REFERENCES social (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE comment
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT   NOT NULL,
    social_id  BIGINT   NOT NULL,
    content    TEXT     NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted_at DATETIME NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (social_id) REFERENCES social (id)
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

CREATE TABLE schedule
(
    id          BIGINT PRIMARY KEY auto_increment,
    user_id     BIGINT         NOT NULL,
    -- TODO: 8. routine 의 이모지 필드 enum 값 필요
    emoji       ENUM ('SMILE') NOT NULL,
    -- TODO: 4. routine 테이블의 color enum 값 필요
    color       ENUM ('RED')   NOT NULL,
    is_complete BOOLEAN        NOT NULL,
    time        TIME           NULL,
    title       CHAR(100)      NOT NULL,
    location    VARCHAR(255)   NOT NULL,
    routine_day DATE           NULL,
    alarm       ENUM ('10', '30', '60', 'OFF')   NOT NULL,
    memo        CHAR(255)      NOT NULL,
    created_at  DATETIME       NOT NULL,
    updated_at  DATETIME       NOT NULL,
    deleted_at  DATETIME       NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
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

CREATE TABLE repeat_day
(
    id         BIGINT PRIMARY KEY auto_increment,
    day        ENUM ('MON','TUE','WED','THU','FRI','SAT','SUN') NOT NULL,
    created_at DATETIME                                         NOT NULL,
    updated_at DATETIME                                         NOT NULL,
    deleted_at DATETIME                                         NULL
);

CREATE TABLE repeat_day_link
(
    id            BIGINT PRIMARY KEY auto_increment,
    routine_id    BIGINT   NULL,
    schedule_id   BIGINT   NULL,
    repeat_day_id BIGINT   NOT NULL,
    created_at    DATETIME NOT NULL,
    updated_at    DATETIME NOT NULL,
    deleted_at    DATETIME NULL,
    FOREIGN KEY (routine_id) REFERENCES routine (id),
    FOREIGN KEY (schedule_id) REFERENCES schedule (id),
    FOREIGN KEY (repeat_day_id) REFERENCES repeat_day (id)
);

INSERT INTO social_category (name, created_at, updated_at)
VALUES ('CONCENTRATION', NOW(), NOW()),
       ('MEMORY', NOW(), NOW()),
       ('IMPULSE', NOW(), NOW()),
       ('ANXIETY', NOW(), NOW()),
       ('SLEEP', NOW(), NOW());
