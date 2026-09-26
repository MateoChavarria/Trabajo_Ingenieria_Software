/**
 * Componente reutilizable de barra de progreso: no depende de ningun
 * id fijo, recibe los elementos del DOM que debe controlar. Sirve
 * tanto para el test vocacional (X de Y preguntas) como, mas
 * adelante, para la simulacion de semestres (Historia 11: "Semestre
 * X de Y").
 */
class BarraProgreso {
    constructor(elementoBarra, elementoTexto, plantillaTexto = "{actual} de {total} completado") {
        this.elementoBarra = elementoBarra;
        this.elementoTexto = elementoTexto;
        this.plantillaTexto = plantillaTexto;
    }

    actualizar(actual, total) {
        const porcentaje = total === 0 ? 0 : Math.round((actual / total) * 100);
        this.elementoBarra.style.width = porcentaje + "%";
        this.elementoTexto.textContent = this.plantillaTexto
            .replace("{actual}", actual)
            .replace("{total}", total);
    }
}