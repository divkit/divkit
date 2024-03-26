import type { Action, BooleanInt } from '../../typings/common';
import type { DivBaseData } from './base';
import type { DivAspect } from './image';

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

export type VideoScale = 'fill' | 'no_scale' | 'fit';

export interface DivVideoData extends DivBaseData {
    type: 'video';

    video_sources: VideoSource[];
    repeatable?: BooleanInt;
    autostart?: BooleanInt;
    preload_required?: BooleanInt;
    muted?: BooleanInt;
    preview?: string;
    elapsed_time_variable?: string;
    resume_actions?: Action[];
    pause_actions?: Action[];
    end_actions?: Action[];
    buffering_actions?: Action[];
    fatal_actions?: Action[];
    // player_settings_payload?: Record<string, unknown>;
    aspect?: DivAspect;
    scale?: VideoScale;
}

export interface VideoElements {
    start(): void;
    pause(): void;
}
