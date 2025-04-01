-- Remove categories that are not UUIDs before changing the type

<#if mode.name() == "UPDATE">

UPDATE ${myuniversity}_${mymodule}.contacts
SET jsonb = jsonb || jsonb_build_object(
  'addresses',
  CASE WHEN jsonb->'addresses' IS NULL OR jsonb_array_length(jsonb->'addresses') = 0 THEN '[]' ELSE
    (SELECT jsonb_agg(
      jsonb_set(addresses, '{categories}', jsonb_path_query_array(addresses, '$.categories[*] ? (@ like_regex "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")'))
    ) FROM jsonb_array_elements(jsonb->'addresses') AS addresses)
  END,
  'phoneNumbers',
  CASE WHEN jsonb->'phoneNumbers' IS NULL OR jsonb_array_length(jsonb->'phoneNumbers') = 0 THEN '[]' ELSE
    (SELECT jsonb_agg(
      jsonb_set(phoneNumbers, '{categories}', jsonb_path_query_array(phoneNumbers, '$.categories[*] ? (@ like_regex "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")'))
    ) FROM jsonb_array_elements(jsonb->'phoneNumbers') AS phoneNumbers)
  END,
  'emails',
  CASE WHEN jsonb->'emails' IS NULL OR jsonb_array_length(jsonb->'emails') = 0 THEN '[]' ELSE
    (SELECT jsonb_agg(
      jsonb_set(emails, '{categories}', jsonb_path_query_array(emails, '$.categories[*] ? (@ like_regex "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")'))
    ) FROM jsonb_array_elements(jsonb->'emails') AS emails)
  END,
  'urls',
  CASE WHEN jsonb->'urls' IS NULL OR jsonb_array_length(jsonb->'urls') = 0 THEN '[]' ELSE
    (SELECT jsonb_agg(
      jsonb_set(urls, '{categories}', jsonb_path_query_array(urls, '$.categories[*] ? (@ like_regex "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")'))
    ) FROM jsonb_array_elements(jsonb->'urls') AS urls)
  END,
  'categories',
  CASE WHEN jsonb->'categories' IS NULL OR jsonb_array_length(jsonb->'categories') = 0 THEN '[]' ELSE
    jsonb_path_query_array(jsonb, '$.categories[*] ? (@ like_regex "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")')
  END
);

UPDATE ${myuniversity}_${mymodule}.organizations
SET jsonb = jsonb || jsonb_build_object(
  'addresses',
  CASE WHEN jsonb->'addresses' IS NULL OR jsonb_array_length(jsonb->'addresses') = 0 THEN '[]' ELSE
    (SELECT jsonb_agg(
      jsonb_set(addresses, '{categories}', jsonb_path_query_array(addresses, '$.categories[*] ? (@ like_regex "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")'))
    ) FROM jsonb_array_elements(jsonb->'addresses') AS addresses)
  END,
  'phoneNumbers',
  CASE WHEN jsonb->'phoneNumbers' IS NULL OR jsonb_array_length(jsonb->'phoneNumbers') = 0 THEN '[]' ELSE
    (SELECT jsonb_agg(
      jsonb_set(phoneNumbers, '{categories}', jsonb_path_query_array(phoneNumbers, '$.categories[*] ? (@ like_regex "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")'))
    ) FROM jsonb_array_elements(jsonb->'phoneNumbers') AS phoneNumbers)
  END,
  'emails',
  CASE WHEN jsonb->'emails' IS NULL OR jsonb_array_length(jsonb->'emails') = 0 THEN '[]' ELSE
    (SELECT jsonb_agg(
      jsonb_set(emails, '{categories}', jsonb_path_query_array(emails, '$.categories[*] ? (@ like_regex "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")'))
    ) FROM jsonb_array_elements(jsonb->'emails') AS emails)
  END,
  'urls',
  CASE WHEN jsonb->'urls' IS NULL OR jsonb_array_length(jsonb->'urls') = 0 THEN '[]' ELSE
    (SELECT jsonb_agg(
      jsonb_set(urls, '{categories}', jsonb_path_query_array(urls, '$.categories[*] ? (@ like_regex "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")'))
    ) FROM jsonb_array_elements(jsonb->'urls') AS urls)
  END
);

</#if>
