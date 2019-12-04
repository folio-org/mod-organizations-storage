## 2.3.0 - Unreleased

## 2.2.0 - Released
The focus of this release was to tune and improve environment settings

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v2.1.0...v2.2.0)

### Stories
* [MODORGS-52](https://issues.folio.org/browse/MODORGS-52) Update RMB to 29.0.1
* [MODORGS-51](https://issues.folio.org/browse/MODORGS-51) Use JVM features to manage container memory
* [MODORGS-42](https://issues.folio.org/browse/MODORGS-42) Fix log4j/log4j2 configuration conflict
* [FOLIO-2235](https://issues.folio.org/browse/FOLIO-2235) Add LaunchDescriptor settings to each backend non-core module repository

### Bug Fixes
* [MODORGS-47](https://issues.folio.org/browse/MODORGS-47) CQL2PgJSON queryByFt warnings

## 2.1.0 - Released
The primary focus of this release was to add ability to assign tags to an organization record

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v2.0.0...v2.1.0)

### Stories
* [MODORGS-35](https://issues.folio.org/browse/MODORGS-35) Ability to assign tags to an organization record

## 2.0.0 - Released
This release consists of following updates:
* Fix of organizations phone number schema
* Splitting interface credentials into a separate endpoint because of security reasons. 
* Metadata was added to all organizations schemas

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v1.1.0...v2.0.0)

### Stories
* [MODORGS-32](https://issues.folio.org/browse/MODORGS-32) Include metadata property in JSON schemas
* [MODORGS-31](https://issues.folio.org/browse/MODORGS-31) Implement API for interface credentials

### Bug Fixes
* [MODORGS-29](https://issues.folio.org/browse/MODORGS-29) Phone numbers json doesn't match phone number storage

## 1.1.0 - Released
The primary focus of this release was to make a few schema updates required for ui-organizations.

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v1.0.0...v1.1.0)

### Stories
 * [MODORGS-28](https://issues.folio.org/browse/MODORDERS-28) Interface schema update: new optional field - `type`
 * [MODORGS-27](https://issues.folio.org/browse/MODORDERS-27) Make `contact.inactive` default to `false`
 * [MODORGS-26](https://issues.folio.org/browse/MODORDERS-26) Country should not be a required field

## 1.0.0 - Released
The primary focus of this release was transition from Vendors to Organizations. The release provides initial version of the API required to manage organizations.

### Stories
 * [MODORGS-25](https://issues.folio.org/browse/MODORDERS-25) Schema updates: organization.status as an enum
 * [MODORGS-23](https://issues.folio.org/browse/MODORDERS-23) Sample data: add non-vendor organizations
 * [MODORGS-22](https://issues.folio.org/browse/MODORDERS-22) Cleanup unused organization APIs
 * [MODORGS-21](https://issues.folio.org/browse/MODORDERS-21) Schema updates: change the contact schema to have a reference (UUID) to a category record
 * [MODORGS-20](https://issues.folio.org/browse/MODORDERS-20) Schema updates: change the organization schema to have a reference (UUID) to an interface record
 * [MODORGS-14](https://issues.folio.org/browse/MODORDERS-14) Schema updates: JSON key names use camelCase
 * [MODORGS-13](https://issues.folio.org/browse/MODORDERS-13) Add `validate` trait to RAML definitions
 * [MODORGS-12](https://issues.folio.org/browse/MODORDERS-12) Improve Unit test Execution
 * [MODORGS-5](https://issues.folio.org/browse/MODORDERS-5) Make `/_/jsonSchemas` to return module's schemas
 * [MODORGS-4](https://issues.folio.org/browse/MODORDERS-4) Refactor APIs
 * [MODORGS-3](https://issues.folio.org/browse/MODORDERS-3) Sample and Reference Data
 * [MODORGS-1](https://issues.folio.org/browse/MODORDERS-1) Schema updates: migration from "vendors" to "organizations"
