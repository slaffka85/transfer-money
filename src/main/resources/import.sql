DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS transfer_transaction;

CREATE TABLE account (
    id LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
    acc_number BIGINT NOT NULL,
    username VARCHAR(30) NOT NULL,
    balance DECIMAL(19,4) NOT NULL
);

CREATE UNIQUE INDEX acc_num_idx on account (acc_number);
CREATE INDEX acc_username_idx on account (username);

CREATE TABLE transfer_transaction (
    id LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
    acc_number_from BIGINT NOT NULL ,
    balance_before_from DECIMAL(19,4) NOT NULL ,
    balance_after_from DECIMAL(19,4) NOT NULL ,
    acc_number_to BIGINT NOT NULL ,
    balance_before_to DECIMAL(19,4) NOT NULL ,
    balance_after_to DECIMAL(19,4) NOT NULL ,
    amount DECIMAL(19,4) NOT NULL ,
    transaction_date TIMESTAMP NOT NULL
);

ALTER TABLE transfer_transaction ADD FOREIGN KEY (acc_number_from) REFERENCES account(acc_number);
ALTER TABLE transfer_transaction ADD FOREIGN KEY (acc_number_to) REFERENCES account(acc_number);

INSERT INTO account (acc_number, username, balance ) VALUES (1, 'v.tsapaev', 100.0000);
INSERT INTO account (acc_number, username, balance ) VALUES (2, 'tssv85', 500.0000);
INSERT INTO account (acc_number, username, balance ) VALUES (3, 'donaldtrump', 110000.0000);
INSERT INTO account (acc_number, username, balance ) VALUES (4, 'rambo', 1000.0000);