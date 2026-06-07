-- Create _user table
CREATE TABLE IF NOT EXISTS _user (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Create board table
CREATE TABLE IF NOT EXISTS board (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    color VARCHAR(50) NOT NULL,
    owner_id INTEGER NOT NULL,
    CONSTRAINT fk_board_owner FOREIGN KEY (owner_id) REFERENCES _user(id) ON DELETE NO ACTION
);

-- Create index on board.owner_id for better query performance
CREATE INDEX idx_board_owner_id ON board(owner_id);

