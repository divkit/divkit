import lottieIcon from '../../assets/components/lottie.svg?url';
import buttonIcon from '../../assets/components/button.svg?url';
import closeIcon from '../../assets/components/close.svg?url';
import listItemIcon from '../../assets/components/list-item.svg?url';
import type { ComponentProperty } from './componentProps';

interface TemplateDescription {
    nameKey: string;
    description?: Record<string, string>;
    visible?: boolean;
    inShortList?: boolean;
    inlineTextEditorProp?: string;
    icon: string;
    props: ComponentProperty[];
    newNode: object;
    template: Record<string, unknown>;

    baseType?: string | undefined;
}

export const namedTemplates: Record<string, TemplateDescription> = {
    _template_lottie: {
        nameKey: 'templates.lottie',
        visible: true,
        inShortList: true,
        icon: lottieIcon,
        props: [{
            type: 'group',
            title: 'lottieProps.title',
            list: [{
                name: 'props.lottie_url',
                prop: 'lottie_params.lottie_url',
                type: 'file',
                subtype: 'lottie',
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
            }]
        }],
        newNode: {
            lottie_params: {
                lottie_url: '',
                repeat_count: -1,
                repeat_mode: 'restart'
            },
            width: {
                type: 'fixed',
                value: 100
            },
            height: {
                type: 'fixed',
                value: 100
            }
        },
        template: {
            type: 'gif',
            scale: 'fit',
            extensions: [
                {
                    id: 'lottie',
                    $params: 'lottie_params'
                }
            ],
            gif_url: 'https://yastatic.net/s3/home/divkit/empty2.png'
        }
    },
    _template_button: {
        nameKey: 'templates.button',
        visible: true,
        inShortList: true,
        inlineTextEditorProp: 'text',
        icon: buttonIcon,
        props: [{
            type: 'group',
            title: 'buttonProps.title',
            list: [{
                name: 'props.text',
                prop: 'text',
                type: 'string',
                enableTanker: true,
                enableSources: true,
                // zero-width space
                default: '​'
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
            }, {
                name: 'props.corners',
                prop: 'corners',
                type: 'integer',
                min: 0,
                max: 100,
                enableSources: true
            }, {
                name: 'props.actions',
                prop: 'actions',
                type: 'actions2'
            }]
        }],
        newNode: {
            text: 'Hello',
            background: [{
                type: 'solid',
                color: '#000'
            }],
            text_color: '#fff',
            corners: 4
        },
        template: {
            type: 'text',
            text_alignment_horizontal: 'center',
            text_alignment_vertical: 'center',
            border: { $corner_radius: 'corners' },
            paddings: {
                bottom: 24,
                left: 28,
                right: 28,
                top: 22
            },
            width: { type: 'wrap_content' }
        }
    },
    _template_close: {
        nameKey: 'templates.close',
        visible: true,
        inShortList: true,
        icon: closeIcon,
        props: [{
            type: 'group',
            title: 'closeProps.title',
            list: [{
                name: 'color',
                prop: 'tint_color',
                type: 'color',
                enableSources: true
            }, {
                name: 'props.actions',
                prop: 'actions',
                type: 'actions2'
            }]
        }],
        newNode: {
            alignment_horizontal: 'right',
            height: { type: 'fixed', value: 28 },
            margins: { top: 20, right: 24 },
            width: { type: 'fixed', value: 28 }
        },
        template: {
            accessibility: {
                description: 'Закрыть',
                mode: 'merge',
                type: 'button'
            },
            actions: [
                {
                    log_id: 'close_popup',
                    url: 'div-screen://close'
                }
            ],
            image_url: 'empty://',
            preview: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFQAAABUBAMAAADuRQ3yAAAACXBIWXMAACE4AAAhOAFFljFgAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAwUExURQAAAIeHh4qKiouLi4yMjIyMjIqKioyMjIuLi4uLi4yMjIyMjIyMjI2NjYyMjIyMjDn5Ri0AAAAPdFJOUwAgMEBQX2B/gKCvv8/f707huRkAAAFkSURBVEjH7dfNTcQwEAXgsEiOYIW2lS2BEugk6SB0QAMUQQXQBy1w4baRODwOm7XH43kzPu5hfYs9+pQfz5MzDLdxFeP+8++lc20BTqy0XtsBwGxXjgBQ2EeAsgsAfOTLJ1B2BAD8qtITRUXpAxh7RsUN7MDYMyoea5gIu6GrmEqE3dBXOWezBspYC7VZE7VZG7VYglosQ1uWoi3LUc06qGY9tGZdtGZ9VLIBKtkILWyIFjZGMxujme1AK3YNgi51o4Jdw/xM3Whm145YXgAnGI02pXnboh3sWF7W3IuG7Cg/7NyLBuxln04xe9mnKWTL5g/ZsvkjVnZUwMqO8tm6TV22blOP1b3vsLr3OdsGCmXbQGGslVKEtVLKZu3oM1k7+iyW5anBsjxt2UQDZWOf88SeplTSx5wDT6lJHZ4OPPqSKt070TfVN5CcPE3qnPXm5OmkzllfP0dWevf+fbz9DFzH+AfPICZho9A64QAAAABJRU5ErkJggg==',
            tint_color: '#73000000',
            type: 'image',
        }
    },
    _template_list_item: {
        nameKey: 'templates.list_item',
        visible: true,
        icon: listItemIcon,
        props: [{
            type: 'group',
            title: 'listItemProps.title',
            list: [{
                name: 'props.text',
                prop: 'list_text',
                type: 'string',
                enableTanker: true,
                default: '\0',
                enableSources: true
            }, {
                name: 'props.font_size',
                prop: 'list_text_size',
                type: 'integer',
                min: 1,
                max: 1000,
                enableSources: true
            }, {
                name: 'props.font_weight',
                prop: 'list_font_weight',
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
                prop: 'list_color',
                type: 'color',
                enableSources: true
            }, {
                name: 'props.actions',
                prop: 'actions',
                type: 'actions2'
            }]
        }],
        newNode: {
            list_text: 'Text',
            list_color: '#000',
            list_text_size: 24,
            list_font_weight: 'medium'
        },
        template: {
            type: 'container',
            orientation: 'horizontal',
            items: [{
                type: 'image',
                image_url: 'https://yastatic.net/s3/home/div/div_fullscreens/hyphen.4.png',
                $tint_color: 'list_color',
                width: {
                    type: 'fixed',
                    value: 28,
                    unit: 'sp'
                },
                height: {
                    type: 'fixed',
                    value: 28,
                    unit: 'sp'
                },
                margins: {
                    top: 2,
                    right: 12,
                    bottom: 2
                }
            }, {
                type: 'text',
                $text: 'list_text',
                $text_color: 'list_color',
                $font_size: 'list_text_size',
                line_height: 32,
                $font_weight: 'list_font_weight',
                width: {
                    type: 'wrap_content',
                    constrained: true
                }
            }]
        }
    }
};

export function isTemplate(name: string): boolean {
    return namedTemplates.hasOwnProperty(name);
}

export function getTemplateBaseType(name: string): string | undefined {
    return namedTemplates[name]?.baseType;
}
