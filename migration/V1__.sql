CREATE TABLE category
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime     NOT NULL,
    updated_at datetime     NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted_at datetime NULL,
    deleted_by VARCHAR(255) NULL,
    version    BIGINT NULL,
    name       VARCHAR(100) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE company
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    created_at      datetime     NOT NULL,
    updated_at      datetime     NOT NULL,
    created_by      VARCHAR(255) NOT NULL,
    updated_by      VARCHAR(255) NOT NULL,
    deleted_at      datetime NULL,
    deleted_by      VARCHAR(255) NULL,
    version         BIGINT NULL,
    name            VARCHAR(255) NOT NULL,
    pan_no          VARCHAR(255) NULL,
    phone_no        VARCHAR(255) NOT NULL,
    company_address VARCHAR(255) NULL,
    CONSTRAINT pk_company PRIMARY KEY (id)
);

CREATE TABLE custom_unit
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime     NOT NULL,
    updated_at datetime     NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    deleted_at datetime NULL,
    deleted_by VARCHAR(255) NULL,
    version    BIGINT NULL,
    unit_name  VARCHAR(255) NOT NULL,
    CONSTRAINT pk_custom_unit PRIMARY KEY (id)
);

CREATE TABLE customer_category
(
    category_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    CONSTRAINT pk_customer_category PRIMARY KEY (category_id, customer_id)
);

CREATE TABLE customer_entity
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    created_at         datetime     NOT NULL,
    updated_at         datetime     NOT NULL,
    created_by         VARCHAR(255) NOT NULL,
    updated_by         VARCHAR(255) NOT NULL,
    deleted_at         datetime NULL,
    deleted_by         VARCHAR(255) NULL,
    version            BIGINT NULL,
    user_id            BIGINT NULL,
    address            VARCHAR(255) NULL,
    pan_number         VARCHAR(255) NULL,
    status             VARCHAR(20)  NOT NULL,
    block_reason       VARCHAR(255) NULL,
    blocked_by         VARCHAR(255) NULL,
    blocked_at         datetime NULL,
    deactivated_reason VARCHAR(255) NULL,
    deactivated_by     VARCHAR(255) NULL,
    deactivated_at     datetime NULL,
    activated_reason   VARCHAR(255) NULL,
    activated_by       VARCHAR(255) NULL,
    activated_at       datetime NULL,
    restored_reason    VARCHAR(255) NULL,
    restored_by        VARCHAR(255) NULL,
    restored_at        datetime NULL,
    CONSTRAINT pk_customerentity PRIMARY KEY (id)
);

CREATE TABLE password_reset_otp
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    user_id     BIGINT   NOT NULL,
    expiry_date datetime NOT NULL,
    otp         VARCHAR(10) NULL,
    used        BIT(1)   NOT NULL,
    CONSTRAINT pk_password_reset_otp PRIMARY KEY (id)
);

CREATE TABLE refresh_tokens
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    created_at           datetime     NOT NULL,
    updated_at           datetime     NOT NULL,
    created_by           BIGINT       NOT NULL,
    updated_by           BIGINT       NOT NULL,
    deleted_at           datetime NULL,
    deleted_by           BIGINT NULL,
    version              BIGINT NULL,
    subject              VARCHAR(255) NOT NULL,
    token_hash           VARCHAR(88)  NOT NULL,
    jti                  VARCHAR(36) NULL,
    expiry_date          datetime     NOT NULL,
    revoked              BIT(1)       NOT NULL,
    ip_address           VARCHAR(45) NULL,
    last_token_issued_at datetime NULL,
    user_agent           VARCHAR(512) NULL,
    device_fingerprint   VARCHAR(255) NULL,
    last_used_at         datetime NULL,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id)
);

