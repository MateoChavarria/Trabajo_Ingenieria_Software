-- ============================================================
-- ENDURECIMIENTO DE SEGURIDAD EN SUPABASE (version completa)
-- Ejecutar en: Supabase -> SQL Editor. Es seguro repetirlo.
--
-- El backend se conecta a PostgreSQL con el rol "postgres" (dueño de
-- las tablas), que no se ve afectado por RLS. En cambio, la API REST
-- publica de Supabase (PostgREST) usa los roles "anon" y
-- "authenticated" con la clave publica. Sin RLS, cualquiera con esa
-- clave y la URL del proyecto podria leer/modificar las tablas.
--
-- Este script activa RLS y retira los permisos de los roles publicos
-- en TODAS las tablas de la aplicacion (10). No se crean politicas:
-- la aplicacion NO usa la API REST de Supabase para datos, solo JDBC.
-- Si alguna tabla aun no existe en tu base, se omite sin error.
-- ============================================================

do $$
declare
  t text;
begin
  foreach t in array array[
    'usuarios', 'resultados_test', 'preguntas', 'opciones_respuesta',
    'carreras', 'universidades', 'semestres_carrera', 'materias_carrera',
    'temas_dificiles_materia', 'pagos'
  ]
  loop
    if to_regclass('public.' || t) is not null then
      execute format('alter table public.%I enable row level security', t);
      execute format('revoke all on table public.%I from anon, authenticated', t);
    end if;
  end loop;
end $$;

-- Correo unico sin importar mayusculas/minusculas.
create unique index if not exists usuarios_correo_unico_idx
    on public.usuarios (lower(correo));

-- Verificacion: las 10 filas deben mostrar rowsecurity = true.
-- Si falta alguna fila, esa tabla no existe todavia en tu base.
select tablename, rowsecurity
from pg_tables
where schemaname = 'public'
  and tablename in ('usuarios', 'resultados_test', 'preguntas', 'opciones_respuesta',
                    'carreras', 'universidades', 'semestres_carrera', 'materias_carrera',
                    'temas_dificiles_materia', 'pagos')
order by tablename;