-- ============================================================
-- DATOS DE PRUEBA: TEST VOCACIONAL (version final, 53 preguntas, 8 areas)
-- Orientador Vocacional UPB
-- ============================================================
-- 8 areas (deben coincidir exactamente con area_categoria en la tabla carreras):
--   ingenieria, salud, administracion, diseño, ciencias_sociales, derecho, educacion, humanidades
-- (nota historica, ya no aplica tal cual, se deja la linea original abajo por continuidad)
-- Areas usadas (deben coincidir exactamente con las que usaran
-- luego para relacionar carreras en la Historia 5):
--   ingenieria, salud, administracion, diseño, ciencias_sociales, derecho
-- ============================================================

truncate table opciones_respuesta, preguntas restart identity cascade;

-- ------------------------------------------------------------
-- PREGUNTAS 1-45
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
  ('Si pudieras liderar un proyecto, ¿cuál elegirías?', 'entorno_trabajo'),                                      -- 15
  ('¿Qué documental verías con más gusto un fin de semana?', 'intereses'),                                       -- 16
  ('¿Qué te generaría más orgullo al terminar tu carrera?', 'valores'),                                          -- 17
  ('En un examen final, ¿qué tipo de pregunta prefieres?', 'habilidades'),                                       -- 18
  ('¿Qué situación te parece más injusta?', 'valores'),                                                          -- 19
  ('Si te regalaran un curso corto, ¿cuál elegirías?', 'intereses'),                                             -- 20
  ('¿Qué actividad harías gratis, solo por diversión?', 'intereses'),                                            -- 21
  ('¿Cómo te gustaría que te recordaran tus compañeros de trabajo?', 'valores'),                                 -- 22
  ('¿Qué tipo de problema disfrutas más resolver?', 'habilidades'),                                              -- 23
  ('¿Qué te llamaría más la atención en una feria de universidades?', 'intereses'),                              -- 24
  ('¿En qué tipo de ambiente de trabajo te imaginas más cómodo?', 'entorno_trabajo'),                            -- 25
  ('¿Qué habilidad te gustaría dominar en los próximos años?', 'habilidades'),                                   -- 26
  ('¿Qué noticia te llama más la atención cuando la ves en el celular?', 'intereses'),                           -- 27
  ('Si formaras parte de un proyecto social, ¿qué rol tomarías?', 'valores'),                                    -- 28
  ('¿Qué tipo de reto te motiva más?', 'habilidades'),                                                           -- 29
  ('¿Qué te gustaría que dijera tu hoja de vida en 10 años?', 'valores'),                                        -- 30
  ('¿Qué tipo de video de YouTube ves más seguido?', 'intereses'),                                               -- 31
  ('¿Qué te parece más interesante de una ciudad?', 'intereses'),                                                -- 32
  ('En un grupo de estudio, ¿qué tarea prefieres hacer?', 'habilidades'),                                        -- 33
  ('¿Qué tipo de negocio te gustaría emprender algún día?', 'entorno_trabajo'),                                  -- 34
  ('¿Qué situación cotidiana te gustaría poder mejorar?', 'valores'),                                            -- 35
  ('¿Qué te llama más la atención de una empresa cuando investigas sobre ella?', 'intereses'),                   -- 36
  ('¿Cómo prefieres aprender algo nuevo?', 'habilidades'),                                                       -- 37
  ('¿Qué tipo de evento cultural disfrutarías más?', 'intereses'),                                                -- 38
  ('Si tuvieras que dar una charla, ¿sobre qué tema te sentirías más seguro?', 'habilidades'),                   -- 39
  ('¿Qué tipo de impacto te gustaría dejar en tu comunidad?', 'valores'),                                        -- 40
  ('¿Qué actividad de voluntariado elegirías?', 'valores'),                                                      -- 41
  ('¿Qué app usarías más si tuvieras que crear una?', 'intereses'),                                              -- 42
  ('¿Qué te gustaría que la tecnología resolviera en el futuro?', 'intereses'),                                  -- 43
  ('¿Qué tipo de decisión se te hace más fácil de tomar?', 'habilidades'),                                       -- 44
  ('¿Con qué frase te identificas más al pensar en tu futuro laboral?', 'valores'),  -- 45
  ('¿Qué actividad te gustaría hacer en tu tiempo libre entre semana?', 'intereses'),             -- 46
  ('¿Qué tipo de libro comprarías primero en una librería?', 'intereses'),                        -- 47
  ('¿Qué rol tomarías en un grupo juvenil o comunitario?', 'valores'),                             -- 48
  ('¿Qué pregunta te parece más interesante de responder?', 'intereses'),                         -- 49
  ('¿Qué actividad de voluntariado te llamaría más la atención?', 'valores'),                     -- 50
  ('¿Qué tema elegirías para escribir un ensayo?', 'habilidades'),                                -- 51
  ('¿Qué te gustaría lograr como profesional en el futuro?', 'valores'),                          -- 52
  ('¿Qué actividad disfrutarías más en un intercambio cultural?', 'intereses');                   -- 53