CREATE TABLE report
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    created_at   datetime     NOT NULL,
    updated_at   datetime     NOT NULL,
    created_by   VARCHAR(255) NOT NULL,
    updated_by   VARCHAR(255) NOT NULL,
    deleted_at   datetime NULL,
    deleted_by   VARCHAR(255) NULL,
    version      BIGINT NULL,
    start_date   date NULL,
    end_date     date NULL,
    total_credit DECIMAL(19, 4) NULL,
    total_debit  DECIMAL(19, 4) NULL,
    balance      DECIMAL(19, 4) NULL,
    customer_id  BIGINT NULL,
    category_id  BIGINT NULL,
    CONSTRAINT pk_report PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id     BIGINT AUTO_INCREMENT NOT NULL,
    `role` VARCHAR(255) NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE transaction
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    created_at       datetime     NOT NULL,
    updated_at       datetime     NOT NULL,
    created_by       VARCHAR(255) NOT NULL,
    updated_by       VARCHAR(255) NOT NULL,
    deleted_at       datetime NULL,
    deleted_by       VARCHAR(255) NULL,
    version          BIGINT NULL,
    customer_id      BIGINT       NOT NULL,
    company_id       BIGINT       NOT NULL,
    unit_amount      DECIMAL(19, 4) NULL,
    transaction_date datetime NULL,
    `description`    VARCHAR(255) NULL,
    due_amount       DECIMAL(19, 4) NULL,
    total_amount     DECIMAL(19, 4) NULL,
    quantity         DECIMAL(19, 4) NULL,
    purchase_or_sale VARCHAR(255) NULL,
    unit_id          BIGINT NULL,
    CONSTRAINT pk_transaction PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id)
);

CREATE TABLE users
(
    id                    BIGINT AUTO_INCREMENT NOT NULL,
    created_at            datetime     NOT NULL,
    updated_at            datetime     NOT NULL,
    created_by            BIGINT       NOT NULL,
    updated_by            BIGINT       NOT NULL,
    deleted_at            datetime NULL,
    deleted_by            BIGINT NULL,
    version               BIGINT NULL,
    first_name            VARCHAR(255) NOT NULL,
    last_name             VARCHAR(255) NOT NULL,
    password              VARCHAR(255) NOT NULL,
    email                 VARCHAR(255) NOT NULL,
    phone_number          VARCHAR(255) NOT NULL,
    email_verified        BIT(1)       NOT NULL,
    account_locked        BIT(1)       NOT NULL,
    last_login_at         datetime NULL,
    last_login_ip         VARCHAR(255) NULL,
    failed_login_attempts INT          NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_5b7dcd14a4faa2cdd54cf6eb8 UNIQUE (``);

ALTER TABLE users
    ADD CONSTRAINT uc_74165e195b2f7b25de690d14a UNIQUE (email);

ALTER TABLE category
    ADD CONSTRAINT uc_category_name UNIQUE (name);

ALTER TABLE company
    ADD CONSTRAINT uc_company_phoneno UNIQUE (phone_no);

ALTER TABLE custom_unit
    ADD CONSTRAINT uc_custom_unit_unit_name UNIQUE (unit_name);

ALTER TABLE customer_entity
    ADD CONSTRAINT uc_customerentity_pan_number UNIQUE (pan_number);

ALTER TABLE customer_entity
    ADD CONSTRAINT uc_customerentity_user UNIQUE (user_id);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT uc_refresh_tokens_jti UNIQUE (jti);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT uc_refresh_tokens_token_hash UNIQUE (token_hash);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_role UNIQUE (`role`);

ALTER TABLE users
    ADD CONSTRAINT uc_users_phonenumber UNIQUE (phone_number);

CREATE INDEX idx_refresh_token_expiry ON refresh_tokens (expiry_date);

CREATE UNIQUE INDEX idx_refresh_token_hash ON refresh_tokens (token_hash);

CREATE INDEX idx_refresh_token_revoked ON refresh_tokens (revoked);

CREATE INDEX idx_refresh_token_subject ON refresh_tokens (subject);

CREATE INDEX idx_transaction_date ON transaction (transaction_date);

ALTER TABLE customer_entity
    ADD CONSTRAINT FK_CUSTOMERENTITY_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE password_reset_otp
    ADD CONSTRAINT FK_PASSWORD_RESET_OTP_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE report
    ADD CONSTRAINT FK_REPORT_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

ALTER TABLE report
    ADD CONSTRAINT FK_REPORT_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customer_entity (id);

ALTER TABLE transaction
    ADD CONSTRAINT FK_TRANSACTION_ON_COMPANY FOREIGN KEY (company_id) REFERENCES company (id);

ALTER TABLE transaction
    ADD CONSTRAINT FK_TRANSACTION_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customer_entity (id);

CREATE INDEX idx_transaction_customer ON transaction (customer_id);

ALTER TABLE transaction
    ADD CONSTRAINT FK_TRANSACTION_ON_UNIT FOREIGN KEY (unit_id) REFERENCES custom_unit (id);

ALTER TABLE customer_category
    ADD CONSTRAINT fk_cuscat_on_category FOREIGN KEY (category_id) REFERENCES category (id);

ALTER TABLE customer_category
    ADD CONSTRAINT fk_cuscat_on_customer_entity FOREIGN KEY (customer_id) REFERENCES customer_entity (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role_entity FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);