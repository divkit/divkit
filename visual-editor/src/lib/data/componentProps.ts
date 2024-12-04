import type { ConditionObject } from './props';
import fixedHorizontal from '../../assets/fixedHorizontal.svg?url';
import matchParentHorizontal from '../../assets/matchParentHorizontal.svg?url';
import wrapContentHorizontal from '../../assets/wrapContentHorizontal.svg?url';
import fixedVertical from '../../assets/fixedVertical.svg?url';
import matchParentVertical from '../../assets/matchParentVertical.svg?url';
import wrapContentVertical from '../../assets/wrapContentVertical.svg?url';

export const supportedComponents = new Set([
    'image',
    'gif',
    'text',
    'separator',
    'container',
    'grid',
    'gallery',
    'pager',
    'tabs',
    'state',
    'custom',
    'indicator',
    'slider',
    'input',
    'select',
    'video'
]);

export const containerComponents = new Set([
    'container',
    'grid',
    'gallery',
    'pager',
    'tabs',
    'state',
    'custom'
]);

export interface BaseProperty {
    name?: string;
    rawName?: string;
    prop?: string;
    sizeValue?: 'width' | 'height';
    default?: unknown;
    type: string;
    siblings?: SiblingComponentProperty[];
    show?: ConditionObject;
    enabled?: ConditionObject | false;
    enableSources?: boolean;
}

export interface RadioProperty extends BaseProperty {
    type: 'radio';
    prop: string;
    options: {
        name: string;
        value: string | number;
        show?: ConditionObject;
    }[];
}

export interface IntegerProperty extends BaseProperty {
    type: 'integer';
    min: number;
    max: number;
    constraint?: string;
    inlineLabel?: string;
}

export interface NumberProperty extends BaseProperty {
    type: 'number';
    constraint?: string;
}

export interface BooleanProperty extends BaseProperty {
    type: 'boolean';
    label?: string;
    theme?: 'sibling';
}

export interface SelectProperty extends BaseProperty {
    type: 'select';
    options: {
        rawName?: string;
        name?: string;
        value: string;
        icon?: string;
        show?: ConditionObject;
    }[];
}

export interface PercentProperty extends BaseProperty {
    type: 'percent';
}

export interface RotationProperty extends BaseProperty {
    type: 'rotation';
}

export interface Background2Property extends BaseProperty {
    type: 'background2';
}

export interface StringProperty extends BaseProperty {
    type: 'string';
    enableTanker?: boolean;
}

export interface ColorProperty extends BaseProperty {
    type: 'color';
}

export interface Actions2Property extends BaseProperty {
    type: 'actions2';
    aloneProp?: string;
}

export interface VideoSourcesProperty extends BaseProperty {
    type: 'video_sources';
}

export interface FileProperty extends BaseProperty {
    type: 'file';
    subtype: 'image' | 'gif' | 'lottie' | 'image_preview';
}

export interface AlignmentProperty extends BaseProperty {
    type: 'alignment';
    horizontalProp: string;
    verticalProp: string;
    orientationProp?: string;
    isContent?: boolean;
    isSelfAlign?: boolean;
}

export interface MarginsPaddingsProperty extends BaseProperty {
    type: 'margins-paddings';
}

export interface TextAlignProperty extends BaseProperty {
    type: 'text-align';
    prop: string;
}

export interface TextStylesProperty extends BaseProperty {
    type: 'text-styles';
}

export interface GroupProperty {
    type: 'group';
    title?: string;
    rawTitle?: string;
    list: ComponentProperty[];
}

export interface SplitProperty {
    type: 'split';
    list: [ComponentProperty, ComponentProperty];
}

export interface VariableNameProperty extends BaseProperty {
    type: 'variable-name';
    required?: boolean;
}

export interface SelectOptionsProperty extends BaseProperty {
    type: 'select-options';
}

export interface SelectFontFamily extends BaseProperty {
    type: 'select-font-family';
}

