package info.mx.tracks.navigation

enum class MXMenuItem constructor(private val menuCode: String, val title: String) {

    MENU_IMPORT_ADMIN("MENU_IMPORT_ADMIN", "Import/Admin"),
    MENU_LOCATION_MONITOR("MENU_LOCATION_MONITOR", "Location Monitor"),
    MENU_INVITE("MENU_INVITE", "Invite"),
    MENU_CRASHLYTIC("MENU_CRASHLYTIC", "Crashlytic"),
    MENU_NETWORK_PROBLEMS("MENU_NETWORK_PROBLEMS", "Network problems");

    companion object {
        /**
         * find an instance of ErrorCode that matches the parameter from method signature
         *
         * @param errorCode String representation of the error code
         * @return an instance of ErrorCode that is either the matching ErrorCode or UNKNOWN
         */
        fun fromValue(errorCode: String): MXMenuItem? {
            for (code in values()) {
                if (code.menuCode.equals(errorCode, ignoreCase = true)) {
                    return code
                }
            }
            return null
        }
    }
}
