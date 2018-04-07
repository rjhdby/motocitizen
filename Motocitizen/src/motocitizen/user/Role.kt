package motocitizen.user

import motocitizen.dictionary.Dictionary

enum class Role constructor(override val code: Int, override val text: String) : Dictionary<Int> {
    RO(0, "только чтение"),
    //    BANNED("banned", "забанен"),
    STANDARD(1, "пользователь"),
    MODERATOR(2, "модератор"),
    //    ADMINISTRATOR("admin", "администратор"),
    DEVELOPER(3, "разработчик");

    fun isModerator(): Boolean = this in arrayOf(MODERATOR, DEVELOPER)
}
