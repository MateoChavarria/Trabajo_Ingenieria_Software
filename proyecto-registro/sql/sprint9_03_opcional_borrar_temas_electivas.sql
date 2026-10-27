-- ============================================================
-- OPCIONAL (Sprint 9): borrar los temas dificiles de las materias
-- electivas y optativas, porque dependen de lo que escoja cada
-- estudiante. Despues de las 4 partes de temas hay 4216 filas en
-- temas_dificiles_materia; con este borrado quedan 3762.
-- Ejecutar solo si el equipo decidio aplicarlo. Es seguro repetirlo.
-- ============================================================

delete from temas_dificiles_materia t
using materias_carrera m
where t.materia_id = m.id
  and (
    translate(lower(m.nombre), 'áéíóúü', 'aeiouu') like '%electiv%'
    or translate(lower(m.nombre), 'áéíóúü', 'aeiouu') like '%optativ%'
  );

-- Verificacion
select count(*) as total_temas from temas_dificiles_materia;
