create table criteria (
       id bigint not null,
        deleted bit not null,
        name varchar(255) not null,
        description varchar(255) not null,
        publish_date datetime(6),
        reusable bit not null,
        FULLTEXT(name,description),
        primary key (id)
    ) engine=InnoDB;

    create table criterion_tags (
       criterion_id bigint not null,
        tag_id bigint not null
    ) engine=InnoDB;

    create table evaluation_ratings (
       evaluation_id bigint not null,
        rating_id bigint not null
    ) engine=InnoDB;

    create table evaluations (
       id bigint not null,
        comments varchar(255),
        completed bit not null,
        date datetime(6),
        deleted bit not null,
        evaluatee_id bigint,
        evaluator_id bigint,
        rubric_id bigint,
        task_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table hibernate_sequence (
       next_val bigint
    ) engine=InnoDB;

    insert into hibernate_sequence values ( 1 );

    insert into hibernate_sequence values ( 1 );

    insert into hibernate_sequence values ( 1 );

    insert into hibernate_sequence values ( 1 );

    insert into hibernate_sequence values ( 1 );

    insert into hibernate_sequence values ( 1 );

    insert into hibernate_sequence values ( 1 );

    create table ratings (
       id bigint not null,
        description varchar(255) not null,
        value double precision not null,
        criterion_id bigint,
        FULLTEXT(description),
        primary key (id)
    ) engine=InnoDB;

    create table rubric_crtieria (
       rubric_id bigint not null,
        criterion_id bigint not null
    ) engine=InnoDB;

    create table rubrics (
       id bigint not null,
        deleted bit not null,
        description varchar(255),
        public bit not null,
        name varchar(255) not null,
        obsolete bit not null,
        publish_date datetime(6),
        creator_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table tags (
       id bigint not null,
        count integer not null,
        value varchar(255) not null,
        FULLTEXT(value),
        primary key (id)
    ) engine=InnoDB;

    create table tasks (
       id bigint not null,
        due_date datetime(6),
        name varchar(255) not null,
        type varchar(255) not null,
        evaluator_id bigint,
        rubric_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table users (
       id bigint not null,
        cin varchar(255) not null,
        first_name varchar(255) not null,
        last_name varchar(255) not null,
        middle_name varchar(255),
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    alter table users 
       add constraint UK_ka6m8ghsr7vna1ti6lftwww8o unique (cin);

    alter table users 
       add constraint UK_r43af9ap4edm43mmtq01oddj6 unique (username);

    alter table criterion_tags 
       add constraint FKi7hmj6w4cwfju9raklytid47o 
       foreign key (tag_id) 
       references tags (id);

    alter table criterion_tags 
       add constraint FK60113r10ymtj1i21yop0mijdl 
       foreign key (criterion_id) 
       references criteria (id);

    alter table evaluation_ratings 
       add constraint FKr7pgea1xxloknvvx64dkwdbji 
       foreign key (rating_id) 
       references ratings (id);

    alter table evaluation_ratings 
       add constraint FKtfg0ui66knd2kd0o4de9n2g3w 
       foreign key (evaluation_id) 
       references evaluations (id);

    alter table evaluations 
       add constraint FKst0d95rgtyx6wtqgn6746u9sp 
       foreign key (evaluatee_id) 
       references users (id);

    alter table evaluations 
       add constraint FKqihdmjba0yaamhjp8gr00c27m 
       foreign key (evaluator_id) 
       references users (id);

    alter table evaluations 
       add constraint FKhdbd2jouukmpq9gy3vf3wx9av 
       foreign key (rubric_id) 
       references rubrics (id);

    alter table evaluations 
       add constraint FKj3rmu9d62hqh1y9c3336xa86p 
       foreign key (task_id) 
       references tasks (id);

    alter table ratings 
       add constraint FKqkq5f3y63dkujpg7imq036s2n 
       foreign key (criterion_id) 
       references criteria (id);

    alter table rubric_crtieria 
       add constraint FKlb4spar5hm9gm790ewvfj53ps 
       foreign key (criterion_id) 
       references criteria (id);

    alter table rubric_crtieria 
       add constraint FKp4yma4w56dnway7l59b3r6cht 
       foreign key (rubric_id) 
       references rubrics (id);

    alter table rubrics 
       add constraint FK2w3xneoptjuj9tdmjbmt09rr6 
       foreign key (creator_id) 
       references users (id);

    alter table tasks 
       add constraint FK5a855yh5kwk4d0h9p6k5uphvx 
       foreign key (evaluator_id) 
       references users (id);

    alter table tasks 
       add constraint FKcnegrugc9fvfwtdrmeb65agp3 
       foreign key (rubric_id) 
       references rubrics (id);
