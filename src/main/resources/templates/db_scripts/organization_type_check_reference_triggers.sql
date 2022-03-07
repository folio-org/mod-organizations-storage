SET search_path TO ${myuniversity}_${mymodule};

CREATE OR REPLACE FUNCTION organization_types_obtain_locks(anyelement) RETURNS void AS $$
DECLARE
  id text;
BEGIN
  IF pg_typeof($1) = 'text[]'::regtype then
    FOREACH id IN ARRAY $1
    LOOP
      PERFORM pg_advisory_xact_lock(hashtext(id));
    END LOOP;
  ELSEIF pg_typeof($1) = 'text'::regtype then
    PERFORM pg_advisory_xact_lock(hashtext($1));
  END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION organizations_check_type_references()
RETURNS TRIGGER AS $$
DECLARE
  invalid text;
  newIds text[];
BEGIN
  SELECT array_agg(id) INTO newIds FROM jsonb_array_elements_text(NEW.jsonb->'organizationTypes') AS id;
  PERFORM organization_types_obtain_locks(newIds);

  SELECT ref
    INTO invalid
    FROM jsonb_array_elements_text(NEW.jsonb->'organizationTypes') ref
    LEFT JOIN organization_types ON id=ref::uuid
    WHERE id IS NULL
    LIMIT 1;
  IF FOUND THEN
    RAISE foreign_key_violation USING
      MESSAGE='organization_type with id=' || invalid || ' doesn''t exist.',
      DETAIL='foreign key violation in organizationTypes array of organizations with id=' || NEW.id,
      SCHEMA=TG_TABLE_SCHEMA,
      TABLE=TG_TABLE_NAME;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION organization_types_check_references() RETURNS TRIGGER
AS $$
  DECLARE
    invalid text;
  BEGIN
    PERFORM organization_types_obtain_locks(OLD.id::text);
    SELECT id
      INTO invalid
      FROM organizations
      WHERE jsonb->'organizationTypes' ? OLD.id::text
      LIMIT 1;
    IF FOUND THEN
      RAISE foreign_key_violation USING
        MESSAGE='organization_type with id=' || OLD.id || ' is in use by at least one organization.',
        DETAIL='foreign key violation in organizationTypes array of organizations with id=' || invalid,
        SCHEMA=TG_TABLE_SCHEMA,
        TABLE=TG_TABLE_NAME;
    END IF;
    RETURN OLD;
  END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS organizations_check_type_references_on_insert ON organizations;
CREATE TRIGGER organizations_check_type_references_on_insert
  BEFORE INSERT ON organizations
  FOR EACH ROW
  WHEN (NEW.jsonb->'organizationTypes' IS NOT NULL AND NEW.jsonb->'organizationTypes' <> '[]')
  EXECUTE FUNCTION organizations_check_type_references();

DROP TRIGGER IF EXISTS organizations_check_type_references_on_update ON organizations;
CREATE TRIGGER organizations_check_type_references_on_update
  BEFORE UPDATE ON organizations
  FOR EACH ROW
  WHEN (NEW.jsonb->'organizationTypes' IS NOT NULL AND NEW.jsonb->'organizationTypes' <> '[]'
             AND OLD.jsonb->'organizationTypes' IS DISTINCT FROM NEW.jsonb->'organizationTypes')
  EXECUTE FUNCTION organizations_check_type_references();

DROP TRIGGER IF EXISTS organization_types_check_references_on_delete ON organization_types;
CREATE TRIGGER organization_types_check_references_on_delete
  BEFORE DELETE ON organization_types
  FOR EACH ROW
  EXECUTE FUNCTION organization_types_check_references();
