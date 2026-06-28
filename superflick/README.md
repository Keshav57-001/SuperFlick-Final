# SuperFlick API — Spring Boot Backend

Swipe-based job discovery platform backend.

## Tech Stack
- Java 17 + Spring Boot 3.2
- PostgreSQL + Redis
- WebSocket (STOMP) for real-time chat
- JWT Authentication + OAuth2
- AWS S3 for file storage
- Razorpay / Stripe for payments
- OpenAI API for resume parsing

## Modules
| Module | Description |
|---|---|
| auth | JWT, OTP, OAuth (Google/MS/GitHub/LinkedIn) |
| user | User account management |
| candidate | Candidate profile & skills |
| hr | HR profile & company setup |
| job | Job posting & swipe feed |
| swipe | Swipe actions (apply/ignore) |
| application | Application records |
| match | Bi-directional match detection |
| chat | WebSocket real-time chat |
| notification | In-app + email notifications |
| payment | Razorpay/Stripe integration |
| subscription | Premium subscription management |
| matching | Skill/exp/salary matching engine |
| ai | AI resume parsing (OpenAI) |
| file | S3 file upload service |
| admin | Admin dashboard APIs |
| superadmin | Account management + audit logs |
| cron | 4 AM auto-apply scheduled job |

## Quick Start
```bash
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml
# Fill in your env values
mvn spring-boot:run
```

## API Docs
After starting: http://localhost:8080/swagger-ui.html

## Cron Job
Auto-apply runs daily at 4:00 AM IST.
Logs stored in cron_job_logs table.
