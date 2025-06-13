# Card Management System (CMS) Backend API

## Overview

This project is a backend-focused API-based **Card Management System (CMS)** designed for a fintech company. It manages three core entities: **Card**, **Account**, and **Transaction**.

Built with **Java Spring Boot** and **PostgreSQL**, the system provides RESTful endpoints to perform CRUD operations on accounts and cards, handle transaction processing with essential validation, and maintain accurate account balances.

The project also includes **Swagger** API documentation for easy testing and integration.

---

## Features

- **Card Management**
  - Create new cards
  - Activate or deactivate cards
  - Retrieve card details
- **Account Management**
  - Create, read, update, delete (CRUD) accounts
  - Retrieve account details
- **Transaction Processing**
  - Create transactions with validation
  - Validate card status and expiry
  - Validate account status and balance sufficiency for debits
  - Update account balance accordingly (credit or debit)
- **API Documentation**
  - Interactive Swagger UI available at `/swagger-ui.html`

---

## System Requirements

### Card Entity

| Field        | Type     | Description                          |
|--------------|----------|------------------------------------|
| `id`         | UUID     | Unique identifier for the card      |
| `status`     | String   | Card status (`ACTIVE`, `INACTIVE`) |
| `expiry`     | Date     | Card expiry date                    |
| `cardNumber` | String   | card number                         |

Operations:
- Create a new card
- Activate/deactivate a card
- Retrieve card details

---

### Account Entity

| Field     | Type       | Description                         |
|-----------|------------|-----------------------------------|
| `id`      | UUID       | Unique identifier for the account  |
| `status`  | String     | Account status (`ACTIVE`, `INACTIVE`) |
| `balance` | BigDecimal | Current account balance            |

Operations:
- CRUD operations on accounts
- Retrieve account details

---

### Transaction Entity

| Field              | Type       | Description                          |
|--------------------|------------|------------------------------------|
| `id`               | UUID       | Unique transaction identifier       |
| `transactionAmount` | BigDecimal | Transaction amount                  |
| `transactionDate`   | Timestamp  | Date and time of transaction        |
| `transactionType`   | String     | Type of transaction (`C` for Credit, `D` for Debit) |

Operations:
- Create a transaction with validations
- Amend account balance if validation passes

---

## Validation Logic

- **Card Eligibility:**
  - Card must be active (`status = ACTIVE`)
  - Card must not be expired (`expiry >= current date`)

- **Account Eligibility:**
  - Account must be active (`status = ACTIVE`)
  - Account must have sufficient balance for debit transactions

- **Balance Updates:**
  - Debit transactions decrease the account balance
  - Credit transactions increase the account balance

---

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL Database

### Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/cms-backend.git
   cd cms-backend
