-- ============================================================
-- ENDURECIMIENTO DE SEGURIDAD EN SUPABASE
-- Ejecutar UNA vez en: Supabase -> SQL Editor.
--
-- El backend se conecta a PostgreSQL con el rol "postgres" (dueño de
-- las tablas), que no se ve afectado por RLS. En cambio, la API REST
-- publica de Supabase (PostgREST) usa los roles "anon" y
-- "authenticated" con la clave publica. Sin RLS, cualquiera con esa
-- clave y la URL del proyecto podria leer/modificar las tablas.
--
-- Este script activa RLS en todas las tablas y retira los permisos de
-- los roles publicos. No se crean politicas: la aplicacion NO usa la
-- API REST de Supabase para datos, solo JDBC desde el backend.
-- ============================================================

alter table public.usuarios           enable row level security;
alter table public.resultados_test    enable row level security;
alter table public.preguntas          enable row level security;
alter table public.opciones_respuesta enable row level security;
alter table public.carreras           enable row level security;

revoke all on table public.usuarios           from anon, authenticated;
revoke all on table public.resultados_test    from anon, authenticated;
revoke all on table public.preguntas          from anon, authenticated;
revoke all on table public.opciones_respuesta from anon, authenticated;
revoke all on table public.carreras           from anon, authenticated;

-- Correo unico sin importar mayusculas/minusculas.
create unique index if not exists usuarios_correo_unico_idx
    on public.usuarios (lower(correo));

-- Verificacion: todas las filas deben mostrar rowsecurity = true.
select tablename, rowsecurity
from pg_tables
where schemaname = 'public'
  and tablename in ('usuarios', 'resultados_test', 'preguntas', 'opciones_respuesta', 'carreras');