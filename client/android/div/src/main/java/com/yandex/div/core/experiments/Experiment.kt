package com.yandex.div.core.experiments

enum class Experiment(val key: String, val defaultValue: Boolean = false) {
    TAP_BEACONS_ENABLED(
        "tap_beacons_enabled"
    ),
    VISIBILITY_BEACONS_ENABLED(
        "visibility_beacons_enabled"
    ),
    LONGTAP_ACTIONS_PASS_TO_CHILD_ENABLED(
        "longtap_actions_pass_to_child"
    ),
    IGNORE_ACTION_MENU_ITEMS_ENABLED(
        "override_context_menu_handler"
    ),
    HYPHENATION_SUPPORT_ENABLED(
        "support_hyphenation"
    ),
    VISUAL_ERRORS_ENABLED(
        "visual_errors"
    ),
    ACCESSIBILITY_ENABLED(
        "accessibility_enabled"
    ),
    VIEW_POOL_ENABLED(
        "view_pool_enabled",
        defaultValue = true
    ),
    VIEW_POOL_PROFILING_ENABLED(
        "view_pool_profiling_enabled"
    ),
    RESOURCE_CACHE_ENABLED(
        "resource_cache_enabled",
        defaultValue = true
    ),
    MULTIPLE_STATE_CHANGE_ENABLED(
        "multiple_state_change_enabled"
    )
}
