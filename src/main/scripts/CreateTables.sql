    create table artifacts (
       id bigint not null,
        endpoint varchar(255) not null,
        name varchar(255) not null,
        type varchar(255) not null,
        association_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table assessment_group (
       id bigint not null,
        assess_date datetime(6),
        description varchar(255),
        name varchar(255),
        rubric_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table assessment_ratings (
       assessment_id bigint not null,
        rating_id bigint not null
    ) engine=InnoDB;

    create table assessments (
       id bigint not null,
        comments varchar(255),
        completed bit not null,
        date datetime(6),
        deleted bit not null,
        artifact_id bigint,
        assessment_group_id bigint,
		type varchar(255),
        assesor_id bigint,
        association_id bigint,
        rubric_id bigint,
        task_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table associations (
       id bigint not null,
        endpoint varchar(255) not null,
        name varchar(255) not null,
        type varchar(255) not null,
        rubric_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table criteria (
       id bigint not null,
        deleted bit not null,
        description varchar(255) not null,
        name varchar(255) not null,
        publish_date datetime(6),
        reusable bit not null,
        FULLTEXT ( name , description ),
        primary key (id)
    ) engine=InnoDB;

    create table criterion_tags (
       criterion_id bigint not null,
        tag_id bigint not null
    ) engine=InnoDB;

    create table externals (
       id bigint not null,
        external_id varchar(255),
        source varchar(255),
        type varchar(255),
        criterion_id bigint,
        rubric_id bigint,
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

    create table rubric_criteria (
       rubric_id bigint not null,
        criterion_id bigint not null,
        criterion_order integer not null,
        primary key (rubric_id, criterion_order)
    ) engine=InnoDB;

    create table rubrics (
       id bigint not null,
        deleted bit not null,
        description varchar(255),
        public bit not null,
        last_updated_date datetime(6),
        name varchar(255) not null,
        obsolete bit not null,
        publish_date datetime(6),
        creator_id bigint,
         FULLTEXT(name,description),
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
        assessor_id bigint,
        association_id bigint,
        primary key (id)
    ) engine=InnoDB;

    create table users (
       id bigint not null,
        cin varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        middle_name varchar(255),
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    alter table users 
       add constraint UK_r43af9ap4edm43mmtq01oddj6 unique (username);

    alter table artifacts 
       add constraint FKi3xd5usajkpbuimva0k8rv7sh 
       foreign key (association_id) 
       references associations (id);

    alter table assessment_group 
       add constraint FKrc4njigx5qwgrlfe5tfcmui1j 
       foreign key (rubric_id) 
       references rubrics (id);

    alter table assessment_ratings 
       add constraint FKpf7rbs9siysidpavy0kvci08m 
       foreign key (rating_id) 
       references ratings (id);

    alter table assessment_ratings 
       add constraint FK85whnro40v36j4ai3c8pby0gf 
       foreign key (assessment_id) 
       references assessments (id);

    alter table assessments 
       add constraint FKc66egnd7uamp4oyucik0upfl7 
       foreign key (artifact_id) 
       references artifacts (id);

    alter table assessments 
       add constraint FKelomj2cgh9k87n3erq8j2vpeg 
       foreign key (assessment_group_id) 
       references assessment_group (id);

    alter table assessments 
       add constraint FKs7rtlo6e56y6kl5hervu5fehm 
       foreign key (assesor_id) 
       references users (id);

    alter table assessments 
       add constraint FK6bahvwpgsolvirjm45o5imwu0 
       foreign key (association_id) 
       references associations (id);

    alter table assessments 
       add constraint FK7va5fxsqvo6tth0jwvpynrpah 
       foreign key (rubric_id) 
       references rubrics (id);

    alter table assessments 
       add constraint FKqwksq4urufoxd7unq3j5t8a4r 
       foreign key (task_id) 
       references tasks (id);

    alter table associations 
       add constraint FKlj0j4qal71smvw7ldnm2qpk8v 
       foreign key (rubric_id) 
       references rubrics (id);

    alter table criterion_tags 
       add constraint FKi7hmj6w4cwfju9raklytid47o 
       foreign key (tag_id) 
       references tags (id);

    alter table criterion_tags 
       add constraint FK60113r10ymtj1i21yop0mijdl 
       foreign key (criterion_id) 
       references criteria (id);

    alter table externals 
       add constraint FKasn03sjgsaoowf1bnsnnmf3at 
       foreign key (criterion_id) 
       references criteria (id);

    alter table externals 
       add constraint FKo4ga8ut47q5od73ujyfpeaiyp 
       foreign key (rubric_id) 
       references rubrics (id);

    alter table ratings 
       add constraint FKqkq5f3y63dkujpg7imq036s2n 
       foreign key (criterion_id) 
       references criteria (id);

    alter table rubric_criteria 
       add constraint FKiqehfsbkmxgry7gjiq7j5c7xh 
       foreign key (criterion_id) 
       references criteria (id);

    alter table rubric_criteria 
       add constraint FKjs1mcsyrjsecv9qu0em2ldafw 
       foreign key (rubric_id) 
       references rubrics (id);

    alter table rubrics 
       add constraint FK2w3xneoptjuj9tdmjbmt09rr6 
       foreign key (creator_id) 
       references users (id);

    alter table tasks 
       add constraint FKhs3j93s8qbqcx7tg5knbjjjie 
       foreign key (assessor_id) 
       references users (id);

    alter table tasks 
       add constraint FKqur35h4e8e75nx2upqskimavb 
       foreign key (association_id) 
       references associations (id);
