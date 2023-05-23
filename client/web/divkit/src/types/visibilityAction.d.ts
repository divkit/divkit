export interface VisibilityAction {
    log_id: string;
    url?: string;
    // referer
    payload?: Record<string, string>;
    visibility_percentage?: number;
    visibility_duration?: number;
    // download_callbacks
    log_limit?: number;
}

export interface DisappearAction {
    log_id: string;
    url?: string;
    // referer
    payload?: Record<string, string>;
    visibility_percentage?: number;
    disappear_duration?: number;
    // download_callbacks
    log_limit?: number;
}
