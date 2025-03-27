USE toduck;

CREATE TABLE users
(
    -- TODO: 3. users 테이블  oauth 사용 시 필요한 사용자 식별값 → oauth 에서 전화번호 수집이 가능한지 다시 확인 필요
    id           BIGINT PRIMARY KEY auto_increment,
    nickname     VARCHAR(100)                               NOT NULL,
    phone_number VARCHAR(50)                                NULL,
    login_id     VARCHAR(100)                               NULL,
    password     VARCHAR(255)                               NULL,
    email        VARCHAR(100)                               NULL,
    role         ENUM ('ADMIN', 'USER')                     NOT NULL,
    provider     ENUM ('KAKAO', 'NAVER', 'GOOGLE', 'APPLE') NULL,
    created_at   DATETIME                                   NOT NULL,
    updated_at   DATETIME                                   NOT NULL,
    deleted_at   DATETIME                                   NULL
);

CREATE TABLE routine
(
    id               BIGINT PRIMARY KEY auto_increment,
    user_id          BIGINT                                              NOT NULL,
-- TODO: 8. routine 의 이모지 필드 enum 값 필요
    category         ENUM ('STUDY', 'EXERCISE', 'FOOD', 'SLEEP', 'PLAY') NULL,
-- TODO: 4. routine 테이블의 color enum 값 필요
    color            VARCHAR(10)                                         NULL,
    time             TIME                                                NULL,
    days_of_week     TINYINT UNSIGNED                                    NOT NULL,
    title            CHAR(100)                                           NOT NULL,
    is_public        BOOLEAN                                             NOT NULL,
    reminder_minutes INT UNSIGNED                                        NULL,
    memo             TEXT                                                NULL,
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
    content      VARCHAR(255) NOT NULL,
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
CREATE TABLE schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    category ENUM('COMPUTER', 'FOOD', 'PENCIL', 'RED_BOOK', 'YELLOW_BOOK', 'SLEEP', 'POWER', 'PEOPLE', 'MEDICINE', 'TALK', 'HEART', 'VEHICLE', 'NONE') DEFAULT NULL,
    color VARCHAR(100) DEFAULT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_all_day BOOLEAN NOT NULL,
    time TIME DEFAULT NULL,
    days_of_week TINYINT  DEFAULT NULL,
    alarm ENUM('TEN_MINUTE', 'ONE_HOUR', 'ONE_DAY') DEFAULT NULL,
    location VARCHAR(255) DEFAULT NULL,
    memo VARCHAR(255) DEFAULT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted_at DATETIME DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);


CREATE TABLE schedule_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    is_completed BOOLEAN NOT NULL,
    record_date DATE NOT NULL,
    schedule_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    deleted_at DATETIME NULL,
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
       ('SLEEP', NOW(), NOW());

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

CREATE TABLE report
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
