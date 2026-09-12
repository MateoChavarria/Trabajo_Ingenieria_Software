-- ============================================================
-- DATOS DE PRUEBA: TEST VOCACIONAL
-- Orientador Vocacional UPB
-- ============================================================
-- Areas usadas (deben coincidir exactamente, sin mayusculas
-- distintas ni tildes inconsistentes, con las que se usen luego
-- para relacionar carreras en la Historia 5):
--   ingenieria, salud, administracion, diseño, ciencias_sociales, derecho
-- ============================================================

-- Limpia los datos anteriores y reinicia los IDs desde 1.
truncate table opciones_respuesta, preguntas restart identity cascade;

-- ------------------------------------------------------------
-- PREGUNTAS
-- ------------------------------------------------------------
insert into preguntas (texto, categoria) values
  ('¿Qué actividad te generaría más satisfacción en un día de trabajo?', 'intereses'),                          -- 1
  ('Si tuvieras que elegir un proyecto para la universidad, ¿cuál elegirías?', 'intereses'),                    -- 2
  ('¿Qué tipo de lectura disfrutas más?', 'intereses'),                                                          -- 3
  ('En un trabajo en equipo, ¿qué rol prefieres tomar?', 'habilidades'),                                         -- 4
  ('¿Qué te preocupa más al pensar en el futuro?', 'valores'),                                                   -- 5
  ('¿Qué actividad extracurricular te llamaría más la atención?', 'intereses'),                                  -- 6
  ('¿Cuál de estas frases te representa mejor?', 'valores'),                                                     -- 7
  ('Si vieras un problema en tu barrio o ciudad, ¿cómo te gustaría abordarlo?', 'valores'),                      -- 8
  ('¿Qué materia del colegio disfrutabas más?', 'intereses'),                                                    -- 9
  ('¿Qué te resulta más atractivo en una carrera?', 'intereses'),                                                -- 10
  ('¿Cómo prefieres resolver un conflicto?', 'habilidades'),                                                     -- 11
  ('¿Qué te gustaría estudiar más a fondo?', 'intereses'),                                                       -- 12
  ('Cuando ves una app o un producto nuevo, ¿qué es lo primero que evalúas?', 'habilidades'),                    -- 13
  ('¿Qué papel te gustaría tener en la sociedad?', 'valores'),                                                   -- 14
  ('Si pudieras liderar un proyecto, ¿cuál elegirías?', 'entorno_trabajo');                                      -- 15

-- ------------------------------------------------------------
-- OPCIONES DE RESPUESTA
-- ------------------------------------------------------------

-- Pregunta 1
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (1, 'Resolver un problema técnico complejo', 'ingenieria', 3),
  (1, 'Atender y ayudar a una persona que lo necesita', 'salud', 3),
  (1, 'Organizar y liderar un proyecto de principio a fin', 'administracion', 3);

-- Pregunta 2
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (2, 'Diseñar la interfaz visual de una aplicación', 'diseño', 3),
  (2, 'Investigar un fenómeno social en tu comunidad', 'ciencias_sociales', 3),
  (2, 'Analizar un caso legal y proponer una solución', 'derecho', 3);

-- Pregunta 3
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (3, 'Artículos sobre nuevas tecnologías', 'ingenieria', 2),
  (3, 'Historias sobre comportamiento humano y sociedad', 'ciencias_sociales', 2),
  (3, 'Noticias sobre economía y negocios', 'administracion', 2);

-- Pregunta 4
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (4, 'El que construye o programa la solución', 'ingenieria', 2),
  (4, 'El que organiza tareas y plazos', 'administracion', 2),
  (4, 'El que cuida que todos se sientan bien', 'salud', 2);

-- Pregunta 5
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (5, 'Que la tecnología resuelva problemas reales', 'ingenieria', 2),
  (5, 'Que se respeten los derechos de las personas', 'derecho', 3),
  (5, 'Que las personas tengan acceso a servicios de salud', 'salud', 3);

-- Pregunta 6
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (6, 'Un taller de diseño gráfico o de producto', 'diseño', 3),
  (6, 'Un grupo de debate o oratoria', 'derecho', 2),
  (6, 'Un semillero de investigación social', 'ciencias_sociales', 2);

-- Pregunta 7
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (7, 'Me gusta entender cómo funcionan las cosas por dentro', 'ingenieria', 3),
  (7, 'Me gusta entender por qué las personas actúan como actúan', 'ciencias_sociales', 3),
  (7, 'Me gusta entender cómo se maneja el dinero y los recursos', 'administracion', 3);

-- Pregunta 8
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (8, 'Diseñando una solución tecnológica', 'ingenieria', 2),
  (8, 'Proponiendo un cambio en las normas o políticas', 'derecho', 3),
  (8, 'Organizando a la comunidad para actuar', 'ciencias_sociales', 2);

-- Pregunta 9
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (9, 'Matemáticas y física', 'ingenieria', 3),
  (9, 'Biología', 'salud', 3),
  (9, 'Ciencias sociales o filosofía', 'ciencias_sociales', 2),
  (9, 'Arte', 'diseño', 2);

-- Pregunta 10
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (10, 'Crear productos innovadores', 'diseño', 3),
  (10, 'Ayudar a que las empresas funcionen mejor', 'administracion', 2),
  (10, 'Salvar o mejorar la calidad de vida de las personas', 'salud', 3);

-- Pregunta 11
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (11, 'Buscando un acuerdo legal o normativo', 'derecho', 3),
  (11, 'Analizando los datos disponibles antes de decidir', 'ingenieria', 2),
  (11, 'Escuchando a las partes y mediando', 'ciencias_sociales', 2);

-- Pregunta 12
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (12, 'Cómo se construyen los sistemas y las máquinas', 'ingenieria', 3),
  (12, 'Cómo se cuida la salud física y mental', 'salud', 3),
  (12, 'Cómo se administran los recursos de una organización', 'administracion', 3);

-- Pregunta 13
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (13, 'Qué tan bien se ve y qué tan fácil es de usar', 'diseño', 3),
  (13, 'Qué tan bien resuelve el problema técnico', 'ingenieria', 2),
  (13, 'Qué tan rentable sería para un negocio', 'administracion', 2);

-- Pregunta 14
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (14, 'Defender los derechos de quienes no tienen voz', 'derecho', 3),
  (14, 'Cuidar la salud de las comunidades', 'salud', 2),
  (14, 'Estudiar y proponer soluciones a problemas sociales', 'ciencias_sociales', 3);

-- Pregunta 15
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (15, 'Una startup de tecnología', 'ingenieria', 2),
  (15, 'Una fundación social', 'ciencias_sociales', 2),
  (15, 'Una empresa familiar', 'administracion', 3),
  (15, 'Un estudio de diseño', 'diseño', 2);
