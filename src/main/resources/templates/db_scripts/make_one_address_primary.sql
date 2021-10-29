-- Set the first address as primary when an organization does not have any primary address
UPDATE ${myuniversity}_${mymodule}.organizations
SET
  jsonb = jsonb_set(jsonb, '{addresses, 0, isPrimary}', 'true')
WHERE
  jsonb_array_length(jsonb -> 'addresses') > 0 AND NOT jsonb -> 'addresses' @> '[{"isPrimary":true}]';
