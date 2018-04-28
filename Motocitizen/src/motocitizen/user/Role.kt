package motocitizen.user

import motocitizen.dictionary.Dictionary

enum class Role constructor(override val code: Int, override val text: String) : Dictionary<Int> {
    RO(0, "только чтение"),
    STANDARD(1, "пользователь"),
    MODERATOR(2, "модератор"),
    DEVELOPER(3, "разработчик");
}