export type ComponentProperty = RadioProperty | IntegerProperty | BooleanProperty | SelectProperty |
    PercentProperty | RotationProperty | StringProperty |
    ColorProperty | FileProperty | GroupProperty | SplitProperty | AlignmentProperty |
    MarginsPaddingsProperty | Background2Property | TextAlignProperty | TextStylesProperty |
    Actions2Property | VideoSourcesProperty | NumberProperty | VariableNameProperty |
    SelectOptionsProperty | SelectFontFamily;

export type SiblingComponentProperty = ComponentProperty & {
    related?: {
        prop: string;
        value: string;
    }[];
}

export const BASE_COMPONENT_PROPS: ComponentProperty[] = [{
    type: 'group',
    title: 'baseProps.title',
    list: [{
        type: 'split',
        list: [{
            name: 'props.width',
            prop: 'width.type',
            default: 'match_parent',
            type: 'select',
            options: [{
                name: 'props.size_fixed',
                value: 'fixed',
                icon: fixedHorizontal
            }, {
                name: 'props.size_match_parent',
                value: 'match_parent',
                icon: matchParentHorizontal
            }, {
                name: 'props.size_wrap_content',
                value: 'wrap_content',
                icon: wrapContentHorizontal,
                show: {
                    not: {
                        or: [{
                            prop: 'type',
                            equal: 'image'
                        }, {
                            prop: 'type',
                            equal: 'gif'
                        }]
                    }
                }
            }],
            siblings: [{
                prop: 'width.value',
                type: 'integer',
                min: 0,
                max: 9999,
                inlineLabel: 'W',
                show: {
                    prop: 'width.type',
                    equal: 'fixed'
                }
            }, {
                label: 'props.size_constrained',
                prop: 'width.constrained',
                type: 'boolean',
                theme: 'sibling',
                related: [{
                    prop: 'width.type',
                    value: 'wrap_content'
                }],
                show: {
                    prop: 'width.type',
                    equal: 'wrap_content'
                }
            }, {
                type: 'integer',
                min: 0,
                max: 9999,
                sizeValue: 'width',
                enabled: false,
                inlineLabel: 'W',
                show: {
                    or: [{
                        prop: 'width.type',
                        equal: 'match_parent'
                    }, {
                        prop: 'width',
                        isEmpty: true
                    }]
                }
            }, {
                name: 'props.column_span',
                prop: 'column_span',
                type: 'integer',
                min: 1,
                max: 1000,
                show: {
                    prop: '$parent.type',
                    equal: 'grid'
                },
                enableSources: true
            }]
        }, {
            name: 'props.height',
            prop: 'height.type',
            default: 'wrap_content',
            type: 'select',
            options: [{
                name: 'props.size_fixed',
                value: 'fixed',
                icon: fixedVertical
            }, {
                name: 'props.size_match_parent',
                value: 'match_parent',
                icon: matchParentVertical
            }, {
                name: 'props.size_wrap_content',
                value: 'wrap_content',
                icon: wrapContentVertical,
                show: {
                    not: {
                        or: [{
                            prop: 'type',
                            equal: 'image'
                        }, {
                            prop: 'type',
                            equal: 'gif'
                        }]
                    }
                }
            }],
            siblings: [{
                prop: 'height.value',
                type: 'integer',
                min: 0,
                max: 9999,
                inlineLabel: 'H',
                show: {
                    prop: 'height.type',
                    equal: 'fixed'
                }
            }, {
                label: 'props.size_constrained',
                prop: 'height.constrained',
                type: 'boolean',
                theme: 'sibling',
                related: [{
                    prop: 'height.type',
                    value: 'wrap_content'
                }],
                show: {
                    or: [{
                        prop: 'height.type',
                        equal: 'wrap_content'
                    }, {
                        prop: 'height.type',
                        isEmpty: true
                    }]
                }
            }, {
                type: 'integer',
                min: 0,
                max: 9999,
                sizeValue: 'height',
                enabled: false,
                inlineLabel: 'H',
                show: {
                    prop: 'height.type',
                    equal: 'match_parent'
                }
            }, {
                name: 'props.row_span',
                prop: 'row_span',
                type: 'integer',
                min: 1,
                max: 1000,
                show: {
                    prop: '$parent.type',
                    equal: 'grid'
                },
                enableSources: true
            }]
        }]
    }, {
        type: 'alignment',
        name: 'props.alignment',
        horizontalProp: 'alignment_horizontal',
        verticalProp: 'alignment_vertical',
        isSelfAlign: true
    }, {
        type: 'split',
        list: [{
            name: 'props.alpha',
            prop: 'alpha',
            type: 'percent',
            default: 1,
            enableSources: true
        }, {
            name: 'props.rotation',
            prop: 'transform.rotation',
            type: 'rotation',
            default: 0,
            enableSources: true
        }]
    }, {
        name: 'props.insets',
        type: 'margins-paddings'
    }, {
        name: 'props.background',
        prop: 'background',
        type: 'background2'
    }]
}];

