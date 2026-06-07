-- Create sequences for ID generation
CREATE SEQUENCE IF NOT EXISTS _user_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS board_seq START WITH 1 INCREMENT BY 50;

-- Create _user table
CREATE TABLE IF NOT EXISTS _user (
    id INT DEFAULT NEXTVAL('_user_seq') PRIMARY KEY,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Create board table
CREATE TABLE IF NOT EXISTS board (
    id INT DEFAULT NEXTVAL('board_seq') PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    color VARCHAR(50) NOT NULL,
    owner_id INT NOT NULL,
    CONSTRAINT fk_board_owner FOREIGN KEY (owner_id) REFERENCES _user(id) ON DELETE NO ACTION
);

-- Create index on board.owner_id for better query performance
CREATE INDEX IF NOT EXISTS idx_board_owner_id ON board(owner_id);

