# KodiWazi

**"Open Kodi" - a crowd-verified rent transparency API for Kenya.**

House-hunting in Kenya runs on word of mouth. Landlords and agents routinely quote newcomers to an estate higher prices than long-term residents actually pay, and there's no reliable way to check without already knowing someone who lives there. Existing listing sites show *asking* prices - never what people actually pay.

KodiWazi closes that gap: real renters report what they actually pay, and anyone can check whether a quote they've been given is reasonable for that area and house type - backed by real data, with an honest sense of how confident that answer is.

🔗 **Live now:** 🔗  https://kodiwazi-api.fly.dev/api/v1/swagger-ui/index.html

---

## Live URLs

The API is deployed and publicly reachable - no local setup required to try it.

| What | URL |
|---|---|
| **Base URL** | `https://kodiwazi-api.fly.dev/api/v1` |
| **Interactive API docs (Swagger UI)** | [https://kodiwazi-api.fly.dev/api/v1/swagger-ui/index.html](https://kodiwazi-api.fly.dev/api/v1/swagger-ui/index.html) |
| **Raw OpenAPI spec** | [https://kodiwazi-api.fly.dev/api/v1/v3/api-docs](https://kodiwazi-api.fly.dev/api/v1/v3/api-docs) |
| **Try it — list all areas** | [https://kodiwazi-api.fly.dev/api/v1/areas](https://kodiwazi-api.fly.dev/api/v1/areas) |

**Quickest way to explore the API:** open the Swagger UI link above - it documents every endpoint, lets you paste a JWT via the **Authorize** button, and lets you send real requests directly from the browser, no Postman or code required.

> Note: some springdoc versions serve the UI at `/swagger-ui.html` and others at `/swagger-ui/index.html`. If one 404s, try the other.

**Infrastructure:**
- **API:** deployed on [Fly.io](https://fly.io) (Paris region)
- **Database:** hosted PostgreSQL on [Supabase](https://supabase.com) (via the Session Pooler, IPv4-compatible)

---

## Tech Stack

- **Java 21** / **Spring Boot 4**
- **PostgreSQL** - persistence (hosted on Supabase)
- **Flyway** - database migrations
- **Spring Security** + **JWT** (jjwt) - stateless authentication
- **Spring Data JPA** / **Hibernate** - ORM
- **springdoc-openapi** - interactive API documentation (Swagger UI)
- **Docker** + **Fly.io** - containerized deployment
- **JUnit 5** + **Mockito** + **AssertJ** - automated testing
- **Maven** - build tool

---

## Domain Model

```
Region  (1) ─── (many) Area
Area    (1) ─── (many) RentSubmission
HouseType (fixed enum) ─── RentSubmission
Contributor (1) ─── (many) RentSubmission
```

- **Region** - a broad, well-known town/city (e.g. Nairobi, Mombasa, Kisumu).
- **Area** - a specific neighborhood/estate within a Region (e.g. Kilimani, Westlands). This is the geographic unit rent is grouped and compared by - deliberately not a specific street or address, to protect privacy.
- **HouseType** - a fixed, closed vocabulary (`SINGLE_ROOM`, `BEDSITTER`, `STUDIO`, `ONE_BEDROOM`, `TWO_BEDROOM`, `THREE_BEDROOM`, `BUNGALOW`, `MAISONETTE`) - never free text, so aggregation compares like with like.
- **Contributor** - a lightweight account (email + password), just enough to distinguish one person's submissions from another's and support abuse-resistance rules.
- **RentSubmission** - one person's report: "I pay `amount` for a `HouseType` in this `Area`, with/without utilities included." Carries a `status` (`ACTIVE` / `FLAGGED` / `REMOVED`) so abuse-resistance logic can act without destroying data.

The combination of **Area + HouseType + utilitiesIncluded** defines a "market segment" - every estimate, median, and confidence calculation operates on active submissions within one segment.

---

## Running Locally

### Prerequisites
- Java 21
- PostgreSQL running locally (or point at a hosted instance, e.g. Supabase)
- Docker (only needed if building/running the container image)
- Maven (or use the included `./mvnw`)

### Environment variables

Set these before starting the app (locally, in your IDE run configuration; on Fly.io, via `fly secrets set`):

| Variable | Description |
|---|---|
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `JWT_SECRET` | A long, random secret (32+ bytes) used to sign JWTs |

### Database

Create a database named `kodiwazi` (or `postgres`, if using Supabase's default). Flyway handles schema creation and seed data (regions/areas) automatically on first startup - no manual SQL required.

### Running

```bash
./mvnw spring-boot:run
```

The API is served under the context path `/api/v1`, e.g. `http://localhost:8080/api/v1/areas`.

### Running the tests

```bash
./mvnw test
```

Covers the core aggregation, confidence, and abuse-resistance logic in isolation (mocked repositories, no database required).

### Building and running the Docker image

```bash
docker build -t kodiwazi-api .
docker run -p 8080:8080 \
  -e DB_USERNAME=... \
  -e DB_PASSWORD=... \
  -e JWT_SECRET=... \
  kodiwazi-api
```

### Deploying (Fly.io)

```bash
fly deploy
```

Secrets are set once via `fly secrets set KEY=value` and persist across deploys.

---

## Authentication

Registration and login use JWT-based, stateless authentication.

1. `POST /auth/register` - create a Contributor account (email + password; password is BCrypt-hashed).
2. `POST /auth/login` - exchange credentials for a signed JWT (1-hour expiry).
3. Include the token on protected requests: `Authorization: Bearer <token>`.

Public endpoints (browsing areas, estimates, quote checks) require no token - reading is open to everyone, matching the project's transparency goal. Submitting rent data requires authentication.

---

## API Reference

Full interactive documentation, with request/response schemas and a "try it out" button, is available at the [Swagger UI link above](https://kodiwazi-api.fly.dev/api/v1/swagger-ui.html). Summary below.

### Auth

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | Public | Create a contributor account |
| POST | `/auth/login` | Public | Log in, receive a JWT |

### Areas

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/areas` | Public | List areas. Optional query params: `regionId`, `name` (case-insensitive partial match) |

### Rent Submissions

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/rent-submissions` | Required | Submit a rent report. Updates the contributor's existing submission for that segment if one exists, rather than creating a duplicate |
| GET | `/rent-submissions` | Required | List active submissions. Optional query params: `areaId`, `houseType` |

### Estimates & Quote Checking

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/areas/{areaId}/house-types/{houseType}/estimate` | Public | Get a recency-weighted median rent estimate for this segment, split by utilities status, each with a confidence score/label |
| POST | `/areas/{areaId}/house-types/{houseType}/check-quote` | Public | Compare a quoted rent amount against the segment's typical rent, returning a verdict (e.g. `TYPICAL`, `SIGNIFICANTLY_ABOVE_TYPICAL`) |

---

## Design Decisions

These are the deliberate engineering choices behind KodiWazi's trust and aggregation logic.

### Why median, not mean

A plain average is easily skewed by one or two extreme submissions (a luxury unit in an otherwise ordinary estate, or a data-entry mistake). The median - the middle value once sorted - is far more resistant to outliers and gives a more honest sense of what's "typical."

### Recency decay

Rent moves with the market; a submission from a year ago shouldn't count as much as one from last week. Each submission's influence decays exponentially with age, using a 90-day half-life:

```
weight = 0.5 ^ (daysOld / 90)
```

A submission from today has full weight (`1.0`); one from 90 days ago has half weight (`0.5`); one from 180 days ago has quarter weight (`0.25`). This feeds into a **weighted median**, so recent, fresher reports have more influence on the typical figure than stale ones - without ever discarding old data outright.

### Confidence scoring

Every estimate carries a confidence score (0–100) and label (`LOW` / `MEDIUM` / `HIGH`), combining three factors:

1. **Sample size** - more submissions is better, with diminishing returns (saturates at 10 active submissions).
2. **Recency** - the average decay weight across contributing submissions; mostly-fresh data scores higher than mostly-stale data.
3. **Consistency** - how tightly submitted amounts agree with each other, measured via coefficient of variation (standard deviation ÷ mean). Tightly clustered reports read as more trustworthy than wildly scattered ones.

The three sub-scores are averaged into one overall confidence score.

### Utilities-included handling

Rent with utilities (water/electricity) included is not comparable to rent without. Every median, confidence score, and quote check is computed **separately** for `utilitiesIncluded: true` and `utilitiesIncluded: false` - they are never blended.

### Abuse resistance

- **One active submission per contributor per segment.** If a contributor submits again for a segment they've already reported on (same Area + HouseType + utilities status), their existing submission is updated in place rather than a duplicate being created. This ensures no single contributor can inflate their influence on a segment's estimate simply by resubmitting.
- **Outlier detection.** New submissions (not updates) are checked against the existing active submissions in the same segment. If a submission is more than 2 standard deviations from the segment's current mean - and at least 3 existing submissions are available to judge against - it's marked `FLAGGED` instead of `ACTIVE`, excluding it from public estimates without deleting it. Flagged submissions can be reviewed later.

### Input validation

Every write endpoint validates its input before it can affect public data - rejecting missing fields, non-positive amounts, and unrealistic values. Validation failures and business-rule errors (e.g. "Area not found") return clean, structured `400` responses via a global exception handler, never a raw stack trace.

---

## Automated Tests

Core aggregation, confidence, and abuse-resistance logic is covered by unit tests using JUnit 5, Mockito, and AssertJ -repositories are mocked, so tests run without a database and cover:

- Median resistance to outliers
- Recency decay correctly shifting the weighted median toward fresher submissions
- Confidence scores correctly differing between segments with the same median but different sample size/consistency
- Quote-check verdict classification
- Honest null/zero handling for segments with no data
- Statistical outlier detection flagging a submission wildly inconsistent with existing data

---

## Project Status

Built incrementally against a self-directed growth-level spec:

- [x] **Level 1 - Foundation:** domain model, migrations, registration/login, rent submission, area browsing/search, real data-backed estimates
- [x] **Level 2 - Trust & Confidence:** weighted median, multi-factor confidence scoring
- [x] **Level 3 - Abuse Resistance:** duplicate-submission prevention, statistical outlier flagging
- [x] **Level 4 - Trustworthy & Documented:** automated tests, interactive API documentation (Swagger UI)
- [x] **Level 5 - Live:** deployed on Fly.io with hosted PostgreSQL (Supabase), real secrets management

---

## Author

Built by Margaret Njoki as a self-directed learning project — applying Spring Boot, PostgreSQL, JWT authentication, and statistical trust modeling to a real problem in the Kenyan rental market.