export const COMPONENT_PROPS: Record<string, ComponentProperty[]> = {
    text: [...BASE_COMPONENT_PROPS, {
        type: 'group',
        title: 'textProps.title',
        list: [{
            name: 'props.text',
            prop: 'text',
            type: 'string',
            enableTanker: true,
            enableSources: true,
            default: '\0'
        }, {
            type: 'split',
            list: [{
                name: 'props.font_size',
                prop: 'font_size',
                type: 'integer',
                min: 1,
                max: 1000,
                enableSources: true
            }, {
                name: 'props.line_height',
                prop: 'line_height',
                type: 'integer',
                min: 0,
                max: 1000,
                enableSources: true
            }]
        }, {
            type: 'split',
            list: [{
                name: 'props.text_alignment_horizontal',
                prop: 'text_alignment_horizontal',
                type: 'text-align',
                enableSources: true
            }, {
                name: 'textStyle',
                type: 'text-styles'
            }]
        }, {
            name: 'props.font_weight',
            prop: 'font_weight',
            type: 'select',
            options: [{
                name: 'props.font_weight_light',
                value: 'light'
            }, {
                name: 'props.font_weight_normal',
                value: 'regular'
            }, {
                name: 'props.font_weight_medium',
                value: 'medium'
            }, {
                name: 'props.font_weight_bold',
                value: 'bold'
            }],
            enableSources: true
        }, {
            name: 'props.font_family',
            prop: 'font_family',
            type: 'select-font-family',
            show: {
                prop: '$fontFamily',
                equal: 'yes'
            },
            enableSources: true
        }, {
            name: 'props.text_color',
            prop: 'text_color',
            type: 'color',
            enableSources: true
        }],
    }, {
        type: 'group',
        title: 'props.actions',
        list: [{
            name: 'props.actions',
            prop: 'actions',
            type: 'actions2',
            aloneProp: 'action'
        }]
    }],
    image: [...BASE_COMPONENT_PROPS, {
        type: 'group',
        title: 'imageProps.title',
        list: [{
            name: 'props.image_url',
            prop: 'image_url',
            type: 'file',
            subtype: 'image',
            enableSources: true
        }, {
            name: 'props.preview',
            prop: 'preview',
            type: 'file',
            subtype: 'image_preview',
            enableSources: true
        }, {
            name: 'props.image_scale',
            prop: 'scale',
            type: 'select',
            default: 'fill',
            options: [{
                name: 'props.scale_fit',
                value: 'fit'
            }, {
                name: 'props.scale_fill',
                value: 'fill'
            }, {
                name: 'props.scale_no_scale',
                value: 'no_scale'
            }, {
                name: 'props.scale_stretch',
                value: 'stretch'
            }],
            enableSources: true
        }, {
            name: 'props.image_alignment',
            type: 'alignment',
            horizontalProp: 'content_alignment_horizontal',
            verticalProp: 'content_alignment_vertical',
            enableSources: true
        }]
    }],
    gif: [...BASE_COMPONENT_PROPS, {
        type: 'group',
        title: 'imageProps.title',
        list: [{
            name: 'props.image_url',
            prop: 'gif_url',
            type: 'file',
            subtype: 'image',
            enableSources: true
        }, {
            name: 'props.preview',
            prop: 'preview',
            type: 'file',
            subtype: 'image_preview',
            enableSources: true
        }, {
            name: 'props.image_scale',
            prop: 'scale',
            type: 'select',
            default: 'fill',
            options: [{
                name: 'props.scale_fit',
                value: 'fit'
            }, {
                name: 'props.scale_fill',
                value: 'fill'
            }, {
                name: 'props.scale_no_scale',
                value: 'no_scale'
            }, {
                name: 'props.scale_stretch',
                value: 'stretch'
            }],
            enableSources: true
        }, {
            name: 'props.image_alignment',
            type: 'alignment',
            horizontalProp: 'content_alignment_horizontal',
            verticalProp: 'content_alignment_vertical',
            enableSources: true
        }]
    }],
    video: [...BASE_COMPONENT_PROPS, {
        type: 'group',
        title: 'videoProps.title',
        list: [{
            name: 'props.video_sources',
            prop: 'video_sources',
            type: 'video_sources'
        }, {
            name: 'props.preview',
            prop: 'preview',
            type: 'file',
            subtype: 'image_preview',
            enableSources: true
        }, {
            name: 'props.image_scale',
            prop: 'scale',
            type: 'select',
            default: 'fit',
            options: [{
                name: 'props.scale_fit',
                value: 'fit'
            }, {
                name: 'props.scale_fill',
                value: 'fill'
            }, {
                name: 'props.scale_no_scale',
                value: 'no_scale'
            }],
            enableSources: true
        }, {
            name: 'props.video_repeatable',
            prop: 'repeatable',
            type: 'boolean',
            enableSources: true
        }, {
            name: 'props.video_autostart',
            prop: 'autostart',
            type: 'boolean',
            enableSources: true
        }, {
            name: 'props.video_muted',
            prop: 'muted',
            type: 'boolean',
            enableSources: true
        }]
    }],
    container: [...BASE_COMPONENT_PROPS, {
        type: 'group',
        title: 'containerProps.title',
        list: [{
            name: 'props.orientation',
            prop: 'orientation',
            type: 'select',
            default: 'vertical',
            options: [{
                name: 'props.orientation_horizontal',
                value: 'horizontal'
            }, {
                name: 'props.orientation_vertical',
                value: 'vertical'
            }, {
                name: 'props.orientation_overlap',
                value: 'overlap'
            }],
            enableSources: true
        }, {
            name: 'props.content_alignment',
            type: 'alignment',
            horizontalProp: 'content_alignment_horizontal',
            verticalProp: 'content_alignment_vertical',
            orientationProp: 'orientation',
            isContent: true
        }]
    }],
    grid: [...BASE_COMPONENT_PROPS, {
        type: 'group',
        title: 'gridProps.title',
        list: [{
            name: 'props.column_count',
            prop: 'column_count',
            type: 'integer',
            min: 1,
            max: 1000,
            enableSources: true
        }, {
            name: 'props.content_alignment',
            type: 'alignment',
            horizontalProp: 'content_alignment_horizontal',
            verticalProp: 'content_alignment_vertical',
            orientationProp: 'orientation',
            isContent: true
        }]
    }],
    gallery: [...BASE_COMPONENT_PROPS, {
        type: 'group',
        title: 'galleryProps.title',
        list: [{
            name: 'props.orientation',
            prop: 'orientation',
            type: 'select',
            default: 'horizontal',
            options: [{
                name: 'props.orientation_horizontal',
                value: 'horizontal'
            }, {
                name: 'props.orientation_vertical',
                value: 'vertical'
            }],
            enableSources: true
        }, {
            name: 'props.cross_content_alignment',
            prop: 'cross_content_alignment',
            type: 'select',
            options: [{
                name: 'props.orientation_start',
                value: 'start'
            }, {
                name: 'props.orientation_center',
                value: 'center'
            }, {
                name: 'props.orientation_end',
                value: 'end'
            }],
            enableSources: true
        }, {
            type: 'integer',
            prop: 'item_spacing',
            min: 0,
            max: 9999,
            name: 'props.spacing',
            enableSources: true
        }]
    }],
    separator: [...BASE_COMPONENT_PROPS, {
        type: 'group',
        title: 'separatorProps.title',
        list: [{
            name: 'props.orientation',
            prop: 'delimiter_style.orientation',
            type: 'select',
            default: 'horizontal',
            options: [{
                name: 'props.orientation_horizontal',
                value: 'horizontal'
            }, {
                name: 'props.orientation_vertical',
                value: 'vertical'
            }],
            enableSources: true
        }, {
            name: 'props.separator_color',
            prop: 'delimiter_style.color',
            type: 'color',
            default: '#000',
            enableSources: true
        }]
    }],
    input: [...BASE_COMPONENT_PROPS, {
        type: 'group',
        title: 'a11yProps.title',
        list: [{
            name: 'props.a11y_description',
            prop: 'accessibility.description',
            type: 'string',
            enableSources: true
        }]
    }, {
        type: 'group',
        title: 'inputProps.title',
        list: [{
            name: 'props.text_variable',
            prop: 'text_variable',
            type: 'variable-name',
            required: true
        }, {
            name: 'props.keyboard_type',
            prop: 'keyboard_type',
            type: 'select',
            default: 'multi_line_text',
            options: [{
                name: 'props.keyboard_single_line_text',
                value: 'single_line_text'
            }, {
                name: 'props.keyboard_multi_line_text',
                value: 'multi_line_text'
            }, {
                name: 'props.keyboard_phone',
                value: 'phone'
            }, {
                name: 'props.keyboard_number',
                value: 'number'
            }, {
                name: 'props.keyboard_email',
                value: 'email'
            }, {
                name: 'props.keyboard_uri',
                value: 'uri'
            }, {
                name: 'props.keyboard_password',
                value: 'password'
            }],
            enableSources: true
        }, {
            name: 'props.max_length',
            type: 'integer',
            min: 0,
            max: 99999,
            enableSources: true
        }, {
            type: 'split',
            list: [{
                name: 'props.font_size',
                prop: 'font_size',
                type: 'integer',
                min: 1,
                max: 1000,
                enableSources: true
            }, {
                name: 'props.line_height',
                prop: 'line_height',
                type: 'integer',
                min: 0,
                max: 1000,
                enableSources: true
            }]
        }, {
            name: 'props.text_color',
            prop: 'text_color',
            type: 'color',
            enableSources: true
        }, {
            name: 'props.hint_text',
            prop: 'hint_text',
            type: 'string',
            enableSources: true
        }]
    }],
    select: [...BASE_COMPONENT_PROPS, {
        type: 'group',
        title: 'a11yProps.title',
        list: [{
            name: 'props.a11y_description',
            prop: 'accessibility.description',
            type: 'string',
            enableSources: true
        }]
    }, {
        type: 'group',
        title: 'selectProps.title',
        list: [{
            name: 'props.select_options',
            prop: 'options',
            type: 'select-options'
        }, {
            name: 'props.text_variable',
            prop: 'value_variable',
            type: 'variable-name',
            required: true
        }, {
            type: 'split',
            list: [{
                name: 'props.font_size',
                prop: 'font_size',
                type: 'integer',
                min: 1,
                max: 1000,
                enableSources: true
            }, {
                name: 'props.line_height',
                prop: 'line_height',
                type: 'integer',
                min: 0,
                max: 1000,
                enableSources: true
            }]
        }, {
            name: 'props.text_color',
            prop: 'text_color',
            type: 'color',
            enableSources: true
        }, {
            name: 'props.hint_text',
            prop: 'hint_text',
            type: 'string',
            enableSources: true
        }]
    }]
};

export const ROOT_PROPS: ComponentProperty[] = [{
    name: 'props.background',
    prop: 'background',
    type: 'background2'
}];
