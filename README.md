# Event and Vendor Management System

**Student:** IRADUKUNDA Prince  
**Student ID:** 28007  
**Project:** Event and Vendor Management System

Spring Boot REST API for organizers, vendors, service types, venue stalls, event bookings, and vendor invoices. It uses Spring Data JPA and PostgreSQL and follows the API requirements in the mid-term project paper.

## Requirements

- Java 17
- Maven 3.9+
- Docker Desktop (recommended for PostgreSQL), or a local PostgreSQL instance

## Run the API and PostgreSQL

From the project root:

```powershell
docker compose up --build
```

The API listens at `http://localhost:8080`. PostgreSQL is available on port `5432`. The local development database uses the `eventify` database/user/password. For a local PostgreSQL instance, configure `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD`.

To run only the API after PostgreSQL is available:

```powershell
cd backend
mvn spring-boot:run
```

Hibernate uses `spring.jpa.hibernate.ddl-auto=update`; SQL statements are printed with `spring.jpa.show-sql=true`.

## Domain and API

All entities are used directly as JSON request/response bodies. Association fields should reference existing records using nested objects containing their `id`, for example:

```json
{
  "eventDate": "2026-11-02",
  "eventName": "Garden reception",
  "guests": 100,
  "organizer": { "id": "ORGANIZER_UUID" },
  "vendor": { "id": "VENDOR_UUID" }
}
```

Create parent records first: service types and stalls before vendors; vendors and organizers before event bookings; delivered event bookings before invoices. The database is intentionally not auto-seeded. For an empty development database, start the API and run the Windows PowerShell seed script; it creates the Step 0 records by calling the API save/update/status endpoints:

```powershell
.\backend\seed-data.ps1
```

The script refuses to run if any of the five core resource collections already contains records. You can also enter the records manually through the save endpoints and use the Postman collection in the repository root.

| Resource | CRUD endpoints |
| --- | --- |
| Service types | `POST /api/service-types/save`, `GET /api/service-types/all`, `GET /api/service-types/{id}`, `PUT /api/service-types/update/{id}`, `DELETE /api/service-types/delete/{id}` |
| Stalls | `POST /api/stalls/save`, `GET /api/stalls/all`, `GET /api/stalls/{id}`, `PUT /api/stalls/update/{id}`, `DELETE /api/stalls/delete/{id}` |
| Vendors | `POST /api/vendors/save`, `GET /api/vendors/all`, `GET /api/vendors/{id}`, `PUT /api/vendors/update/{id}`, `DELETE /api/vendors/delete/{id}` |
| Organizers | `POST /api/organizers/save`, `GET /api/organizers/all`, `GET /api/organizers/{id}`, `PUT /api/organizers/update/{id}`, `DELETE /api/organizers/delete/{id}` |
| Event bookings | `POST /api/event-bookings/save`, `GET /api/event-bookings/all`, `GET /api/event-bookings/{id}`, `PUT /api/event-bookings/update/{id}`, `DELETE /api/event-bookings/delete/{id}` |

Additional required endpoints:

| Purpose | Endpoint |
| --- | --- |
| Organizers by case-insensitive last name | `GET /api/organizers/by-last-name?lastName=gatete` |
| Bookings by status | `GET /api/event-bookings/by-status?status=REQUESTED` |
| Bookings in inclusive date range | `GET /api/event-bookings/between?start=2026-10-01&end=2026-10-31` |
| Vendors by service type | `GET /api/vendors/by-service-type?name=cater` |
| Free stalls | `GET /api/stalls/free` |
| Unused service types | `GET /api/service-types/unused` |
| Organizers of a vendor | `GET /api/organizers/of-vendor/{vendorId}` |
| Change booking status | `PATCH /api/event-bookings/{id}/status?status=ACCEPTED` |
| Add vendor service type | `PUT /api/vendors/{vendorId}/service-types/{serviceTypeId}` |
| Move vendor to stall | `PATCH /api/vendors/{vendorId}/stall?stallNumber=4` |
| Save invoice | `POST /api/vendor-invoices/save?eventBookingId={id}` |
| Invoice history by organizer code | `GET /api/vendor-invoices/organizer/{organizerCode}` |
| Delete invoice | `DELETE /api/vendor-invoices/delete/{id}` |
| Booking counts by status | `GET /api/event-bookings/stats/by-status` |
| Cancel non-delivered bookings on a day | `PATCH /api/event-bookings/cancel-day?vendorId={id}&date=2026-11-02` |
| Dashboard summary | `GET /api/dashboard/summary` |
| Paginated bookings | `GET /api/event-bookings/page?page=0&size=5&sort=eventDate,desc` |

`POST /api/event-bookings/save` rejects past dates, prevents a vendor from having overlapping non-declined bookings on the same date, and always starts the booking at `REQUESTED`. Only the status endpoint can advance booking status. Invoices can be written only for `DELIVERED` bookings; bookings with invoices cannot be deleted.

## Postman

Import `eventand-vendor-management-system-midterm.postman_collection.json` into Postman. Set the collection variable `baseUrl` to `http://localhost:8080`. Replace the `{{...Id}}` placeholders with IDs returned by the corresponding `/all` endpoints. Run the save requests before the dependent query and business-rule requests.

The paper's example includes bookings dated `2026-10-20`. Since that date is in the past as of 2026-10-22, the safe-save endpoint correctly rejects a new booking on that date. To reproduce the example data while honoring the rule, save the bookings on future dates first, then update their dates using the CRUD update endpoint; use the status endpoint to set one of the two bookings to `DECLINED`.
