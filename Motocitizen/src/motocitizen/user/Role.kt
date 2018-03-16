package motocitizen.user

enum class Role constructor(val code: Int, val text: String) {
    RO(0, "только чтение"),
    //    BANNED("banned", "забанен"),
    STANDARD(1, "пользователь"),
    MODERATOR(2, "модератор"),
    //    ADMINISTRATOR("admin", "администратор"),
    DEVELOPER(3, "разработчик");

    val isModerator: Boolean
        inline get() = this in arrayOf(MODERATOR, DEVELOPER)

    companion object {
        fun parse(role: Int): Role = values().firstOrNull { it.code == role } ?: RO
    }
}
