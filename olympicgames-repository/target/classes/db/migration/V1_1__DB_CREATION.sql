create table modality (
	id numeric generated by default as identity (start with 1, increment by 1) primary key,
	name varchar(50) not null
);

create table location (
	id numeric generated by default as identity (start with 1, increment by 1) primary key,
	description varchar(50) not null
);

create table country (
	id numeric generated by default as identity (start with 1, increment by 1) primary key,
	name varchar(50) not null
);

create table phase (
	id numeric generated by default as
		identity (start with 1, increment by 1) primary key,
	description varchar(50) not null,
	tag varchar(20) not null,
	allow_same_country integer not null
);

create table competition (
	id numeric generated by default as identity (start with 1, increment by 1) primary key,
	id_location numeric not null,
	id_modality numeric not null,
	id_phase numeric not null,
	init_date TIME WITH TIME ZONE not null,
	end_date TIME WITH TIME ZONE not null
);

create table competition_country (
	id_competition numeric not null,
	id_country numeric not null
);

alter table competition add constraint fk_competition_location
	foreign key (id_location) references location (id);
	
alter table competition add constraint fk_competition_modality
	foreign key (id_modality) references modality (id);
	
alter table competition add constraint fk_competition_phase
	foreign key (id_phase) references phase (id);
	
alter table competition_country add constraint fk_comp_count_competition
	foreign key (id_competition) references competition (id);
	
alter table competition_country add constraint fk_comp_count_country
	foreign key (id_country) references country (id);
	
insert into phase (tag, description, allow_same_country) values ('final', 'Final', 1);
insert into phase (tag, description, allow_same_country) values ('semifinals', 'Semifinal', 1);
insert into phase (tag, description, allow_same_country) values ('quarterfinals', 'Quartas de Final', 0);
insert into phase (tag, description, allow_same_country) values ('eighth-finals', 'Oitavas de Final', 0);
insert into phase (tag, description, allow_same_country) values ('eliminatories', 'Eliminatórias', 0);
commit;