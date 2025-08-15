import type { DivBaseData } from './base';
import type { DivActionableData } from './actionable';
import type { FontWeight } from './text';
import type { Action, BooleanInt } from '../../typings/common';
import type { EdgeInsets } from './edgeInserts';
import type { CornersRadius } from './border';
import type { FixedSize } from './sizes';

export type TabsTitleAnimationType = 'slide' | 'fade' | 'none';

export interface TabsTitleStyle {
    font_size?: number;
    // font_size_unit
    paddings?: EdgeInsets;
    item_spacing?: number;
    line_height?: number;
    letter_spacing?: number;
    font_weight?: FontWeight;
    active_font_weight?: FontWeight;
    inactive_font_weight?: FontWeight;
    font_family?: string;
    active_text_color?: string;
    inactive_text_color?: string;
    active_background_color?: string;
    inactive_background_color?: string;
    corner_radius?: number;
    corners_radius?: CornersRadius;
    animation_type?: TabsTitleAnimationType;
    animation_duration?: number;
}

export interface TabItem {
    title: string;
    title_click_action?: Action;
    div: DivBaseData;
}

export interface TabDelimiterStyle {
    image_url: string;
    width: FixedSize;
    height: FixedSize;
}

export interface DivTabsData extends DivBaseData, DivActionableData {
    type: 'tabs';
    title_paddings?: EdgeInsets;
    separator_paddings?: EdgeInsets;
    tab_title_style?: TabsTitleStyle;
    selected_tab?: number;
    has_separator?: BooleanInt;
    switch_tabs_by_content_swipe_enabled?: BooleanInt;
    separator_color?: string;
    items: TabItem[];
    // tabs_position
    // dynamic_height
    restrict_parent_scroll?: BooleanInt;
    tab_title_delimiter?: TabDelimiterStyle;
}
