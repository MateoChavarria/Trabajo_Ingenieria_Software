-- ============================================================
-- Sprint 10: acceso completo y pagos (pasarela simulada)
-- Ejecutar UNA vez en: Supabase -> SQL Editor.
-- IMPORTANTE: correrlo ANTES de arrancar la aplicacion con el
-- codigo nuevo, porque Hibernate (ddl-auto=validate) revisa que
-- la tabla "pagos" y la columna "acceso_completo" existan.
-- ============================================================

-- 1) Campo que indica si el usuario ya desbloqueo todas las funciones.
--    Los usuarios que ya existen quedan en "false" (acceso gratuito).
alter table public.usuarios
  add column if not exists acceso_completo boolean not null default false;

-- 2) Tabla de pagos: un registro por cada intento de pago.
create table if not exists public.pagos (
  id                bigserial primary key,
  usuario_id        uuid        not null references public.usuarios (id) on delete cascade,
  monto             integer     not null check (monto > 0),            -- pesos colombianos (COP)
  estado            varchar(20) not null
                      check (estado in ('pendiente', 'exitoso', 'fallido', 'cancelado')),
  referencia        varchar(40) not null unique,                       -- codigo unico de la transaccion
  fecha             timestamp   not null default now(),                -- cuando se inicio el pago
  fecha_resolucion  timestamp,                                         -- cuando quedo exitoso, fallido o cancelado
  motivo_fallo      varchar(200)                                       -- solo si el pago fallo
);

create index if not exists pagos_usuario_idx on public.pagos (usuario_id);

-- 3) Seguridad: igual que el resto de tablas. El backend entra por el
--    pooler con su propio usuario; anon y authenticated no deben leerla
--    ni escribirla desde la API publica de Supabase.
alter table public.pagos enable row level security;
revoke all on table public.pagos from anon, authenticated;

-- 4) Verificacion: pagos debe mostrar rowsecurity = true, y la columna
--    acceso_completo debe aparecer con valor false para todos.
select tablename, rowsecurity
from pg_tables
where schemaname = 'public' and tablename = 'pagos';

select count(*) as usuarios_total,
       count(*) filter (where acceso_completo) as con_acceso_completo
from public.usuarios;
