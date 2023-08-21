import type { Action, BooleanInt } from '../../typings/common';
import type { DivBaseData } from './base';

export interface VideoSource {
    type: 'video_source';

    url: string;
    mime_type: string;
    /* resolution?: {
        type: 'resolution';
        width: number;
        height: number;
    }; */
    // bitrate?: number;
}

export interface DivVideoData extends DivBaseData {
    type: 'video';

    video_sources: VideoSource[];
    repeatable?: BooleanInt;
    autostart?: BooleanInt;
    muted?: BooleanInt;
    preview?: string;
    elapsed_time_variable?: string;
    resume_actions?: Action[];
    pause_actions?: Action[];
    end_actions?: Action[];
    buffering_actions?: Action[];
    fatal_actions?: Action[];
    // player_settings_payload?: Record<string, unknown>;
}

export interface VideoElements {
    start(): void;
    pause(): void;
}
