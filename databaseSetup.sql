create database people_crud;

create table dependente (
    id bigint not null,
    birth_date date,
    name varchar(255) not null,
    version bigint,
    primary key (id)
);

create table pessoa (
    type varchar(31) not null,
    id bigint not null,
    city varchar(255),
    name varchar(255) not null,
    uf varchar(255) not null,
    version bigint,
    birth_date date,
    foundation_date date,
    primary key (id)
);
     
create table pessoa_dependente (
    id_pessoa bigint not null,
    id_dependente bigint not null,
    primary key (id_pessoa, id_dependente)
);

alter table pessoa_dependente 
    add constraint pessoa_dependente_id_dependente_fk 
    foreign key (id_dependente) 
    references dependente;

alter table pessoa_dependente 
    add constraint pessoa_dependente_id_pessoa_fk 
    foreign key (id_pessoa) 
    references pessoa;

create sequence hibernate_sequence start 1 increment 1;