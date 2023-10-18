INSERT INTO ${myuniversity}_${mymodule}.settings (id, jsonb)
VALUES ('cb007def-4b68-496c-ad78-ea8e039e819d', '{"id": "cb007def-4b68-496c-ad78-ea8e039e819d", "key": "BANKING_INFORMATION_ENABLED", "value": "false"}')
ON CONFLICT (id) DO NOTHING;
