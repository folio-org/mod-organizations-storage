UPDATE ${myuniversity}_${mymodule}.organizations
SET
  jsonb = jsonb_set(
    jsonb,
    '{accounts}',
    (
      SELECT COALESCE(jsonb_agg(account), '[]'::jsonb)
      FROM jsonb_array_elements(jsonb -> 'accounts') AS account
      WHERE account ->> 'name' != 'Cairn University'
    )
  )
WHERE
  jsonb ->> 'code' = 'MOSAIC'
  AND jsonb -> 'accounts' @> '[{"name": "Cairn University"}]';