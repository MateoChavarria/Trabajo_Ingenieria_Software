-- ============================================================
-- ESQUEMA BASE (Sprints 1 a 4): usuarios, preguntas,
-- opciones_respuesta y resultados_test.
--
-- IMPORTANTE: estas tablas ya existen en tu Supabase (se crearon en
-- los primeros sprints). Este script solo sirve para dejar el
-- repositorio completo o para montar una base NUEVA desde cero.
-- Todo usa "if not exists", asi que ejecutarlo en tu base actual
-- NO borra ni cambia nada.
--
-- Orden para una base nueva:
--   1) este script   2) carreras.sql   3) datos_prueba_preguntas.sql
--   4) seguridad_rls.sql   5) sprint7_*   6) sprint9_*   7) sprint10_*
-- (sprint10_01_pagos.sql agrega usuarios.acceso_completo.)
-- ============================================================

create table if not exists public.usuarios (
  id              uuid primary key,
  correo          text not null,
  fecha_registro  timestamp not null default now(),
  estado_cuenta   varchar(30) not null default 'activa'
);

create table if not exists public.preguntas (
  id         serial primary key,
  texto      text not null,
  categoria  text not null
);

create table if not exists public.opciones_respuesta (
  id           serial primary key,
  pregunta_id  int not null references public.preguntas (id) on delete cascade,
  texto        text not null,
  area         text not null,
  peso         int  not null check (peso > 0)
);

create table if not exists public.resultados_test (
  id          bigserial primary key,
  usuario_id  uuid not null references public.usuarios (id) on delete cascade,
  fecha       timestamp not null default now(),
  detalle     text not null
);

create index if not exists resultados_test_usuario_idx
  on public.resultados_test (usuario_id, fecha desc);
