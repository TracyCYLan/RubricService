    create table hibernate_sequence (
       next_val bigint
    ) engine=InnoDB;

    insert into hibernate_sequence values ( 1 );

    insert into hibernate_sequence values ( 1 );

    insert into hibernate_sequence values ( 1 );

    insert into hibernate_sequence values ( 1 );

    insert into hibernate_sequence values ( 1 );

    insert into hibernate_sequence values ( 1 );

    create table rubric_criterion (
       id bigint not null,
        description varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table rubric_evaluation_ratings (
       evaluation_id bigint not null,
        rating_id bigint not null
    ) engine=InnoDB;

    create table rubric_evaluations (
       id bigint not null,
        comments varchar(255),
        completed bit not null,
        date datetime(6),
        deleted bit not null,
        type varchar(255) not null,
        evaluatee_id bigint,
        evaluator_id bigint,
        rubric_id bigint,
        rubrictask_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table rubric_ratings (
       id bigint not null,
        description varchar(255) not null,
        value double precision not null,
        criteria_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table rubric_tasks (
       id bigint not null,
        due_date datetime(6),
        name varchar(255) not null,
        type varchar(255) not null,
        evaluator_id bigint,
        rubric_id bigint,
        primary key (id)
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

    create table rubrics_criterion_matching (
       rubric_id bigint not null,
        criteria_id bigint not null
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

    alter table rubric_evaluation_ratings 
       add constraint FKkq0n99a3nmwq94nm3tkft5fq5 
       foreign key (rating_id) 
       references rubric_ratings (id);

    alter table rubric_evaluation_ratings 
       add constraint FKrq7757pel1j60wsybip93ue65 
       foreign key (evaluation_id) 
       references rubric_evaluations (id);

    alter table rubric_evaluations 
       add constraint FKgoepd7osu38mniewshw9eac8u 
       foreign key (evaluatee_id) 
       references users (id);

    alter table rubric_evaluations 
       add constraint FK9giivdwhme78nsi10jsgen5dt 
       foreign key (evaluator_id) 
       references users (id);

    alter table rubric_evaluations 
       add constraint FKexc69etdcc5wyjpqtey3uks1e 
       foreign key (rubric_id) 
       references rubrics (id);

    alter table rubric_evaluations 
       add constraint FKf8x5ng4ct54evaiy3g5qqr5we 
       foreign key (rubrictask_id) 
       references rubric_tasks (id);

    alter table rubric_ratings 
       add constraint FK1irogjrcesxohs6uxe1tyj94u 
       foreign key (criteria_id) 
       references rubric_criterion (id);

    alter table rubric_tasks 
       add constraint FKt0acomxt9ygh5vkbuk4ob5hek 
       foreign key (evaluator_id) 
       references users (id);

    alter table rubric_tasks 
       add constraint FKmp59bvm7f1dylpab88pgbhenk 
       foreign key (rubric_id) 
       references rubrics (id);

    alter table rubrics 
       add constraint FK2w3xneoptjuj9tdmjbmt09rr6 
       foreign key (creator_id) 
       references users (id);

    alter table rubrics_criterion_matching 
       add constraint FKsrrs2lhuwost7s96uwy1cbpno 
       foreign key (criteria_id) 
       references rubric_criterion (id);

    alter table rubrics_criterion_matching 
       add constraint FK5457gung2rrhueiq7vm4bjwvl 
       foreign key (rubric_id) 
       references rubrics (id);
