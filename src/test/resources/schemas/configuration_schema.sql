-- new_tenant Role, Grant, Schema and Table

CREATE ROLE new_tenant_mod_configuration PASSWORD 'new_tenant' NOSUPERUSER NOCREATEDB INHERIT LOGIN;
GRANT new_tenant_mod_configuration TO CURRENT_USER;
CREATE SCHEMA new_tenant_mod_configuration AUTHORIZATION new_tenant_mod_configuration;

CREATE TABLE IF NOT EXISTS new_tenant_mod_configuration.config_data (
  id UUID PRIMARY KEY,
  jsonb JSONB NOT NULL
);

-- partial_tenant Role, Grant, Schema and Table

CREATE ROLE partial_tenant_mod_configuration PASSWORD 'partial_tenant' NOSUPERUSER NOCREATEDB INHERIT LOGIN;
GRANT partial_tenant_mod_configuration TO CURRENT_USER;
CREATE SCHEMA partial_tenant_mod_configuration AUTHORIZATION partial_tenant_mod_configuration;

CREATE TABLE IF NOT EXISTS partial_tenant_mod_configuration.config_data (
  id UUID PRIMARY KEY,
  jsonb JSONB NOT NULL
);

-- migration Role, Grant, Schema and Table

CREATE ROLE migration_mod_configuration PASSWORD 'migration' NOSUPERUSER NOCREATEDB INHERIT LOGIN;
GRANT migration_mod_configuration TO CURRENT_USER;
CREATE SCHEMA migration_mod_configuration AUTHORIZATION migration_mod_configuration;

CREATE TABLE IF NOT EXISTS migration_mod_configuration.config_data (
  id UUID PRIMARY KEY,
  jsonb JSONB NOT NULL
);

-- diku Role, Grant, Schema and Table

CREATE ROLE diku_mod_configuration PASSWORD 'diku' NOSUPERUSER NOCREATEDB INHERIT LOGIN;
GRANT diku_mod_configuration TO CURRENT_USER;
CREATE SCHEMA diku_mod_configuration AUTHORIZATION diku_mod_configuration;

CREATE TABLE IF NOT EXISTS diku_mod_configuration.config_data (
  id UUID PRIMARY KEY,
  jsonb JSONB NOT NULL
);
