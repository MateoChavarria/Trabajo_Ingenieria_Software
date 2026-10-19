-- ============================================================
-- Sprint 10: consultas utiles para pruebas y demostracion.
-- NO es obligatorio ejecutarlo. Correr cada bloque por separado.
-- ============================================================

-- Ver los ultimos pagos con el correo de quien pago.
select p.fecha, u.correo, p.monto, p.estado, p.referencia, p.motivo_fallo
from public.pagos p
join public.usuarios u on u.id = p.usuario_id
order by p.fecha desc
limit 20;

-- Volver a un usuario al estado gratuito (para repetir la demo del pago).
-- Cambiar el correo por el de la cuenta de prueba.
update public.usuarios set acceso_completo = false where correo = 'tu_correo@ejemplo.com';

-- Dar acceso completo sin pasar por la pasarela (solo para pruebas).
update public.usuarios set acceso_completo = true where correo = 'tu_correo@ejemplo.com';
