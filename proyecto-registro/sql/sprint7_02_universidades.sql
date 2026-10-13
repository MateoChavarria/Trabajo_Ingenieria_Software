-- ============================================================
-- SPRINT 7 - PASO 2: datos de universidades de Antioquia
-- Fuente: universidades_antioquia.docx (57 instituciones).
-- Solo la UPB tiene informacion completa (carreras y pensum).
-- Se puede ejecutar mas de una vez sin duplicar (on conflict).
-- ============================================================

begin;

insert into universidades (nombre, caracter, municipio, tiene_informacion_completa) values
  ('Colegio Mayor de Antioquía (COLMAYOR)', 'Pública', 'Medellín', false),
  ('Universidad de Antioquia (UDEA)', 'Pública', 'Medellín', false),
  ('Politécnico Colombiano Jaime Isaza Cadavid', 'Pública', 'Medellín, Bello, Niquía, Urabá', false),
  ('Instituto Tecnológico Metropolitano (ITM)', 'Pública', 'Medellín', false),
  ('Institución Universitaria de Envigado (IUE)', 'Pública', 'Envigado', false),
  ('Universidad Nacional Abierta y a Distancia (UNAD)', 'Pública', 'No especificada', false),
  ('Tecnológico de Antioquía (TDEA)', 'Privada', 'Medellín, Copacabana, Itagüí', false),
  ('Universidad Autónoma Latinoamericana (UNAULA)', 'Privada', 'Medellín', false),
  ('Universidad Pontificia Bolivariana (UPB)', 'Privada', 'Medellín', true),
  ('Universidad Católica de Oriente (UCO)', 'Privada', 'Medellín, Rionegro', false),
  ('Universidad de Medellín (UDEM)', 'Privada', 'Medellín', false),
  ('Universidad CES', 'Privada', 'Apartadó, Envigado, Medellín, Sabaneta', false),
  ('Universidad EAFIT', 'Privada', 'Medellín, Rionegro', false),
  ('Corporación Universitaria de Ciencia y Tecnología de Colombia', 'Privada', 'Medellín', false),
  ('Corporación Universitaria Lasallista', 'Privada', 'Caldas', false),
  ('Corporación Tecnológica Católica de Occidente (TECOC)', 'Privada', 'Santa Fe de Antioquía', false),
  ('Corporación Academia Superior de Artes', 'Privada', 'Medellín', false),
  ('Corporación Tecnológico Copacabana (TECO)', 'Privada', 'Copacabana', false),
  ('Corporación Universitaria Americana', 'Privada', 'Medellín', false),
  ('Universidad Antonio Nariño (UAN)', 'Privada', 'Medellín', false),
  ('Corporación Universitaria de Sabaneta J. E. Valderrama', 'Privada', 'Sabaneta', false),
  ('Corporación Politécnico Marco Fidel Suárez', 'Privada', 'Bello', false),
  ('Corporación Universitaria Remington', 'Privada', 'Apartadó, Medellín, Rionegro', false),
  ('Corporación Universitaria Adventista (UNAC)', 'Privada', 'Medellín', false),
  ('Corporación de Estudios Superiores Salamandra', 'Privada', 'Medellín', false),
  ('Escuela Colombiana de Carreras Industriales (ECCI)', 'Privada', 'Medellín', false),
  ('Fundación Tecnológica Rural COREDI', 'Privada', 'Marinilla', false),
  ('Fundación Universitaria San Martín', 'Privada', 'Sabaneta', false),
  ('Politécnico Grancolombiano', 'Privada', 'Medellín', false),
  ('Universidad de San Buenaventura (USB)', 'Privada', 'Medellín, Bello', false),
  ('Servicio Nacional de Aprendizaje (SENA)', 'Privada', 'Medellín', false),
  ('Corporación para Investigaciones Biológicas (CIB)', 'Privada', 'Medellín', false),
  ('Centro Educacional de Cómputos y Sistemas', 'Privada', 'Medellín', false),
  ('Fundación de Estudios Superiores Universitaria de Urabá (FESU)', 'Privada', 'Apartadó', false),
  ('Fundación Universitaria Católica del Norte (UCN)', 'Privada', 'Medellín, Santa Rosa de Osos', false),
  ('Fundación Universitaria Seminario Bíblico de Colombia', 'Privada', 'Medellín', false),
  ('Fundación Universitaria Bellas Artes de Medellín', 'Privada', 'Medellín', false),
  ('Fundación Universitaria Autónoma de las Américas', 'Privada', 'Medellín', false),
  ('Universidad Católica Luis Amigó', 'Privada', 'Medellín, Apartadó', false),
  ('Escuela de Ingeniería de Antioquia (EIA)', 'Privada', 'Envigado', false),
  ('Escuela Universitaria de Mercadotecnia (ESCOLME)', 'Privada', 'Medellín', false),
  ('Institución Universitaria Esumer', 'Privada', 'Medellín', false),
  ('Institución Universitaria Pascual Bravo', 'Privada', 'Medellín', false),
  ('Instituto Superior IDEARTES', 'Privada', 'Medellín', false),
  ('Institución Universitaria Colegiatura Colombiana', 'Privada', 'Medellín', false),
  ('Institución Universitaria Ceipa', 'Privada', 'Sabaneta, Medellín', false),
  ('Institución Universitaria Salazar y Herrera (IUSH)', 'Privada', 'Medellín', false),
  ('Corporación Academia Tecnológica de Colombia', 'Privada', 'No especificada', false),
  ('Corporación Universitaria IDEAS', 'Privada', 'Medellín', false),
  ('Politécnico de Antioquía', 'Privada', 'Medellín', false),
  ('Centro de Comercio Regional Antioquía', 'Privada', 'Medellín', false),
  ('Escuela Superior Tecnológica de Artes Débora Arango', 'Privada', 'Envigado', false),
  ('Corporación Universitaria Minuto de Dios', 'Privada', 'No especificada', false),
  ('Escuela de Tecnologías de Antioquia', 'Privada', 'Medellín', false),
  ('Fundación Universitaria María Cano', 'Privada', 'Medellín', false),
  ('Universidad Santo Tomás (USTA)', 'Privada', 'Medellín', false),
  ('Universidad Cooperativa de Colombia (UCC)', 'Privada', 'Apartadó, Medellín', false)
on conflict (nombre) do nothing;

-- Las 37 carreras que ya existen en "carreras" son de la UPB Medellin.
update carreras
   set universidad_id = (select id from universidades
                         where nombre = 'Universidad Pontificia Bolivariana (UPB)')
 where universidad_id is null;

commit;

-- Verificacion (debe devolver 57 y 37):
select count(*) as universidades from universidades;
select count(*) as carreras_con_universidad from carreras where universidad_id is not null;
