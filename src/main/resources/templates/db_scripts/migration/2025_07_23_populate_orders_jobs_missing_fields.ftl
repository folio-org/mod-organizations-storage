<#if mode.name() == "UPDATE">

UPDATE ${myuniversity}_mod_configuration.config_data
SET jsonb = jsonb_set(
      jsonb,
      '{value}',
      to_jsonb(
        (jsonb_set(
          (jsonb->>'value')::jsonb,
          '{exportTypeSpecificParameters,vendorEdiOrdersExportConfig}',
          ((jsonb->>'value')::jsonb->'exportTypeSpecificParameters'->'vendorEdiOrdersExportConfig') ||
          jsonb_build_object(
            'integrationType', 'Ordering',
            'transmissionMethod', 'FTP',
            'fileFormat', 'EDI'
          ),
          true
        ))::text
      )
    )
WHERE jsonb->>'module' = 'mod-data-export-spring' AND jsonb ? 'value'
  AND (jsonb->>'value')::jsonb->>'type' = 'EDIFACT_ORDERS_EXPORT'
  AND ((jsonb->>'value')::jsonb->'exportTypeSpecificParameters' ? 'vendorEdiOrdersExportConfig')
  AND NOT (((jsonb->>'value')::jsonb->'exportTypeSpecificParameters'->'vendorEdiOrdersExportConfig') ? 'integrationType')
  AND NOT (((jsonb->>'value')::jsonb->'exportTypeSpecificParameters'->'vendorEdiOrdersExportConfig') ? 'transmissionMethod')
  AND NOT (((jsonb->>'value')::jsonb->'exportTypeSpecificParameters'->'vendorEdiOrdersExportConfig') ? 'fileFormat');

</#if>
