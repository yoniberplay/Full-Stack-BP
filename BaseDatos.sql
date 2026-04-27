CREATE TABLE dbo.personas (
    persona_id     BIGINT         NOT NULL IDENTITY(1, 1),
    dtype          NVARCHAR(31)   NOT NULL,
    nombre         NVARCHAR(255)  NOT NULL,
    genero         NVARCHAR(255)  NOT NULL,
    edad           INT            NOT NULL,
    identificacion NVARCHAR(255)  NOT NULL,
    direccion      NVARCHAR(255)  NOT NULL,
    telefono       NVARCHAR(255)  NOT NULL,
    contrasena     NVARCHAR(255)  NULL,
    estado         BIT            NULL,
    CONSTRAINT PK_personas PRIMARY KEY (persona_id),
    CONSTRAINT UQ_personas_identificacion UNIQUE (identificacion)
);
GO

CREATE TABLE dbo.cuentas (
    cuenta_id             BIGINT         NOT NULL IDENTITY(1, 1),
    numero_cuenta         NVARCHAR(255)  NOT NULL,
    tipo_cuenta           NVARCHAR(255)  NOT NULL, 
    saldo_inicial         DECIMAL(19, 2) NOT NULL,
    saldo_disponible      DECIMAL(19, 2) NOT NULL,
    estado                BIT            NOT NULL,
    limite_diario_retiro  DECIMAL(19, 2) NULL,
    total_retiros_dia     DECIMAL(19, 2) NOT NULL
        CONSTRAINT DF_cuentas_total_retiros_dia DEFAULT 0,
    persona_id            BIGINT         NOT NULL,
    CONSTRAINT PK_cuentas PRIMARY KEY (cuenta_id),
    CONSTRAINT UQ_cuentas_numero_cuenta UNIQUE (numero_cuenta),
    CONSTRAINT FK_cuentas_persona FOREIGN KEY (persona_id)
        REFERENCES dbo.personas (persona_id)
);
GO

CREATE INDEX IX_cuentas_persona_id ON dbo.cuentas (persona_id);
GO

CREATE TABLE dbo.movimientos (
    movimiento_id  BIGINT         NOT NULL IDENTITY(1, 1),
    fecha          DATETIME2      NOT NULL,
    tipo_movimiento NVARCHAR(255) NOT NULL,
    valor          DECIMAL(19, 2) NOT NULL,
    saldo          DECIMAL(19, 2) NOT NULL,
    cuenta_id      BIGINT         NOT NULL,
    CONSTRAINT PK_movimientos PRIMARY KEY (movimiento_id),
    CONSTRAINT FK_movimientos_cuenta FOREIGN KEY (cuenta_id)
        REFERENCES dbo.cuentas (cuenta_id)
);
GO

CREATE INDEX IX_movimientos_cuenta_id ON dbo.movimientos (cuenta_id);
GO
