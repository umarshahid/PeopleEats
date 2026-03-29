CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    order_no INTEGER PRIMARY KEY AUTOINCREMENT,
    customer TEXT NOT NULL,
    item TEXT NOT NULL,
    price REAL NOT NULL,
    order_type TEXT NOT NULL,
    state TEXT NOT NULL,
    rider_name TEXT
);

CREATE TABLE IF NOT EXISTS feedback (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_no INTEGER NOT NULL,
    rider_name TEXT NOT NULL,
    rating INTEGER NOT NULL,
    comment TEXT,
    created_at TEXT NOT NULL
);
