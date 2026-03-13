
    create table category (
        is_delete bit not null,
        create_at datetime(6),
        id bigint not null auto_increment,
        update_at datetime(6),
        name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table company (
        is_delete bit not null,
        create_at datetime(6),
        id bigint not null auto_increment,
        update_at datetime(6),
        company_address varchar(255),
        name varchar(255) not null,
        pan_no varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table custom_unit (
        is_delete bit not null,
        create_at datetime(6),
        id bigint not null auto_increment,
        update_at datetime(6),
        unit_name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table customer_category (
        category_id bigint not null,
        customer_id bigint not null,
        primary key (category_id, customer_id)
    ) engine=InnoDB;

    create table customer_info (
        is_delete bit not null,
        create_at datetime(6),
        id bigint not null auto_increment,
        update_at datetime(6),
        address varchar(255),
        contact varchar(255) not null,
        name varchar(255) not null,
        pan_no varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table refresh_tokens (
        expiry_date datetime(6),
        id bigint not null auto_increment,
        user_id bigint not null,
        token_hash varchar(512) not null,
        id_address varchar(255),
        user_agent varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table report (
        balance decimal(19,4),
        end_date date,
        is_delete bit not null,
        start_date date,
        total_credit decimal(19,4),
        total_debit decimal(19,4),
        category_id bigint,
        create_at datetime(6),
        customer_id bigint,
        id bigint not null auto_increment,
        update_at datetime(6),
        primary key (id)
    ) engine=InnoDB;

    create table roles (
        id bigint not null auto_increment,
        role enum ('ADMIN','AUDITOR','SUPERADMIN') not null,
        primary key (id)
    ) engine=InnoDB;

    create table transaction_info (
        due_amount decimal(19,4),
        is_delete bit not null,
        quantity decimal(19,4),
        total_amount decimal(19,4),
        unit_amount decimal(19,4),
        company_id bigint not null,
        create_at datetime(6),
        customer_id bigint not null,
        id bigint not null auto_increment,
        transaction_date datetime(6),
        unit_id bigint,
        update_at datetime(6),
        description varchar(255),
        purchase_or_sale enum ('PURCHASE','SALE'),
        primary key (id)
    ) engine=InnoDB;

    create table user_roles (
        role_id bigint not null,
        user_id bigint not null,
        primary key (role_id, user_id)
    ) engine=InnoDB;

    create table users (
        is_email_verified bit not null,
        user_id bigint not null auto_increment,
        email varchar(255) not null,
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (user_id)
    ) engine=InnoDB;

    create table verification_token (
        expiry_date datetime(6) not null,
        id bigint not null auto_increment,
        user_id bigint not null,
        token varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    alter table category 
       add constraint UK46ccwnsi9409t36lurvtyljak unique (name);

    alter table custom_unit 
       add constraint UK2p1jyv4xnet6vro26qj0q65my unique (unit_name);

    create index idx_customer_contact 
       on customer_info (contact);

    alter table roles 
       add constraint UKg50w4r0ru3g9uf6i6fr4kpro8 unique (role);

    create index idx_transaction_customer 
       on transaction_info (customer_id);

    create index idx_transaction_date 
       on transaction_info (transaction_date);

    alter table users 
       add constraint UKr43af9ap4edm43mmtq01oddj6 unique (username);

    alter table users 
       add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);

    alter table verification_token 
       add constraint UKq6jibbenp7o9v6tq178xg88hg unique (user_id);

    alter table verification_token 
       add constraint UKp678btf3r9yu6u8aevyb4ff0m unique (token);

    alter table customer_category 
       add constraint FKd3y8stblsin7j38kx8xt4v8qf 
       foreign key (category_id) 
       references category (id);

    alter table customer_category 
       add constraint FKa8r7yjsb2y5330fsgabhgfvmw 
       foreign key (customer_id) 
       references customer_info (id);

    alter table refresh_tokens 
       add constraint FK1lih5y2npsf8u5o3vhdb9y0os 
       foreign key (user_id) 
       references users (user_id);

    alter table report 
       add constraint FK6cpsxw6txn4u2hf9rihmg9l0u 
       foreign key (category_id) 
       references category (id);

    alter table report 
       add constraint FKiyljbi6sutr47ys2r2vuf5pii 
       foreign key (customer_id) 
       references customer_info (id);

    alter table transaction_info 
       add constraint FKbnx3l07v23h0jklmctpbojbex 
       foreign key (company_id) 
       references company (id);

    alter table transaction_info 
       add constraint FKbjs329m1y2f8llo6sj8x659wq 
       foreign key (customer_id) 
       references customer_info (id);

    alter table transaction_info 
       add constraint FKqe9g3x2flj4gr621u7nvcniwg 
       foreign key (unit_id) 
       references custom_unit (id);

    alter table user_roles 
       add constraint FKh8ciramu9cc9q3qcqiv4ue8a6 
       foreign key (role_id) 
       references roles (id);

    alter table user_roles 
       add constraint FKhfh9dx7w3ubf1co1vdev94g3f 
       foreign key (user_id) 
       references users (user_id);

    alter table verification_token 
       add constraint FK3asw9wnv76uxu3kr1ekq4i1ld 
       foreign key (user_id) 
       references users (user_id);
