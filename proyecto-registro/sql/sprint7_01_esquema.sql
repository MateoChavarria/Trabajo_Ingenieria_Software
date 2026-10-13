-- ============================================================
-- SPRINT 7 - PASO 1: esquema (tablas nuevas)
-- Crea: universidades, semestres_carrera, materias_carrera
-- Modifica: carreras (agrega la columna universidad_id)
-- Se puede ejecutar mas de una vez sin error (if not exists).
-- ============================================================

begin;

-- ------------------------------------------------------------
-- 1. Universidades de Antioquia
--    municipio: ciudad(es) o sede(s) tal como aparece en la fuente.
--    tiene_informacion_completa: true solo si la app tiene las
--    carreras y el pensum de esa universidad (hoy: solo la UPB).
-- ------------------------------------------------------------
create table if not exists universidades (
  id serial primary key,
  nombre text not null unique,
  caracter text not null check (caracter in ('Pública', 'Privada')),
  municipio text not null,
  tiene_informacion_completa boolean not null default false
);

-- ------------------------------------------------------------
-- 2. Cada carrera pertenece a una universidad
-- ------------------------------------------------------------
alter table carreras
  add column if not exists universidad_id int references universidades(id);

create index if not exists carreras_universidad_idx on carreras (universidad_id);

-- ------------------------------------------------------------
-- 3. Semestres de cada carrera (enfoque y descripcion)
-- ------------------------------------------------------------
create table if not exists semestres_carrera (
  id serial primary key,
  carrera_id int not null references carreras(id) on delete cascade,
  numero int not null check (numero > 0),
  enfoque_principal text not null,
  descripcion text not null,
  unique (carrera_id, numero)
);

-- ------------------------------------------------------------
-- 4. Materias de cada semestre (nombre, creditos y enfoque)
-- ------------------------------------------------------------
create table if not exists materias_carrera (
  id serial primary key,
  semestre_id int not null references semestres_carrera(id) on delete cascade,
  nombre text not null,
  creditos int not null check (creditos >= 0),
  enfoque_tematico text not null
);

create index if not exists materias_semestre_idx on materias_carrera (semestre_id);

-- ------------------------------------------------------------
-- 5. Seguridad: igual que el resto de tablas (ver seguridad_rls.sql).
--    El backend entra por JDBC con el rol dueño, asi que no se afecta.
-- ------------------------------------------------------------
alter table universidades     enable row level security;
alter table semestres_carrera enable row level security;
alter table materias_carrera  enable row level security;

revoke all on table universidades     from anon, authenticated;
revoke all on table semestres_carrera from anon, authenticated;
revoke all on table materias_carrera  from anon, authenticated;

commit;

-- Verificacion: las 3 tablas deben aparecer con rowsecurity = true
select tablename, rowsecurity
from pg_tables
where schemaname = 'public'
  and tablename in ('universidades', 'semestres_carrera', 'materias_carrera');