-- ------------------------------------------------------------
-- OPCIONES DE RESPUESTA 1-15
-- ------------------------------------------------------------
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (1, 'Resolver un problema técnico complejo', 'ingenieria', 3),
  (1, 'Atender y ayudar a una persona que lo necesita', 'salud', 3),
  (1, 'Organizar y liderar un proyecto de principio a fin', 'administracion', 3);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (2, 'Diseñar la interfaz visual de una aplicación', 'diseño', 3),
  (2, 'Investigar un fenómeno social en tu comunidad', 'ciencias_sociales', 3),
  (2, 'Analizar un caso legal y proponer una solución', 'derecho', 3);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (3, 'Artículos sobre nuevas tecnologías', 'ingenieria', 2),
  (3, 'Historias sobre comportamiento humano y sociedad', 'ciencias_sociales', 2),
  (3, 'Noticias sobre economía y negocios', 'administracion', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (4, 'El que construye o programa la solución', 'ingenieria', 2),
  (4, 'El que organiza tareas y plazos', 'administracion', 2),
  (4, 'El que cuida que todos se sientan bien', 'salud', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (5, 'Que la tecnología resuelva problemas reales', 'ingenieria', 2),
  (5, 'Que se respeten los derechos de las personas', 'derecho', 3),
  (5, 'Que las personas tengan acceso a servicios de salud', 'salud', 3);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (6, 'Un taller de diseño gráfico o de producto', 'diseño', 3),
  (6, 'Un grupo de debate o oratoria', 'derecho', 2),
  (6, 'Un semillero de investigación social', 'ciencias_sociales', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (7, 'Me gusta entender cómo funcionan las cosas por dentro', 'ingenieria', 3),
  (7, 'Me gusta entender por qué las personas actúan como actúan', 'ciencias_sociales', 3),
  (7, 'Me gusta entender cómo se maneja el dinero y los recursos', 'administracion', 3);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (8, 'Diseñando una solución tecnológica', 'ingenieria', 2),
  (8, 'Proponiendo un cambio en las normas o políticas', 'derecho', 3),
  (8, 'Organizando a la comunidad para actuar', 'ciencias_sociales', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (9, 'Matemáticas y física', 'ingenieria', 3),
  (9, 'Biología', 'salud', 3),
  (9, 'Ciencias sociales o filosofía', 'ciencias_sociales', 2),
  (9, 'Arte', 'diseño', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (10, 'Crear productos innovadores', 'diseño', 3),
  (10, 'Ayudar a que las empresas funcionen mejor', 'administracion', 2),
  (10, 'Salvar o mejorar la calidad de vida de las personas', 'salud', 3);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (11, 'Buscando un acuerdo legal o normativo', 'derecho', 3),
  (11, 'Analizando los datos disponibles antes de decidir', 'ingenieria', 2),
  (11, 'Escuchando a las partes y mediando', 'ciencias_sociales', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (12, 'Cómo se construyen los sistemas y las máquinas', 'ingenieria', 3),
  (12, 'Cómo se cuida la salud física y mental', 'salud', 3),
  (12, 'Cómo se administran los recursos de una organización', 'administracion', 3);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (13, 'Qué tan bien se ve y qué tan fácil es de usar', 'diseño', 3),
  (13, 'Qué tan bien resuelve el problema técnico', 'ingenieria', 2),
  (13, 'Qué tan rentable sería para un negocio', 'administracion', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (14, 'Defender los derechos de quienes no tienen voz', 'derecho', 3),
  (14, 'Cuidar la salud de las comunidades', 'salud', 2),
  (14, 'Estudiar y proponer soluciones a problemas sociales', 'ciencias_sociales', 3);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (15, 'Una startup de tecnología', 'ingenieria', 2),
  (15, 'Una fundación social', 'ciencias_sociales', 2),
  (15, 'Una empresa familiar', 'administracion', 3),
  (15, 'Un estudio de diseño', 'diseño', 2);

-- ------------------------------------------------------------
-- OPCIONES DE RESPUESTA 16-45 (nuevas)
-- ------------------------------------------------------------
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (16, 'Uno sobre avances tecnológicos o espacio', 'ingenieria', 2),
  (16, 'Uno sobre salud, medicina o el cuerpo humano', 'salud', 3),
  (16, 'Uno sobre historia, sociedad o cultura', 'ciencias_sociales', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (17, 'Haber creado algo que la gente usa todos los días', 'ingenieria', 3),
  (17, 'Haber ayudado a sanar o cuidar a alguien', 'salud', 3),
  (17, 'Haber liderado un equipo exitoso', 'administracion', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (18, 'Ejercicios con procedimientos claros y una sola respuesta correcta', 'ingenieria', 3),
  (18, 'Preguntas de análisis y argumentación', 'derecho', 3),
  (18, 'Preguntas sobre casos reales de personas o comunidades', 'ciencias_sociales', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (19, 'Que la tecnología no llegue a todos por igual', 'ingenieria', 2),
  (19, 'Que alguien no reciba atención médica cuando la necesita', 'salud', 3),
  (19, 'Que a alguien no se le respeten sus derechos', 'derecho', 3);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (20, 'Programación o desarrollo de software', 'ingenieria', 3),
  (20, 'Primeros auxilios o cuidado de la salud', 'salud', 3),
  (20, 'Finanzas personales o administración de negocios', 'administracion', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (21, 'Armar o arreglar cosas con las manos', 'ingenieria', 2),
  (21, 'Dibujar, diseñar o crear cosas visuales', 'diseño', 3),
  (21, 'Escuchar y aconsejar a mis amigos', 'ciencias_sociales', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (22, 'Como alguien que resolvía problemas técnicos difíciles', 'ingenieria', 3),
  (22, 'Como alguien confiable que organizaba bien las cosas', 'administracion', 2),
  (22, 'Como alguien que siempre defendía lo justo', 'derecho', 3);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (23, 'Uno con una solución técnica exacta', 'ingenieria', 3),
  (23, 'Uno que involucra entender a varias personas con intereses distintos', 'ciencias_sociales', 2),
  (23, 'Uno donde hay que decidir cómo repartir recursos limitados', 'administracion', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (24, 'El stand de ingeniería con robots o prototipos', 'ingenieria', 3),
  (24, 'El stand de ciencias de la salud', 'salud', 3),
  (24, 'El stand de diseño con proyectos creativos', 'diseño', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (25, 'Un laboratorio o taller técnico', 'ingenieria', 2),
  (25, 'Un hospital, clínica o centro de salud', 'salud', 3),
  (25, 'Una oficina dirigiendo un equipo', 'administracion', 2),
  (25, 'Un estudio creativo o de diseño', 'diseño', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (26, 'Programar o desarrollar tecnología', 'ingenieria', 3),
  (26, 'Hablar en público y persuadir', 'derecho', 2),
  (26, 'Diseñar experiencias o productos visuales', 'diseño', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (27, 'Un nuevo avance en inteligencia artificial', 'ingenieria', 3),
  (27, 'Un descubrimiento médico', 'salud', 3),
  (27, 'Un cambio en una ley o política pública', 'derecho', 3);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (28, 'El que diseña la estrategia y consigue recursos', 'administracion', 3),
  (28, 'El que atiende directamente a las personas beneficiadas', 'salud', 2),
  (28, 'El que investiga el problema social de fondo', 'ciencias_sociales', 3);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (29, 'Resolver un problema técnico que nadie más pudo resolver', 'ingenieria', 3),
  (29, 'Ayudar a alguien en una situación difícil de salud', 'salud', 3),
  (29, 'Sacar adelante un negocio en crisis', 'administracion', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (30, 'Que construí tecnología que mejoró la vida de otros', 'ingenieria', 3),
  (30, 'Que ayudé a que muchas personas estuvieran más sanas', 'salud', 3),
  (30, 'Que dirigí una organización exitosa', 'administracion', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (31, 'Reseñas o tutoriales de tecnología', 'ingenieria', 2),
  (31, 'Contenido sobre bienestar y salud', 'salud', 2),
  (31, 'Documentales sociales o de opinión', 'ciencias_sociales', 2),
  (31, 'Contenido de diseño, arte o moda', 'diseño', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (32, 'Su infraestructura y desarrollo tecnológico', 'ingenieria', 2),
  (32, 'Su sistema de salud pública', 'salud', 2),
  (32, 'Su dinámica social y cultural', 'ciencias_sociales', 3),
  (32, 'Su arquitectura y diseño urbano', 'diseño', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (33, 'Resolver los ejercicios más técnicos', 'ingenieria', 2),
  (33, 'Organizar el cronograma del grupo', 'administracion', 2),
  (33, 'Redactar y argumentar las conclusiones', 'derecho', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (34, 'Una startup tecnológica', 'ingenieria', 3),
  (34, 'Un centro de bienestar o salud', 'salud', 2),
  (34, 'Una consultora o firma de negocios', 'administracion', 3),
  (34, 'Un estudio de diseño o arquitectura', 'diseño', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (35, 'La eficiencia del transporte o los servicios públicos', 'ingenieria', 2),
  (35, 'El acceso a la salud en zonas alejadas', 'salud', 3),
  (35, 'La desigualdad social en mi ciudad', 'ciencias_sociales', 3);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (36, 'Su nivel de innovación tecnológica', 'ingenieria', 2),
  (36, 'Su impacto en la salud de las personas', 'salud', 2),
  (36, 'Su rentabilidad y modelo de negocio', 'administracion', 3),
  (36, 'Su identidad visual y diseño de marca', 'diseño', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (37, 'Experimentando y probando cosas por mi cuenta', 'ingenieria', 2),
  (37, 'Leyendo casos reales y debatiendo sobre ellos', 'derecho', 2),
  (37, 'Viendo ejemplos visuales y practicando', 'diseño', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (38, 'Una feria de innovación o tecnología', 'ingenieria', 2),
  (38, 'Una exposición de arte o diseño', 'diseño', 3),
  (38, 'Un conversatorio sobre temas sociales', 'ciencias_sociales', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (39, 'Sobre cómo funciona una tecnología nueva', 'ingenieria', 2),
  (39, 'Sobre cómo cuidar la salud física o mental', 'salud', 2),
  (39, 'Sobre los derechos de las personas', 'derecho', 3);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (40, 'Crear soluciones tecnológicas útiles', 'ingenieria', 3),
  (40, 'Mejorar la salud de quienes me rodean', 'salud', 3),
  (40, 'Generar empleo con un negocio propio', 'administracion', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (41, 'Enseñar programación o tecnología a jóvenes', 'ingenieria', 2),
  (41, 'Apoyar jornadas de salud comunitarias', 'salud', 3),
  (41, 'Asesorar legalmente a personas de escasos recursos', 'derecho', 3);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (42, 'Una app que resuelva un problema técnico cotidiano', 'ingenieria', 3),
  (42, 'Una app para el cuidado de la salud', 'salud', 2),
  (42, 'Una app para gestionar finanzas personales', 'administracion', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (43, 'Que resolviera problemas de ingeniería complejos', 'ingenieria', 3),
  (43, 'Que ayudara a diagnosticar enfermedades más rápido', 'salud', 3),
  (43, 'Que hiciera más justos los procesos legales', 'derecho', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (44, 'Decisiones técnicas con datos claros', 'ingenieria', 2),
  (44, 'Decisiones que afectan el bienestar de otros', 'salud', 2),
  (44, 'Decisiones de negocio bajo presión', 'administracion', 3);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (45, '"Quiero construir tecnología que cambie vidas"', 'ingenieria', 3),
  (45, '"Quiero cuidar y sanar a las personas"', 'salud', 3),
  (45, '"Quiero liderar organizaciones exitosas"', 'administracion', 2),
  (45, '"Quiero defender lo que es justo"', 'derecho', 2);

-- ------------------------------------------------------------
-- OPCIONES NUEVAS en preguntas YA EXISTENTES, para dar cobertura
-- real a 'educacion' y 'humanidades' (antes no tenian ninguna)
-- ------------------------------------------------------------
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (1, 'Enseñarle a alguien algo hasta que lo entienda', 'educacion', 3),
  (4, 'El que explica y ayuda a los demás a entender la tarea', 'educacion', 2),
  (9, 'Lengua castellana o literatura', 'educacion', 2),
  (12, 'Cómo se enseña y se aprende mejor', 'educacion', 3),
  (20, 'Pedagogía o técnicas de enseñanza', 'educacion', 2),
  (26, 'Explicar temas complejos de forma simple', 'educacion', 2),
  (33, 'Explicarle a mis compañeros lo que no entendieron', 'educacion', 2),
  (39, 'Sobre cómo aprender mejor algo', 'educacion', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (3, 'Ensayos de filosofía o pensamiento crítico', 'humanidades', 2),
  (7, 'Me gusta reflexionar sobre el sentido de las cosas', 'humanidades', 2),
  (9, 'Filosofía', 'humanidades', 2),
  (16, 'Uno sobre filosofía o religión', 'humanidades', 2),
  (31, 'Contenido de reflexión o filosofía', 'humanidades', 2),
  (37, 'Reflexionando y debatiendo ideas abstractas', 'humanidades', 2),
  (38, 'Un conversatorio filosófico o literario', 'humanidades', 2),
  (45, '"Quiero dedicarme a pensar y enseñar"', 'humanidades', 2);

-- ------------------------------------------------------------
-- OPCIONES de las preguntas NUEVAS 46-53
-- ------------------------------------------------------------
insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (46, 'Preparar una clase o taller para otros', 'educacion', 3),
  (46, 'Leer sobre un tema filosófico o espiritual', 'humanidades', 2),
  (46, 'Resolver un problema técnico como hobby', 'ingenieria', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (47, 'Uno sobre cómo enseñar o formar a otros', 'educacion', 2),
  (47, 'Uno de filosofía, ética o teología', 'humanidades', 3),
  (47, 'Uno sobre salud y bienestar', 'salud', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (48, 'El que guía y forma a los más jóvenes', 'educacion', 3),
  (48, 'El que reflexiona sobre los valores del grupo', 'humanidades', 2),
  (48, 'El que organiza las actividades y recursos', 'administracion', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (49, '¿Cómo aprendemos mejor como seres humanos?', 'educacion', 3),
  (49, '¿Cuál es el sentido de la vida o la existencia?', 'humanidades', 3),
  (49, '¿Cómo se diseña algo realmente útil?', 'diseño', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (50, 'Dar clases de refuerzo a niños', 'educacion', 3),
  (50, 'Acompañar espiritualmente a personas en crisis', 'humanidades', 2),
  (50, 'Hacer brigadas de salud comunitaria', 'salud', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (51, 'Cómo mejorar la educación en mi país', 'educacion', 3),
  (51, 'Un dilema ético o filosófico actual', 'humanidades', 3),
  (51, 'Un caso legal controvertido', 'derecho', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (52, 'Formar a las próximas generaciones', 'educacion', 3),
  (52, 'Ayudar a las personas a encontrar sentido y propósito', 'humanidades', 2),
  (52, 'Construir soluciones tecnológicas', 'ingenieria', 2);

insert into opciones_respuesta (pregunta_id, texto, area, peso) values
  (53, 'Dar un taller o clase a estudiantes locales', 'educacion', 2),
  (53, 'Participar en debates filosóficos o religiosos', 'humanidades', 2),
  (53, 'Conocer cómo funciona su sistema de salud', 'salud', 2);