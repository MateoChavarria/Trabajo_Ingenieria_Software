-- ============================================================
-- TABLA NUEVA: carreras
-- 37 programas de pregrado UPB Medellin, en 8 areas
-- ============================================================

create table carreras (
  id serial primary key,
  nombre text not null,
  descripcion text not null,
  campo_laboral text not null,
  area_categoria text not null,
  duracion_semestres int,
  tarifa_semestre numeric
);

alter table carreras enable row level security;

-- ------------------------------------------------------------
-- 37 PROGRAMAS
-- ------------------------------------------------------------
insert into carreras (nombre, descripcion, campo_laboral, area_categoria, duracion_semestres, tarifa_semestre) values
  ('Administración de Empresas', 'Dirección estratégica corporativa, liderazgo, finanzas y emprendimiento (Acreditación EQUAA).', 'Formación en gestión estratégica, toma de decisiones financieras, optimización de recursos y dirección organizacional sostenible con acreditación internacional EQUAA.', 'administracion', 8, 13703400),
  ('Arquitectura', 'Proyección urbana, diseño de edificaciones, urbanismo sostenible y patrimonio cultural.', 'Diseño urbano, planificación espacial, construcción sostenible, hábitat y restauración del patrimonio arquitectónico y territorial.', 'diseño', 10, 14490800),
  ('Ciencias Políticas', 'Estudio del poder público, políticas sociales, geopolítica e instituciones estatales.', 'Análisis de sistemas políticos, relaciones internacionales, gestión de políticas públicas, resolución de conflictos y gobernanza.', 'derecho', 8, 13502400),
  ('Comunicación Social – Periodismo', 'Generación de contenidos para medios impresos, audiovisuales y digitales, e investigación social.', 'Investigación periodística, producción multimedial, comunicación organizacional, narrativa audiovisual y medios digitales.', 'ciencias_sociales', 9, 14531200),
  ('Derecho', 'Litigio, consultoría jurídica, legislación nacional e internacional y resolución pacífica de conflictos.', 'Formación jurídica integral en derecho constitucional, privado, público y penal, con énfasis en argumentación y conciliación.', 'derecho', 10, 13502400),
  ('Diseño de Vestuario', 'Diseño conceptual de moda, tecnologías textiles, patronaje e industria del vestuario.', 'Creación conceptual, investigación formal, tendencias de moda, gestión textil y producción estética de vestuario.', 'diseño', 8, 16113600),
  ('Diseño Gráfico', 'Branding, comunicación visual, medios digitales, diseño editorial e ilustración.', 'Comunicación visual, identidad de marca, ilustración, diseño editorial, medios interactivos y lenguajes gráficos.', 'diseño', 8, 16113600),
  ('Diseño Industrial', 'Concepción de productos de consumo, desarrollo de equipamiento, usabilidad y experiencia de usuario.', 'Desarrollo de productos, experiencia de usuario (UX), ergonomía, procesos de manufactura e innovación funcional.', 'diseño', 8, 14323200),
  ('Economía', 'Formación en política económica, econometría, análisis financiero y desarrollo sostenible.', 'Análisis de mercados, políticas públicas, evaluación de proyectos socioeconómicos y modelación econométrica.', 'administracion', 8, 12942100),
  ('Enfermería', 'Cuidado integral de la salud, administración de servicios hospitalarios y promoción comunitaria.', 'Cuidado holístico de la salud humana, gestión de servicios sanitarios, atención comunitaria y salud preventiva.', 'salud', 10, 9459800),
  ('Estudios Literarios', 'Investigación en teoría literaria, edición de publicaciones y crítica estética.', 'Análisis crítico e histórico de la literatura universal y nacional, crítica estética, edición de textos y creación literaria.', 'humanidades', 8, 4742400),
  ('Filosofía', 'Análisis riguroso del pensamiento occidental, lógica, ética y filosofía contemporánea.', 'Investigación de corrientes filosóficas clásicas y contemporáneas, ética, epistemología, lógica y filosofía política.', 'humanidades', 8, 4149600),
  ('Gestión del Emprendimiento y la Innovación', 'Modelos de negocio disruptivos, aceleración de startups e innovación empresarial.', 'Creación y escalabilidad de nuevos modelos de negocio, innovación corporativa, incubación y metodologías ágiles.', 'administracion', 8, 13703400),
  ('Historia', 'Análisis histórico, conservación patrimonial, crítica de fuentes y memoria histórica.', 'Investigación del pasado, análisis de procesos socio-históricos, conservación de memoria documental y divulgación cultural.', 'ciencias_sociales', 8, 6520800),
  ('Ingeniería Administrativa', 'Integra la gestión empresarial con la optimización de procesos, modelos financieros y dirección de operaciones.', 'Integración de ingeniería de operaciones con gestión gerencial, finanzas empresariales y cadenas de suministro.', 'ingenieria', 10, 13091200),
  ('Ingeniería Aeronáutica', 'Especializada en aerodinámica, estructuras de aeronaves, propulsión y mantenimiento aeronáutico.', 'Diseño, mantenimiento, aerodinámica, estructuras y sistemas de propulsión para la industria aeroespacial y aviación.', 'ingenieria', 10, 13091200),
  ('Ingeniería Agroindustrial', 'Enfocada en el procesamiento de recursos agropecuarios, biotecnología aplicada y calidad de alimentos.', 'Transformación industrial de recursos biológicos, bioeconomía, seguridad alimentaria y procesamiento agropecuario.', 'ingenieria', 10, 12273000),
  ('Ingeniería de Sistemas e Informática', 'Diseño de arquitectura de software, ciberseguridad, gestión de bases de datos y desarrollo de aplicaciones cloud.', 'Desarrollo de software, arquitectura de sistemas, ciberseguridad, ingeniería de datos y computación avanzada.', 'ingenieria', 9, 12273000),
  ('Ingeniería Eléctrica', 'Desarrollo de sistemas de potencia, energías limpias, redes de distribución y eficiencia energética.', 'Generación, transmisión y distribución de energía eléctrica, redes inteligentes (smart grids) y energías renovables.', 'ingenieria', 10, 13091200),
  ('Ingeniería Electrónica', 'Sistemas embebidos, automatización de procesos, robótica industrial y redes de telecomunicación.', 'Telecomunicaciones, automatización industrial, robótica, microelectrónica y procesamiento digital de señales.', 'ingenieria', 10, 13091200),
  ('Ingeniería en Diseño de Entretenimiento Digital', 'Desarrollo técnico e interactivo de videojuegos, efectos visuales (VFX) y realidad virtual.', 'Desarrollo de videojuegos, animación 3D, entornos virtuales inmersivos, efectos visuales y tecnologías interactivas.', 'ingenieria', 8, 13909400),
  ('Ingeniería en Inteligencia Artificial y Ciencia de Datos', 'Modelación matemática, aprendizaje automático (Machine Learning), Big Data y desarrollo de soluciones con IA.', 'Algoritmos de machine learning, deep learning, análisis masivo de datos (Big Data) y sistemas inteligentes.', 'ingenieria', 8, 12273000),
  ('Ingeniería en Nanotecnología', 'Investigación y desarrollo de nanoestructuras, biomateriales y dispositivos nanotecnológicos.', 'Síntesis y manipulación de materia a escala nanométrica para medicina, energía y materiales avanzados.', 'ingenieria', 10, 13091200),
  ('Ingeniería Industrial', 'Gestión de cadenas de suministro, logística, optimización de productividad y control de calidad.', 'Optimización de procesos productivos, gestión de calidad, logística global e investigación de operaciones.', 'ingenieria', 10, 13091200),
  ('Ingeniería Mecánica', 'Diseño de máquinas, sistemas térmicos, robótica y manufactura industrial (Acreditación ABET).', 'Diseño térmico y mecánico, conversión de energía, manufactura de maquinaria y automatización (Acreditado ABET).', 'ingenieria', 9, 13091200),
  ('Ingeniería Química', 'Transformación de materia prima, diseño de reactores, procesos sostenibles y polímeros (Acreditación ABET).', 'Diseño de procesos químicos y biotecnológicos, refinación de materiales y sostenibilidad (Acreditado ABET).', 'ingenieria', 10, 11454800),
  ('Licenciatura en Español e Inglés', 'Formación pedagógica en bilingüismo, didáctica de idiomas y lingüística aplicada.', 'Formación docente bilingüe, enseñanza de lenguas, lingüística aplicada y didáctica del español e inglés.', 'educacion', 9, 4742400),
  ('Licenciatura en Etnoeducación', 'Pedagogía orientada a comunidades étnicas, diversidad cultural e inclusión social.', 'Pedagogía comunitaria e intercultural, valoración de saberes ancestrales y atención a la diversidad étnica.', 'educacion', 8, 4371200),
  ('Licenciatura en Filosofía y Letras', 'Docencia del pensamiento filosófico, estudios literarios y teoría humanística.', 'Formación pedagógica en pensamiento filosófico, producción literaria y didáctica de las disciplinas del lenguaje.', 'educacion', 8, 5928000),
  ('Licenciatura en Lenguas Extranjeras (Inglés/Francés)', 'Metodologías de enseñanza de lenguas modernas y cultura internacional.', 'Enseñanza plurilingüe de inglés y francés, adquisición de segundas lenguas y diseño curricular educativo.', 'educacion', 8, 4475800),
  ('Licenciatura en Literatura y Lengua Castellana', 'Enseñanza de la competencia lectoescritora, crítica literaria y lenguaje.', 'Didáctica del lenguaje, desarrollo del pensamiento crítico lector, teoría literaria y gestión cultural.', 'educacion', 8, 4758400),
  ('Medicina', 'Formación médica cirujana integral con rotaciones clínicas en instituciones hospitalarias acreditadas.', 'Diagnóstico, tratamiento y prevención médica integral de enfermedades con formación clínica interdisciplinaria.', 'salud', 13, 24377000),
  ('Negocios Internacionales', 'Estrategia internacional de mercados, comercio global, logística y relaciones diplomático-comerciales.', 'Estrategias de comercio exterior, logística global, negociación intercultural y mercados internacionales.', 'administracion', 8, 12942100),
  ('Psicología', 'Evaluación psicológica, psicología clínica, organizacional, educativa e intervención comunitaria.', 'Estudio de la conducta humana, procesos mentales, intervención clínica, social, educativa y organizacional.', 'ciencias_sociales', 9, 12309700),
  ('Publicidad y Marketing Digital', 'Estrategia publicitaria, marketing de contenidos, campañas digitales y analítica de mercado.', 'Estrategia de marcas, analítica digital, campañas omnicanal y posicionamiento de mercado.', 'ciencias_sociales', 8, 14358200),
  ('Teología', 'Estudio de las fuentes bíblicas, historia eclesial, teología dogmática y pastoral.', 'Estudio sistemático de la fe, hermenéutica bíblica, teología pastoral y diálogo interreligioso.', 'humanidades', 8, 4742400),
  ('Trabajo Social', 'Planeación de proyectos sociales, intervención en vulnerabilidad y desarrollo comunitario.', 'Intervención en dinámicas comunitarias, desarrollo social, gestión de proyectos de impacto humano e inclusión.', 'ciencias_sociales', 9, 7251200);