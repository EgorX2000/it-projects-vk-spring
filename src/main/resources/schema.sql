create table if not exists users (
    id serial primary key ,
    name varchar(100) not null unique,
    experience int
);

create table if not exists user_skills (
    user_id int not null,
    skill varchar(100) not null,
    constraint fk_user_id foreign key (user_id) references users(id) on delete cascade,
    primary key (user_id, skill)
);

create table if not exists jobs (
    id serial primary key,
    title varchar(100) not null unique,
    company varchar(100) not null ,
    required_experience int
);

create table if not exists job_tags (
    job_id int not null,
    tag varchar(100) not null,
    constraint fk_job_id foreign key (job_id) references jobs(id) on delete cascade,
    primary key (job_id, tag)
);