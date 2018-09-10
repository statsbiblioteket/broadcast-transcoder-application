create schema public
;

comment on schema public is 'standard public schema'
;

create table if not exists broadcasttranscodingrecord
(
	id varchar(255) not null
		constraint broadcasttranscodingrecord_pkey
			primary key,
	domslatesttimestamp bigint,
	failuremessage varchar(255),
	lasttranscodedtimestamp bigint,
	transcodingstate integer,
	broadcastendtime timestamp,
	broadcaststarttime timestamp,
	channel varchar(255),
	endoffset integer not null,
	startoffset integer not null,
	title varchar(255),
	transcodingcommand text,
	tvmeter boolean not null,
	video boolean default false
)
;

create table if not exists programmediainfo
(
	id varchar(255) not null
		constraint programmediainfo_pkey
			primary key,
	broadcasttype integer,
	endoffset integer not null,
	expectedfilesizebyte bigint not null,
	fileexists boolean not null,
	filesizebyte bigint not null,
	filetimestamp timestamp,
	lasttouched timestamp,
	lengthinseconds integer not null,
	mediatype integer,
	note varchar(255),
	sharduuid varchar(255),
	startoffset integer not null,
	transcodecommandline varchar(255)
)
;

create table if not exists reklamefilmtranscodingrecord
(
	id varchar(255) not null
		constraint reklamefilmtranscodingrecord_pkey
			primary key,
	domslatesttimestamp bigint,
	failuremessage varchar(255),
	lasttranscodedtimestamp bigint,
	transcodingstate integer,
	inputfile varchar(255),
	transcodingcommand text
)
;

create table if not exists thumbnailextractionrecord
(
	id varchar(255) not null
		constraint thumbnailextractionrecord_pkey
			primary key,
	errormessage text,
	extractioncommand text,
	extractionstate varchar(255)
)
;

