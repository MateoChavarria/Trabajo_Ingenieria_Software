-- ============================================================
-- Sprint 9: temas mas dificiles por materia
-- Crea: temas_dificiles_materia
-- Cada materia tiene 2 o 3 temas (columna "orden" = 1, 2 o 3).
-- ============================================================

create table if not exists temas_dificiles_materia (
  id         serial primary key,
  materia_id int  not null references materias_carrera (id) on delete cascade,
  orden      int  not null check (orden between 1 and 3),
  tema       text not null check (length(trim(tema)) > 0),
  unique (materia_id, orden)
);

-- Seguridad: igual que el resto de tablas. El backend entra por el
-- pooler con su propio usuario; anon y authenticated no deben leerla
-- directamente desde la API publica de Supabase.
alter table temas_dificiles_materia enable row level security;
revoke all on table temas_dificiles_materia from anon, authenticated;

-- Verificacion: debe mostrar rowsecurity = true
select tablename, rowsecurity
from pg_tables
where schemaname = 'public' and tablename = 'temas_dificiles_materia';
