CREATE TABLE IF NOT EXISTS banking_information (
  id uuid NOT NULL PRIMARY KEY,
  jsonb JSONB NOT NULL,
  organization_id  uuid,
  constraint ORG_BANK_INFO_FK foreign key (organization_id)
  references organizations(id)
);
