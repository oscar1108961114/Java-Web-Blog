CREATE DATABASE IF NOT EXISTS blog
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog;
CREATE TABLE article
(
	id  INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    author VARCHAR(50) ,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE personal_information
(
	id INT PRIMARY KEY DEFAULT 1 ,
    user_name VARCHAR(50) NOT NULL DEFAULT '',
    user_dream VARCHAR(200) NOT NULL DEFAULT '' ,
    introduction TEXT  NOT NULL
);
CREATE TABLE member
(
	id INT PRIMARY KEY DEFAULT 1 ,
    name VARCHAR(50) UNIQUE ,
    account VARCHAR(25), 
    password VARCHAR(200),
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP
);