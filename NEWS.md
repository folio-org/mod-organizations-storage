## 5.0.0 - Unreleased

## 4.9.0 - Released (Sunflower R1 2025)
The primary focus of the release was to implement kafka events and update libraries of dependent acquisition modules.

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v4.8.0...v4.9.0)

### Stories
* [MODORGSTOR-164] (https://folio-org.atlassian.net/browse/MODORGSTOR-164) - Implement audit outbox pattern for sending kafka events about organization updates
* [MODORGSTOR-173] (https://folio-org.atlassian.net/browse/MODORGSTOR-173) - Add POST and DELETE methods to /organizations-storage/settings API
* [FOLIO-4200] (https://folio-org.atlassian.net/browse/FOLIO-4200) - Update to mod-organizations-storage Java 21

### Dependencies
* Bump `java` from `17` to `21`
* Bump `raml` from `35.3.0` to `35.4.0`


## 4.8.0 - Released (Ramson R2 2024)
The primary focus of this release was to improve the banking feature and update libraries of dependent acq modules

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v4.7.0...v4.8.0)

### Stories
* [MODORGSTOR-155] (https://folio-org.atlassian.net/browse/MODORGSTOR-163) - Banking - prevent deletion of account type if it is being used by an organization
* [MODORGSTOR-153] (https://folio-org.atlassian.net/browse/MODORGSTOR-166) - Update libraries of dependant acq modules to the latest versions

### Dependencies
* Bump `raml` from `35.2.0` to `35.3.0`
* Added `folio-module-descriptor-validator` version `1.0.0`


## 4.7.0 - Released (Quesnelia R1 2024)
The primary focus of this release was to create CRUD for Banking Accounting Types
and Organization Settings as well as API for privileged contacts

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v4.6.0...v4.7.0)

### Stories
* [MODORGSTOR-155] (https://issues.folio.org/browse/MODORGSTOR-155) - Implement CRUD for Organization Settings
* [MODORGSTOR-153] (https://issues.folio.org/browse/MODORGSTOR-153) - Implement API for Privileged contacts
* [MODORGSTOR-148] (https://issues.folio.org/browse/MODORGSTOR-148) - Implement CRUD for Banking Account Types

### Tech Debts
* [MODORGSTOR-154] (https://issues.folio.org/browse/MODORGSTOR-154) - Refactor deleteOrganizationsInterfaceById to remove Tx usage

### Dependencies
* Bump `raml` from `35.0.1` to `35.2.0`
* Bump `vertx` from `4.3.4` to `4.5.4`

## 4.6.0 - Released (Poppy R2 2023)
The primary focus of this release was to update dependent raml-util and java version

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v4.5.0...v4.6.0)

### Stories
* [MODORGSTOR-143] (https://issues.folio.org/browse/MODORGSTOR-143) - Update to Java 17 mod-organizations-storage
* [MODORGSTOR-137] (https://issues.folio.org/browse/MODORGSTOR-137) - Update dependent raml-util

### Bug Fixes
* [MODORGSTOR-144] (https://issues.folio.org/browse/MODORGSTOR-144) - Organizations app shows only 20k records as total when the actual number is much higher

## 4.5.0 - Released (Orchid R1 2023)
The primary focus of this release was to update logging

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v4.4.0...v4.5.0)

### Stories
* [MODORGSTOR-122] (https://issues.folio.org/browse/MODORGSTOR-122) - Logging improvement

## 4.4.0 - Released (Nolana R3)
The primary focus of this release was to update RMB up to v35.0.1

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v4.3.0...v4.4.0)

### Stories
* [MODORGSTOR-131] (https://issues.folio.org/browse/MODORGSTOR-131) - Upgrade RAML Module Builder

## 4.3.0 - Released (Morning Glory R2 2022)

The primary focus of this release was to update RMB up to v34.1.0

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v4.2.0...v4.3.0)

### Stories
* [MODORGSTOR-129] (https://issues.folio.org/browse/MODORGSTOR-129) - Upgrade RAML Module Builder
* [MODORGSTOR-121] (https://issues.folio.org/browse/MODORGSTOR-121) - Create Organization type schema and API


## 4.2.0 - Released

The primary focus of this release was to update RMB up to v33.0.0

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v4.1.0...v4.2.0)

### Stories
* [MODORGSTOR-112] (https://issues.folio.org/browse/MODORGSTOR-112) - All organizations should have primary address


## 4.1.0 - Released

The primary focus of this release was to update RMB up to v33.0.0

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v4.0.0...v4.1.0)

### Stories
* [MODORGSTOR-107] (https://issues.folio.org/browse/MODORGSTOR-107) - Account schema remove from required : "paymentMethod", "libraryCode","libraryEdiCode"
* [MODORGSTOR-104] (https://issues.folio.org/browse/MODORGSTOR-104) - Mod-organizations-storage: Update RMB
* [MODORGSTOR-100] (https://issues.folio.org/browse/MODORGSTOR-100) - Add personal data disclosure form

## 4.0.0 - Released

The primary focus of this release was to update RMB up to v32.1.0

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v3.2.2...v4.0.0)

### Stories
* [MODORGSTOR-98](https://issues.folio.org/browse/MODORGSTOR-98) - mod-organizations-storage: Update RMB

## 3.2.2 - Released

The primary focus of this release was to fix RMB and logging issues

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v3.2.1...v3.2.2)

### Bug Fixes
* [MODORGSTOR-95](https://issues.folio.org/browse/MODORGSTOR-95) - Wrong RMB version in the latest 3.2.1 Q3 release
* [MODORGSTOR-91](https://issues.folio.org/browse/MODORGSTOR-91) - No logging in honeysuckle version

## 3.2.1 - Released

The primary focus of this release was to fix RMB and logging issues

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v3.2.0...v3.2.1)

### Bug Fixes
* [MODORGSTOR-93](https://issues.folio.org/browse/MODORGSTOR-93) - Update RMB up to v31.1.5
* [MODORGSTOR-91](https://issues.folio.org/browse/MODORGSTOR-91) - No logging in honeysuckle version


## 3.2.0 - Released
The primary focus of this release was to migrate to JDK 11 with new RMB, update schema

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v3.1.1...v3.2.0)

### Stories
* [MODORGSTOR-85](https://issues.folio.org/browse/MODORGSTOR-85) Retrieve exact count of total records > 1000
* [MODORGSTOR-83](https://issues.folio.org/browse/MODORGSTOR-83) mod-organizations-storage: Update RMB
* [MODORGSTOR-79](https://issues.folio.org/browse/MODORGSTOR-79) Migrate mod-organizations-storage to JDK 11
* [MODORGSTOR-78](https://issues.folio.org/browse/MODORGSTOR-78) Allow use of spaces in organization code
* [MODORGSTOR-77](https://issues.folio.org/browse/MODORGSTOR-77) Support new field "exportToAccounting" in organization schema

## 3.1.1 - Released
Bugfix release for fixing upgrade issue between Q1 and Q2 and it was done by moving on RMB 30.2.4

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v3.1.0...v3.1.1)

### Bug Fixes
* [MODORGSTOR-74](https://issues.folio.org/browse/MODORGSTOR-74) Upgrade issue between Q1 and Q2: "public.gin_trgm_ops" does not exist)

## 3.1.0 - Released
The primary focus of this release was to add acquisition units support for organizations API

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v3.0.1...v3.1.0)

### Stories
* [MODORGSTOR-72](https://issues.folio.org/browse/MODORGSTOR-72) Update to RMB v30.0.1
* [MODORGSTOR-69](https://issues.folio.org/browse/MODORGSTOR-69) Securing APIs by default
* [MODORGSTOR-67](https://issues.folio.org/browse/MODORGSTOR-67) Add an "acqUnitIds" field to the organization schema

### Bug Fixes
* [MODORGSTOR-68](https://issues.folio.org/browse/MODORGSTOR-68) Migration issue between Edelweiss and Fameflower

## 3.0.1 - Released
Bugfix release to fix of deletion functionality for Interface in Organizations app

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v3.0.0...v3.0.1)

### Bug Fixes
* [MODORGS-58](https://issues.folio.org/browse/MODORGS-58) Unable to delete Interface in Organizations app

## 3.0.0 - Released
The primary focus of this release was to change organization code constraints: it is now required in both back-end and front-end

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v2.2.1...v3.0.0)

### Stories
* [MODORGS-60](https://issues.folio.org/browse/MODORGS-60) Migration script for organizations code

### Bug Fixes
* [MODORGS-53](https://issues.folio.org/browse/MODORGS-53) Organization code optional in back-end, required in front-end

## 2.2.1 - Released
Bugfix release to support filtering by Vendor=No in Organizations

[Full Changelog](https://github.com/folio-org/mod-organizations-storage/compare/v2.2.0...v2.2.1)

### Bug Fixes
* [MODORGS-56](https://issues.folio.org/browse/MODORGS-56) Set isVendor default value in the organization schema

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
