--
-- PostgreSQL database dump
-- TODO update this to match the used database
--


SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: broadcasttranscodingrecord; Type: TABLE; Schema: public; Owner: bta; Tablespace: 
--

CREATE TABLE broadcasttranscodingrecord (
    id character varying(255) NOT NULL,
    domslatesttimestamp bigint,
    failuremessage character varying(255),
    lasttranscodedtimestamp bigint,
    transcodingstate integer,
    broadcastendtime timestamp without time zone,
    broadcaststarttime timestamp without time zone,
    channel character varying(255),
    endoffset integer NOT NULL,
    startoffset integer NOT NULL,
    title character varying(255),
    transcodingcommand text,
    tvmeter boolean NOT NULL
);


ALTER TABLE public.broadcasttranscodingrecord OWNER TO bta;

--
-- Name: programmediainfo; Type: TABLE; Schema: public; Owner: bta; Tablespace: 
--

CREATE TABLE programmediainfo (
    id character varying(255) NOT NULL,
    broadcasttype integer,
    endoffset integer NOT NULL,
    expectedfilesizebyte bigint NOT NULL,
    fileexists boolean NOT NULL,
    filesizebyte bigint NOT NULL,
    filetimestamp timestamp without time zone,
    lasttouched timestamp without time zone,
    lengthinseconds integer NOT NULL,
    mediatype integer,
    note character varying(255),
    sharduuid character varying(255),
    startoffset integer NOT NULL,
    transcodecommandline character varying(255)
);


ALTER TABLE public.programmediainfo OWNER TO bta;

--
-- Name: reklamefilmtranscodingrecord; Type: TABLE; Schema: public; Owner: bta; Tablespace: 
--

CREATE TABLE reklamefilmtranscodingrecord (
    id character varying(255) NOT NULL,
    domslatesttimestamp bigint,
    failuremessage character varying(255),
    lasttranscodedtimestamp bigint,
    transcodingstate integer,
    inputfile character varying(255),
    transcodingcommand text
);


ALTER TABLE public.reklamefilmtranscodingrecord OWNER TO bta;

--
-- Name: broadcasttranscodingrecord_pkey; Type: CONSTRAINT; Schema: public; Owner: bta; Tablespace: 
--

ALTER TABLE ONLY broadcasttranscodingrecord
    ADD CONSTRAINT broadcasttranscodingrecord_pkey PRIMARY KEY (id);


--
-- Name: programmediainfo_pkey; Type: CONSTRAINT; Schema: public; Owner: bta; Tablespace: 
--

ALTER TABLE ONLY programmediainfo
    ADD CONSTRAINT programmediainfo_pkey PRIMARY KEY (id);


--
-- Name: reklamefilmtranscodingrecord_pkey; Type: CONSTRAINT; Schema: public; Owner: bta; Tablespace: 
--

ALTER TABLE ONLY reklamefilmtranscodingrecord
    ADD CONSTRAINT reklamefilmtranscodingrecord_pkey PRIMARY KEY (id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

