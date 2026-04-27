#!/bin/bash
set -e

# Start SQL Server in the background
/opt/mssql/bin/sqlservr &
MSSQL_PID=$!

echo ">>> Waiting for SQL Server to accept connections..."
for i in $(seq 1 60); do
    /opt/mssql-tools18/bin/sqlcmd -S localhost -U SA -P "$SA_PASSWORD" -Q "SELECT 1" > /dev/null 2>&1 && break
    echo "    Attempt $i/60 — not ready yet, retrying in 3s..."
    sleep 3
done

echo ">>> SQL Server is ready. Creating database 'devsudb' if it does not exist..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U SA -P "$SA_PASSWORD" -Q \
    "IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'devsudb') CREATE DATABASE devsudb"

echo ">>> Database initialization complete."

# Hand control back to SQL Server process
wait $MSSQL_PID
