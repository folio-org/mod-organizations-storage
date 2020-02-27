UPDATE ${myuniversity}_${mymodule}.organizations
SET
  jsonb = jsonb || jsonb_build_object('isVendor', false)
WHERE
  NOT jsonb ? 'isVendor';

UPDATE ${myuniversity}_${mymodule}.organizations
SET jsonb = jsonb || jsonb_build_object('code', jsonb ->> 'code' || '_' || floor(random() * 2147483646 + 1)::int)
WHERE jsonb ->> 'code' IN (SELECT jsonb->>'code' AS code FROM ${myuniversity}_${mymodule}.organizations GROUP BY code HAVING COUNT(*) > 1);

CREATE UNIQUE INDEX IF NOT EXISTS organizations_code_idx_unique ON ${myuniversity}_${mymodule}.organizations (lower(f_unaccent(jsonb->>'code')));
