-- set default false value for organizations.exportToAccounting
UPDATE ${myuniversity}_${mymodule}.organizations
	SET jsonb=jsonb || jsonb_build_object('exportToAccounting', false)
	WHERE NOT jsonb ? 'exportToAccounting';
