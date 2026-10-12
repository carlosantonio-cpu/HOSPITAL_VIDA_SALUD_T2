USE [HOSPITAL_VIDA_SALUD]
GO
/****** Objeto: Table [dbo].[citas] Fecha de script: 11/10/2026 08:54:51 p. m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[citas](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[paciente_id] [bigint] NOT NULL,
	[especialidad] [nvarchar](100) NOT NULL,
	[fecha_hora] [datetime2](7) NOT NULL,
	[profesional_codigo] [nvarchar](50) NOT NULL,
	[motivo_administrativo] [nvarchar](200) NULL,
	[estado] [nvarchar](20) NOT NULL,
	[fecha_creacion] [datetime2](7) NOT NULL,
	[fecha_actualizacion] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_citas_profesional_fecha] UNIQUE NONCLUSTERED 
(
	[profesional_codigo] ASC,
	[fecha_hora] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Objeto: Table [dbo].[pacientes] Fecha de script: 11/10/2026 08:54:51 p. m. ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[pacientes](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[tipo_documento] [nvarchar](20) NOT NULL,
	[numero_documento] [nvarchar](30) NOT NULL,
	[nombres] [nvarchar](100) NOT NULL,
	[apellidos] [nvarchar](100) NOT NULL,
	[fecha_nacimiento] [date] NULL,
	[telefono] [nvarchar](30) NULL,
	[correo] [nvarchar](150) NULL,
	[direccion] [nvarchar](250) NULL,
	[fecha_registro] [datetime2](7) NOT NULL,
	[estado] [nvarchar](15) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_pacientes_documento] UNIQUE NONCLUSTERED 
(
	[tipo_documento] ASC,
	[numero_documento] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[citas] ADD  DEFAULT ('PENDIENTE') FOR [estado]
GO
ALTER TABLE [dbo].[citas] ADD  DEFAULT (sysdatetime()) FOR [fecha_creacion]
GO
ALTER TABLE [dbo].[citas] ADD  DEFAULT (sysdatetime()) FOR [fecha_actualizacion]
GO
ALTER TABLE [dbo].[pacientes] ADD  DEFAULT (sysdatetime()) FOR [fecha_registro]
GO
ALTER TABLE [dbo].[pacientes] ADD  DEFAULT ('ACTIVO') FOR [estado]
GO
ALTER TABLE [dbo].[citas]  WITH CHECK ADD  CONSTRAINT [CK_citas_estado] CHECK  (([estado]='AUSENTE' OR [estado]='CANCELADA' OR [estado]='ATENDIDA' OR [estado]='CONFIRMADA' OR [estado]='PENDIENTE'))
GO
ALTER TABLE [dbo].[citas] CHECK CONSTRAINT [CK_citas_estado]
GO
ALTER TABLE [dbo].[pacientes]  WITH CHECK ADD  CONSTRAINT [CK_pacientes_estado] CHECK  (([estado]='INACTIVO' OR [estado]='ACTIVO'))
GO
ALTER TABLE [dbo].[pacientes] CHECK CONSTRAINT [CK_pacientes_estado]
GO
