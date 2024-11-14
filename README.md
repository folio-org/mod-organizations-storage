# mod-organizations-storage

Copyright (C) 2019-2023 The Open Library Foundation

This software is distributed under the terms of the Apache License,
Version 2.0. See the file "[LICENSE](LICENSE)" for more information.

## Introduction

This is the Organizations storage module.

## Additional information

The following APIs are not currently in use and may at some point be removed or resurrected:
* POST	/organization-storage/addresses
* GET	/organization-storage/addresses
* GET	/organization-storage/addresses/<id>
* PUT	/organization-storage/addresses/<id>
* DELETE	/organization-storage/addresses/<id>
- POST	/organization-storage/emails
- GET	/organization-storage/emails
- GET	/organization-storage/emails/<id>
- PUT	/organization-storage/emails/<id>
- DELETE	/organization-storage/emails/<id>
* POST	/organization-storage/phone-numbers
* GET	/organization-storage/phone-numbers
* GET	/organization-storage/phone-numbers/<id>
* PUT	/organization-storage/phone-numbers/<id>
* DELETE	/organization-storage/phone-numbers/<id>
- POST	/organization-storage/urls
- GET	/organization-storage/urls
- GET	/organization-storage/urls/<id>
- PUT	/organization-storage/urls/<id>
- DELETE	/organization-storage/urls/<id>

### Kafka domain event pattern
The pattern means that every time when a domain entity is created/updated a message is posted to kafka topic.
Currently, domain events are supported for organizations The events are posted into the following topics:

- `ACQ_ORGANIZATION_CHANGED` - for organizations

The event payload has the following structure:
```json5
{
  "id": "12bb13f6-d0fa-41b5-b0ad-d6561975121b",
  "action": "Created|Edited",
  "userId": "1d4f3f6-d0fa-41b5-b0ad-d6561975121b",
  "eventDate": "2024-11-14T10:00:00.000+0000",
  "actionDate": "2024-11-14T10:00:00.000+0000",
  "entitySnapshot": { } // entity being either of these: organizations
}
```

Default value for all partitions is 1.
Kafka partition key for all the events is entity id.

### Issue tracker

See project [MODORGS](https://issues.folio.org/browse/MODORGS)
at the [FOLIO issue tracker](https://dev.folio.org/guidelines/issue-tracker).

### Other documentation

Other [modules](https://dev.folio.org/source-code/#server-side) are described,
with further FOLIO Developer documentation at
[dev.folio.org](https://dev.folio.org/)
