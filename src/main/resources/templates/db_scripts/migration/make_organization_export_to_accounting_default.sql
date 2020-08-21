-- set default false value for organization.enclosureNeeded
UPDATE ${myuniversity}_${mymodule}.organizations
	SET jsonb=jsonb || jsonb_build_object('exportToAccounting', false)
	WHERE NOT jsonb ? 'exportToAccounting';
