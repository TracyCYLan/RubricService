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

    create table rubric_assignments (
       id bigint not null,
        deleted bit not null,
        due_date datetime(6),
        evaluated_by_instructors bit not null,
        evaluated_by_students bit not null,
        name varchar(255) not null,
        publish_date datetime(6),
        rubric_id bigint not null,
        primary key (id)
    ) engine=InnoDB;

    create table rubric_criterion (
       id bigint not null,
        description varchar(255) not null,
        rubric_id bigint,
        criteria_index integer,
        primary key (id)
    ) engine=InnoDB;

    create table rubric_evaluation_ratings (
       evaluation_id bigint not null,
        rating integer,
        rating_order integer not null,
        primary key (evaluation_id, rating_order)
    ) engine=InnoDB;

    create table rubric_evaluations (
       id bigint not null,
        comments varchar(255),
        completed bit not null,
        date datetime(6),
        deleted bit not null,
        type varchar(255) not null,
        evaluator_id bigint,
        submission_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table rubric_external_evaluators (
       rubric_assignment_id bigint not null,
        evaluator_id bigint not null
    ) engine=InnoDB;

    create table rubric_ratings (
       id bigint not null,
        description varchar(255) not null,
        value integer not null,
        criteria_id bigint,
        rating_index integer,
        primary key (id)
    ) engine=InnoDB;

    create table rubric_submissions (
       id bigint not null,
        external_evaluation_count integer not null,
        instructor_evaluation_count integer not null,
        peer_evaluation_count integer not null,
        assignment_id bigint not null,
        student_id bigint not null,
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
        scale integer not null,
        creator_id bigint,
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

    alter table rubric_assignments 
       add constraint FKi1pdm5ajau8x5ts6bph5n0yge 
       foreign key (rubric_id) 
       references rubrics (id);

    alter table rubric_criterion 
       add constraint FKtiynkjsufk5adqn94r9wanjxu 
       foreign key (rubric_id) 
       references rubrics (id);

    alter table rubric_evaluation_ratings 
       add constraint FKrq7757pel1j60wsybip93ue65 
       foreign key (evaluation_id) 
       references rubric_evaluations (id);

    alter table rubric_evaluations 
       add constraint FK9giivdwhme78nsi10jsgen5dt 
       foreign key (evaluator_id) 
       references users (id);

    alter table rubric_evaluations 
       add constraint FKcxlrpkjqjwv3i3g3hmvisq5sa 
       foreign key (submission_id) 
       references rubric_submissions (id);

    alter table rubric_external_evaluators 
       add constraint FKtqyrcwaodlheaa2nols9x7ljv 
       foreign key (evaluator_id) 
       references users (id);

    alter table rubric_external_evaluators 
       add constraint FKh0dwcirgh3mcegenpj5sjqhh0 
       foreign key (rubric_assignment_id) 
       references rubric_assignments (id);

    alter table rubric_ratings 
       add constraint FK1irogjrcesxohs6uxe1tyj94u 
       foreign key (criteria_id) 
       references rubric_criterion (id);

    alter table rubric_submissions 
       add constraint FKhou4jjkrtstqhtlehmr4hm5yw 
       foreign key (assignment_id) 
       references rubric_assignments (id);

    alter table rubric_submissions 
       add constraint FK6xlypr4gf91159bsho1conkuy 
       foreign key (student_id) 
       references users (id);

    alter table rubrics 
       add constraint FK2w3xneoptjuj9tdmjbmt09rr6 
       foreign key (creator_id) 
       references users (id);
