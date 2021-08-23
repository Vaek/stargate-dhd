package cz.strnad.stargate_dhd

val EARTH = Planet("Země", "bZEjKc", "A")
val ABYDOS = Planet("Abydos", "aGOfLd", "")
val CIMMERIA = Planet("Cimmeria", "KiVQFZ", "")
val DAKARA = Planet("Dakara", "PbCHgD", "")
val CHULAK = Planet("Chulak", "IBWOkT", "")
val TOLLANA = Planet("Tollana", "DcHVRY", "")

val planets = listOf(EARTH, ABYDOS, CIMMERIA, DAKARA, CHULAK, TOLLANA)

data class Planet(
    val name: String,
    val address: String,
    val homeSymbol: String
) {
    override fun toString(): String = name
}
