-- ============================================================
-- Sprint 11: cada usuario de Authentication tiene su fila en "usuarios"
-- ============================================================
-- Problema: un usuario creado desde Supabase (Authentication > Add user)
-- no pasa por el registro de la aplicacion, asi que no tenia fila en la tabla
-- "usuarios". Sin esa fila, iniciar un pago fallaba ("No se pudo iniciar el pago.")
-- porque "pagos" apunta a "usuarios".
--
-- Solucion: un trigger crea la fila automaticamente cuando nace el usuario
-- en auth.users, sin importar como se creo (aplicacion o panel de Supabase).
-- Ejecutar UNA vez en Supabase > SQL Editor.
-- ============================================================

-- 1. Funcion que copia el usuario nuevo a la tabla "usuarios".
create or replace function public.crear_fila_usuario()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  insert into public.usuarios (id, correo, fecha_registro, estado_cuenta, acceso_completo)
  values (new.id, lower(new.email), now(), 'activo', false)
  on conflict (id) do nothing;
  return new;
end;
$$;

-- 2. Trigger: se ejecuta despues de crear cada usuario en auth.users.
drop trigger if exists al_crear_usuario_auth on auth.users;
create trigger al_crear_usuario_auth
  after insert on auth.users
  for each row execute function public.crear_fila_usuario();

-- 3. Usuarios que ya existian sin fila (los creados antes del trigger).
insert into public.usuarios (id, correo, fecha_registro, estado_cuenta, acceso_completo)
select a.id, lower(a.email), now(), 'activo', false
from auth.users a
left join public.usuarios u on u.id = a.id
where u.id is null
  and a.email is not null
on conflict do nothing;

-- 4. Verificacion: ambos numeros deben ser iguales y "sin_fila" debe ser 0.
select
  (select count(*) from auth.users)                                   as usuarios_authentication,
  (select count(*) from public.usuarios)                              as usuarios_en_tabla,
  (select count(*) from auth.users a
     left join public.usuarios u on u.id = a.id
   where u.id is null)                                                as sin_fila;
