-- Users table
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       full_name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50)
);

-- Accounts table
CREATE TABLE accounts (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL REFERENCES users(id),
                          account_number VARCHAR(50) UNIQUE NOT NULL,
                          balance NUMERIC(19,2) DEFAULT 0
);

-- Transactions table
CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              user_id BIGINT NOT NULL REFERENCES users(id),
                              sender_account_number VARCHAR(50) NOT NULL,
                              receiver_account_number VARCHAR(50) NOT NULL,
                              amount NUMERIC(19,2) NOT NULL,
                              type VARCHAR(50),
                              date DATE NOT NULL
);
