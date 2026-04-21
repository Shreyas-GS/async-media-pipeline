# 🚀 Enterprise Asynchronous Media Pipeline

An event-driven, horizontally scalable Spring Boot microservice designed to handle long-running, CPU-intensive tasks (such as AI video rendering or bulk media processing).

This system avoids server bottlenecks by offloading heavy workloads to background workers while keeping clients updated in real-time.

---

## 🧠 Problem & Solution

### ❌ The Problem (Synchronous APIs)

In a traditional REST setup:

* Each request holds a thread until processing completes
* Long tasks (like video rendering) block the server
* High load → thread exhaustion → crashes or dropped requests

---

### ✅ The Solution (Asynchronous Architecture)

This system decouples request handling from processing:

1. API receives request and immediately queues it
2. Background workers process jobs independently
3. Client receives real-time updates via streaming

---

## 🏗️ Architecture Flow

1. **Client Request**
   Sends media processing request to API

2. **Database (PostgreSQL)**
   Saves job with status `PENDING`

3. **Message Broker (RabbitMQ)**
   Publishes job ID to queue

4. **Worker Service**

   * Consumes job
   * Updates status → `PROCESSING`
   * Executes heavy task

5. **Real-Time Updates (SSE)**
   Client subscribes using Job ID and receives progress updates

---

## 🛠️ Tech Stack

* **Java 17 + Spring Boot** → Core backend framework
* **RabbitMQ** → Message broker for async processing
* **PostgreSQL** → Persistent job storage
* **Spring Data JPA + HikariCP** → Efficient DB access
* **Server-Sent Events (SSE)** → Real-time progress updates
* **Docker & Docker Compose** → Containerized environment

---

## 💻 API Usage

### 1. Submit Render Job

```bash
curl -X POST http://localhost:8080/api/v1/jobs/render \
-H "Content-Type: application/json" \
-d '{"prompt": "Cinematic 4K drone shot", "resolution": "4K"}'
```

**Response (202 Accepted)**

```json
{
  "message": "Job accepted and queued for processing",
  "jobId": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
  "statusUrl": "/api/v1/jobs/stream/a1b2c3d4-e5f6-7890-abcd-1234567890ab"
}
```

---

### 2. Stream Live Progress

```bash
curl -N http://localhost:8080/api/v1/jobs/stream/a1b2c3d4-e5f6-7890-abcd-1234567890ab
```

**Response Stream**

```text
data: {"jobId": "a1b2c3d4...", "status": "PROCESSING", "progress": 10}
data: {"jobId": "a1b2c3d4...", "status": "PROCESSING", "progress": 45}
data: {"jobId": "a1b2c3d4...", "status": "COMPLETED", "progress": 100}
```

---

## 🚀 Running Locally

### Prerequisites

* Docker & Docker Compose
* Java 17+
* Maven

---

### 1. Clone Repository

```bash
git clone https://github.com/your-username/async-media-pipeline.git
cd async-media-pipeline
```

---

### 2. Start Infrastructure

```bash
docker-compose up -d
```

Starts:

* PostgreSQL
* RabbitMQ

---

### 3. Run Application

```bash
./mvnw spring-boot:run
```

App will be available at:

```
http://localhost:8080
```

---

## 🧠 Key Design Decisions

* **Asynchronous Processing** → Prevents blocking threads
* **RabbitMQ over Polling** → Efficient task distribution
* **SSE over WebSockets** → Simpler one-way communication
* **Dockerized Setup** → Consistent local + production environment

---

## 📌 Summary

This system is designed to:

* Handle high concurrency
* Process heavy workloads efficiently
* Provide real-time feedback
* Scale horizontally with ease

---

## 👨‍💻 Author

Built with focus on performance, scalability, and clean architecture.
