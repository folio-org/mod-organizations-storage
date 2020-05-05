UPDATE ${myuniversity}_${mymodule}.organizations
SET
  jsonb = jsonb || jsonb_build_object('isVendor', false)
WHERE
  NOT jsonb ? 'isVendor';
