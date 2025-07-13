-- 著者テーブル
CREATE TABLE authors (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL UNIQUE,
    date_of_birth DATE         NOT NULL
);

-- 書籍テーブル
CREATE TABLE books (
    id            BIGSERIAL PRIMARY KEY,
    title         VARCHAR(255) NOT NULL,
    price         NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    -- 0が未出版、1が出版済み
    status        INTEGER      NOT NULL CHECK (status IN (0, 1))
);

-- 書籍と著者の関連テーブル
CREATE TABLE book_authors (
    book_id   BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
);

CREATE INDEX idx_books_title ON books (title);
CREATE INDEX idx_authors_name ON authors (name);
CREATE INDEX idx_book_authors_book_id ON book_authors (book_id);
CREATE INDEX idx_book_authors_author_id ON book_authors (author_id);