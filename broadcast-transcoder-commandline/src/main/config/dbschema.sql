--
-- PostgreSQL database dump
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
    domsprogrampid character varying(255) NOT NULL,
    broadcastendtime timestamp without time zone,
    broadtcaststarttime timestamp without time zone,
    channel character varying(255),
    endoffset integer NOT NULL,
    lasttranscodedtimestamp bigint,
    startoffset integer NOT NULL,
    title character varying(255),
    transcodingcommand text,
    tvmeter boolean NOT NULL
);


ALTER TABLE public.broadcasttranscodingrecord OWNER TO bta;

--
-- Name: reklamefilmtranscodingrecord; Type: TABLE; Schema: public; Owner: bta; Tablespace:
--

CREATE TABLE reklamefilmtranscodingrecord (
    domspid character varying(255) NOT NULL,
    inputfile character varying(255),
    transcodingcommand text,
    transcodingdate timestamp without time zone,
    transcodingtimestamp bigint
);


ALTER TABLE public.reklamefilmtranscodingrecord OWNER TO bta;

--
-- Name: broadcasttranscodingrecord_pkey; Type: CONSTRAINT; Schema: public; Owner: bta; Tablespace:
--

ALTER TABLE ONLY broadcasttranscodingrecord
    ADD CONSTRAINT broadcasttranscodingrecord_pkey PRIMARY KEY (domsprogrampid);


--
-- Name: reklamefilmtranscodingrecord_pkey; Type: CONSTRAINT; Schema: public; Owner: bta; Tablespace:
--

ALTER TABLE ONLY reklamefilmtranscodingrecord
    ADD CONSTRAINT reklamefilmtranscodingrecord_pkey PRIMARY KEY (domspid);


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
