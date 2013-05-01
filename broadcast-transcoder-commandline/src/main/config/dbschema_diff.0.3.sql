--
-- sql commands to update the dbschema for consistency with v0.3
--
ALTER TABLE broadcasttranscodingrecord ADD COLUMN video BOOLEAN default false;
