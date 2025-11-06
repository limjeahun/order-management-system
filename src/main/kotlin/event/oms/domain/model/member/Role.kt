package event.oms.domain.model.member

enum class Role {
    ROLE_USER,
    ROLE_ADMIN;

    companion object {
        fun fromName(name: String): Role {
            return entries.find {
                it.name == name
            }?: ROLE_USER
        }
    }

}