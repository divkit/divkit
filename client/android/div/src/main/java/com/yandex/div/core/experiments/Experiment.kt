package com.yandex.div.core.experiments

enum class Experiment(val key: String, val defaultValue: Boolean = false) {
    TAP_BEACONS_ENABLED(
        "tap_beacons_enabled"
    ),
    VISIBILITY_BEACONS_ENABLED(
        "visibility_beacons_enabled"
    ),
    SWIPE_OUT_BEACONS_ENABLED(
        "swipe_out_beacons_enabled",
        defaultValue = true
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
        "accessibility_enabled",
        defaultValue = true
    ),
    VIEW_POOL_ENABLED(
        "view_pool_enabled",
        defaultValue = true
    ),
    VIEW_POOL_PROFILING_ENABLED(
        "view_pool_profiling_enabled"
    ),
    VIEW_POOL_OPTIMIZATION_DEBUG(
        "view_pool_optimization_debug"
    ),
    RESOURCE_CACHE_ENABLED(
        "resource_cache_enabled",
        defaultValue = true
    ),
    SHOW_RENDERING_TIME(
        "demo_activity_rendering_time_enabled",
        defaultValue = false
    ),
    MULTIPLE_STATE_CHANGE_ENABLED(
        "multiple_state_change_enabled"
    ),
    BIND_ON_ATTACH_ENABLED(
        "bind_on_attach_enabled"
    ),
    COMPLEX_REBIND_ENABLED(
        "complex_rebind_enabled"
    ),
    PAGER_PAGE_CLIP_ENABLED(
        "pager_page_children_enabled",
        defaultValue = true
    ),
    PERMANENT_DEBUG_PANEL_ENABLED(
        "permanent_debug_panel_enabled",
        defaultValue = false
    )
}
