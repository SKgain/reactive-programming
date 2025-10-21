ALTER TABLE transactions
    ADD COLUMN transaction_number VARCHAR(100) UNIQUE NOT NULL DEFAULT concat('TXN', extract(epoch from now())::bigint);
