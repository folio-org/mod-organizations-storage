UPDATE ${myuniversity}_${mymodule}.organizations
SET
  jsonb = jsonb || jsonb_build_object('isVendor', false)
WHERE
  NOT jsonb ? 'isVendor';

UPDATE ${myuniversity}_${mymodule}.organizations
SET
  jsonb = jsonb || jsonb_build_object('code', 'DEFAULT-CODE')
WHERE
  NOT jsonb ? 'code';
