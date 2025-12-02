<script lang="ts">
    import { createEventDispatcher, getContext, onMount, tick } from 'svelte';
    import { fade, fly } from 'svelte/transition';
    import type { Action } from '@divkitframework/divkit/typings/common';
    import { makeStyle } from '../utils/makeStyle';
    import { multilineSubmit, redo, undo } from '../utils/keybinder/shortcuts';
    import { htmlFilter } from '../utils/htmlFilter';
    import { calcSelectionOffset, getInnerText, setSelectionOffset } from '../utils/contenteditable';
    import { isEqual } from '../utils/isEqual';
    import { rangeStyle, type FontWeight, type TextImage, type TextRange, type TextStyles } from '../utils/range';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import { APP_CTX, type AppContext } from '../ctx/appContext';
    import { copyValue } from '../utils/copyValue';
    import { divkitColorToCss } from '../utils/colors';
    import type { TreeLeaf } from '../ctx/tree';
    import { isPaletteColor, paletteIdToValue, valueToPaletteId } from '../data/palette';
    import ColorPreview from './ColorPreview.svelte';
    import alertTailImage from '../../assets/tail.svg?raw';

    // todo subscribe to previewThemeStore
    // todo support newer text properties

    export let text: string;
    export let json;
    export let leaf: TreeLeaf;
    export let disabled = false;
    export let textDisabled = false;
    export let rotation = 0;
    export let scale: number;

    export function getValue() {
        return {
            leaf,
            text,
            ranges,
            images,
            textAlign
        };
    }

    const { l10nString } = getContext<LanguageContext>(LANGUAGE_CTX);
    const {
        actionLogUrlVariable,
        ownerDocument,
        getSelection,
        state,
        color2Dialog,
        file2Dialog,
        link2Dialog,
        textAlign2Dialog
    } = getContext<AppContext>(APP_CTX);
    const { palette, selectedLeaf, previewThemeStore, direction } = state;

    const dispatch = createEventDispatcher();

    interface State {
        text: string;
        ranges: TextRange[];
        images: TextImage[];
    }

    type Style = Record<string, string | number | undefined>;

    interface TextRenderItem {
        index?: number;
        text: string;
        textStyles: TextStyles;
        actions?: Action[];
    }

    interface ImageRenderItem {
        index: number;
        image: {
            url: string;
            width: string;
            height: string;
            wrapperStyle: Style;
            svgFilterId?: string;
        };
    }

    type RenderItem = TextRenderItem | ImageRenderItem;

    function pxToEm(value: number): string {
        if (typeof value !== 'number' && typeof value !== 'string' || !value) {
            return '0';
        }

        const casted = Number(value);

        if (isNaN(casted)) {
            return '0';
        }

        return (Math.ceil(casted * 1000) / 10000) + 'em';
    }

    function fontWeightToCss(fontWeight?: FontWeight | undefined): number | undefined {
        if (
            fontWeight === 'light' ||
            fontWeight === 'medium' ||
            fontWeight === 'bold' ||
            fontWeight === 'regular'
        ) {
            if (fontWeight === 'medium') {
                return 500;
            } else if (fontWeight === 'bold') {
                return 700;
            } else if (fontWeight === 'light') {
                return 300;
            }

            return 400;
        }
    }

    function calcTextStyle(textStyles: TextStyles): string | undefined {
        const childFontSize = textStyles.font_size || 12;
        const lineHeight = (textStyles.line_height || 1.25) / childFontSize;
        const letterSpacing = pxToEm(textStyles.letter_spacing || 0);
        const fontWeight = fontWeightToCss(textStyles.font_weight || 'regular');
        let colorValue = textStyles.text_color || '#000';
        let color = '';

        if (isPaletteColor(colorValue)) {
            const paletteId = valueToPaletteId(colorValue);
            const paletteItem = $palette.find(it => it.id === paletteId);
            if (paletteItem) {
                color = divkitColorToCss(paletteItem[$previewThemeStore]);
            }
        } else {
            color = divkitColorToCss(colorValue);
        }

        let decoration = 'none';
        if (textStyles.underline || textStyles.strike) {
            if (textStyles.underline === 'single' && textStyles.strike === 'single') {
                decoration = 'underline line-through';
            } else if (textStyles.underline === 'single') {
                decoration = 'underline';
            } else if (textStyles.strike === 'single') {
                decoration = 'line-through';
            }
        }

        return makeStyle({
            'font-size': pxToEm((childFontSize * 10) / fontSize),
            'line-height': lineHeight,
            'letter-spacing': letterSpacing,
            'font-weight': fontWeight,
            // 'font-family': fontFamily,
            color,
            'text-decoration': decoration,
            // background: bg?.color || undefined,
            /**
             * box-shadow instead of border because:
             * 1) Doesn't take space as border does
             * 2) There should not be a border-radius on line breaks, but there should be a border
             */
            // 'box-shadow': border ? `inset 0 0 0 ${pxToEm(border.width)} ${border.color}` : undefined,
            // 'border-radius': borderRadius ? pxToEm(borderRadius) : undefined
        });
    }

    function getState(target: HTMLElement): State {
        let offset = 0;
        const res: State = {
            text: '',
            images: [],
            ranges: []
        };
        const walkChild = (child: Node): void => {
            const content = getInnerText(child, false);
            if (child instanceof HTMLElement && child.dataset.range) {
                res.ranges.push({
                    ...JSON.parse(child.dataset.range),
                    start: offset,
                    end: offset + content.length
                });
                offset += content.length;
                res.text += content;
            } else if (child instanceof HTMLElement && child.dataset.image) {
                if (child.tagName === 'SPAN') {
                    Array.from(child.childNodes).forEach(subchild => {
                        if (subchild instanceof HTMLElement && subchild.tagName === 'IMG') {
                            res.images.push({
                                ...JSON.parse(child.dataset.image as string),
                                start: offset
                            });
                        } else {
                            const content = getInnerText(subchild, false);
                            offset += content.length;
                            res.text += content;
                        }
                    });
                } else {
                    res.images.push({
                        ...JSON.parse(child.dataset.image),
                        start: offset
                    });
                }
            } else if (child instanceof HTMLElement && child.childNodes.length) {
                if (child.tagName === 'DIV') {
                    ++offset;
                    res.text += '\n';
                }
                Array.from(child.childNodes).forEach(walkChild);
            } else {
                offset += content.length;
                res.text += content;
            }
        };
        const childs = Array.from(target.childNodes);
        childs.forEach(walkChild);

        res.ranges = simplifyRanges(res.text, res.ranges);

        return res;
    }

    let ranges: TextRange[] = json.ranges || [];
    let images: TextImage[] = json.images || [];
    let selectionStartTextOnly = 0;
    let selectionEndTextOnly = 0;
    let size = '';
    let color = '';
    let weight = '';
    let underline = false;
    let strike = false;
    let historyIndex = 0;
    let historyStack: State[] = [{
        text,
        ranges,
        images
    }];
    let additionalHeight = 0;
    let textAlign = leaf.props.processedJson?.text_alignment_horizontal || 'left';
    let showTextDisabledAlert = textDisabled && !disabled;

    const rootTextStyles = {
        font_size: json.font_size,
        letter_spacing: json.letter_spacing,
        font_weight: json.font_weight,
        font_family: json.font_family,
        text_color: json.text_color,
        underline: json.underline,
        strike: json.strike,
        line_height: json.line_height
    };

    const fontSize = json.font_size || 12;
    const lineHeight = json.line_height !== undefined ? (json.line_height / fontSize) : 1.25;
    const textColor = json.text_color || '#000';
    let customLineHeight = json.line_height !== undefined;

    let root: HTMLElement;
    let elem: HTMLElement;
    let toolbarElem: HTMLElement;
    let textAlignButton: HTMLElement;
    let actionsButton: HTMLElement;
    let renderList: RenderItem[] = [];

    updateRenderList(text, ranges, images);

    function updateRenderList(
        text: string,
        textRanges: TextRange[] | undefined,
        textImages: TextImage[] | undefined
    ) {
        let newRenderList: typeof renderList = [];

        if (
            !(
                Array.isArray(textRanges) && textRanges.length ||
                Array.isArray(textImages) && textImages.length && text
            )
        ) {
            renderList = [{
                index: 0,
                text,
                textStyles: rootTextStyles
            }];
            return;
        }

        const content = text;
        let ranges = textRanges || [
            {
                start: 0,
                end: content.length
            }
        ];
        let images = textImages || [];
        let prevIndex = 0;
        let activeRanges: TextStyles[] = [];
        let list: ({
            index: number;
            elementIndex: number;
            range: TextRange & {
                start: number;
                end: number;
            };
            type: 'rangeStart' | 'rangeEnd';
            isStart?: boolean;
        } | {
            index: number;
            elementIndex: number;
            type: 'image';
            arrayIndex: number;
            image: TextImage & {
                start: number;
                url: string;
            };
        })[] = [];

        ranges.forEach((range, index) => {
            if (range.start !== undefined && range.end !== undefined) {
                list.push({
                    index: range.start,
                    elementIndex: index,
                    range: range as typeof range & {
                        start: number;
                        end: number;
                    },
                    type: 'rangeStart',
                    isStart: true
                });
                list.push({
                    index: range.end,
                    elementIndex: index,
                    range: range as typeof range & {
                        start: number;
                        end: number;
                    },
                    type: 'rangeEnd'
                });
            }
        });
        images.forEach((image, index) => {
            if (image.start !== undefined && image.url && image.start <= content.length) {
                list.push({
                    index: image.start,
                    elementIndex: index,
                    image: image as typeof image & {
                        start: number;
                        url: string;
                    },
                    type: 'image',
                    arrayIndex: index
                });
            }
        });
        list.sort((a, b) => {
            if (a.index === b.index) {
                if (a.type !== b.type) {
                    if (a.type === 'image') {
                        return -1;
                    } else if (b.type === 'image') {
                        return 1;
                    }

                    return a.type < b.type ? -1 : 1;
                } else if (a.type === 'image' && b.type === 'image') {
                    return b.arrayIndex - a.arrayIndex;
                } else if (a.type === 'rangeStart' && b.type === 'rangeStart') {
                    return a.range.end - b.range.end;
                } else if (a.type === 'rangeStart') {
                    return 1;
                } else if (b.type === 'rangeStart') {
                    return -1;
                }
                if (a.type !== 'image' && b.type !== 'image') {
                    return a.range.start - b.range.start;
                }
                return 0;
            }

            return a.index - b.index;
        });
        list.forEach(item => {
            let range = item.type === 'image' ? null : item.range;
            let index = item.index;

            if (index > prevIndex) {
                // eslint-disable-next-line @typescript-eslint/no-explicit-any
                const textStyles = Object.assign({ ...rootTextStyles }, ...activeRanges as any[]) as TextStyles;

                newRenderList.push({
                    index: item.elementIndex,
                    text: content.substring(prevIndex, index),
                    textStyles,
                    actions: item.type === 'rangeEnd' && item.range?.actions || undefined
                });
            }

            if (item.type === 'rangeStart' && range) {
                activeRanges.push(range);
            } else if (item.type === 'rangeEnd') {
                activeRanges = activeRanges.filter(range => range !== item.range);
            } else if (item.type === 'image') {
                // eslint-disable-next-line @typescript-eslint/no-explicit-any
                let textStyles2 = Object.assign({ ...rootTextStyles }, ...activeRanges as any[]) as TextStyles;
                let imageWidth = pxToEm(
                    (((item.image.width && item.image.width.value) || 20) * 10) / (textStyles2.font_size || 12)
                );
                let imageHeight = pxToEm(
                    (((item.image.height && item.image.height.value) || 20) * 10) / (textStyles2.font_size || 12)
                );
                const wrapperStyle: Style = {
                    'font-size': pxToEm(((Number(textStyles2.font_size) || 12) * 10) / fontSize)
                };

                /* let svgFilterId = '';
                const tintColor = item.image.tint_color;
                const tintMode = correctTintMode(item.image.tint_mode, 'source_in');
                if (tintColor) {
                    const color = correctColor(item.image.tint_color);
                    svgFilterId = rootCtx.addSvgFilter(color, tintMode);
                    usedTintColors.push([color, tintMode]);
                } */

                newRenderList.push({
                    index: item.elementIndex,
                    image: {
                        url: item.image.url,
                        width: imageWidth,
                        height: imageHeight,
                        wrapperStyle,
                        // svgFilterId
                    }
                });
            }

            prevIndex = index;
        });

        if (prevIndex < content.length) {
            newRenderList.push({
                text: content.substring(prevIndex),
                textStyles: rootTextStyles
            });
        }

        renderList = newRenderList;
    }

    function draw(renderList: RenderItem[]): void {
        const isFocused = elem === ownerDocument.activeElement;
        let prevStart;
        let prevEnd;
        if (isFocused) {
            const selection = getSelection();
            prevStart = calcSelectionOffset(selection, elem, 'start', true);
            prevEnd = calcSelectionOffset(selection, elem, 'end', true);
        }
        elem.innerHTML = renderList.reduce((acc, item) => {
            let html = '';

            if ('text' in item) {
                if (item.text) {
                    html = `<span ${item.actions?.length ? `class="inplace-editor__link" data-actions="${htmlFilter(JSON.stringify(item.actions))}"` : ''} data-range-index="${htmlFilter(String(item.index))}" data-range="${htmlFilter(JSON.stringify(item.textStyles))}" style="${htmlFilter(calcTextStyle(item.textStyles) || '')}">${htmlFilter(item.text)}</span>`;
                }
            } else {
                html = `<span data-image="${htmlFilter(JSON.stringify(images[item.index]))}" data-image-index="${htmlFilter(String(item.index))}" class="inplace-editor__image-wrapper" style="${htmlFilter(makeStyle(item.image.wrapperStyle) || '')}">` +
                    `<img class="inplace-editor__image" src="${htmlFilter(item.image.url)}" loading="lazy" decoding="async" ` +
                    `aria-hidden="true" alt="" style="${htmlFilter(makeStyle({
                        width: item.image.width,
                        height: item.image.height,
                        // Normalizes line-height for the containing text line
                        'margin-top': customLineHeight ? `-${item.image.height}` : undefined,
                        'margin-bottom': customLineHeight ? `-${item.image.height}` : undefined,
                        filter: item.image.svgFilterId ? `url(#${item.image.svgFilterId})` : undefined
                    }) || '')}` +
                    '></span>';
            }

            return acc + html;
        }, '');
        Array.from(elem.querySelectorAll('.inplace-editor__link')).forEach(link => {
            if (link instanceof HTMLElement) {
                link.addEventListener('click', editLink);
            }
        });
        Array.from(elem.querySelectorAll('img')).forEach(img => {
            img.addEventListener('error', onImgError);
            img.parentElement?.addEventListener('click', editImage);
        });
        const sel = getSelection();
        if (sel && isFocused && prevStart !== undefined && prevEnd !== undefined) {
            sel.removeAllRanges();
            const range = document.createRange();
            setSelectionOffset(sel, elem, range, 'start', prevStart, true);
            setSelectionOffset(sel, elem, range, 'end', prevEnd, true);
            sel.addRange(range);
        }

        // rotated height - non-rotated height
        additionalHeight = elem.getBoundingClientRect().height - elem.offsetHeight;
        tick().then(() => {
            dispatch('resize');
        });
    }

    $: if (renderList.length && elem) {
        draw(renderList);
    }

    $: style = {
        'font-size': pxToEm(fontSize * scale),
        'line-height': lineHeight,
        // 'max-height': maxHeight,
        // '-webkit-line-clamp': lineClamp,
        color: textColor,
        // 'background-image': gradient
        transform: `rotate(${rotation}deg)`
    };

    function editLink(event: MouseEvent): void {
        const target = event.target;
        if (!(target instanceof HTMLElement)) {
            return;
        }

        const index = Number(target.dataset.rangeIndex);
        let range = ranges[index];
        const actions = range?.actions;
        if (!range || !actions || !actions[0]) {
            return;
        }

        range = copyValue(range);

        link2Dialog().show({
            target: actionsButton,
            value: actions[0].url || '',
            disabled: disabled || textDisabled,
            callback(val) {
                ranges = [...ranges];
                if (val) {
                    actions[0].url = val;
                    ranges[index] = range;
                } else {
                    ranges.length = index;
                }

                pushState({ text, ranges, images });
                updateRenderList(text, ranges, images);
            }
        });
    }

    function editImage(event: MouseEvent): void {
        const target = event.target;
        if (!(target instanceof HTMLElement)) {
            return;
        }

        const parent = target.closest('.inplace-editor__image-wrapper');
        if (!parent || !(parent instanceof HTMLElement)) {
            return;
        }

        const index = Number(parent.dataset.imageIndex);
        const image = images[index];
        if (!image) {
            return;
        }

        file2Dialog().show({
            target: toolbarElem,
            title: $l10nString('file.insert_image'),
            direction: 'right',
            subtype: 'image',
            hasSize: true,
            hasDelete: true,
            disabled: disabled || textDisabled,
            value: {
                url: image.url,
                width: image.width?.value,
                height: image.height?.value
            },
            callback(val) {
                images = [...images];

                if (val.url) {
                    const img = {
                        start: image.start,
                        url: val.url,
                        width: {
                            type: 'fixed',
                            value: val.width || 24
                        },
                        height: {
                            type: 'fixed',
                            value: val.height || 24
                        }
                    } as const;
                    images[index] = img;
                } else {
                    images.length = index;
                }

                pushState({ text, ranges, images });
                updateRenderList(text, ranges, images);
            },
            onHide() {
                if (!images[index]) {
                    images = [...images];
                    images.length = index;
                    pushState({ text, ranges, images });
                    updateRenderList(text, ranges, images);
                    restoreSelection(image.start, image.start, false);
                } else {
                    restoreSelection(image.start + 1, image.start + 1, false);
                }
            }
        });
    }

    function simplifyRanges(text: string, ranges: TextRange[]): TextRange[] {
        const newRanges: TextRange[] = [];
        const styles: TextStyles[] = [];

        for (let i = 0; i < text.length; ++i) {
            styles[i] = rootTextStyles;
        }

        ranges.forEach(range => {
            for (let i = range.start; i < range.end; ++i) {
                const style = rangeStyle(range);

                styles[i] = {
                    ...styles[i],
                    ...style
                };
            }
        });

        const appendRange = (start: number, end: number, style: TextStyles): void => {
            if (Object.keys(style).length > 0) {
                newRanges.push({
                    start,
                    end,
                    ...style
                });
            }
        };

        let start = 0;
        for (let i = 1; i < text.length; ++i) {
            if (!isEqual(styles[start], styles[i])) {
                appendRange(start, i, styles[start]);
                start = i;
            }
        }
        appendRange(start, text.length, styles[start]);

        return newRanges;
    }

    function setRange(prop: string, value: unknown, start?: number, end?: number): void {
        const selection = getSelection();
        start ||= calcSelectionOffset(selection, elem, 'start', false);
        end ||= calcSelectionOffset(selection, elem, 'end', false);

        if (start === end || textDisabled) {
            start = 0;
            end = text.length;
        }

        let newRanges = [
            ...ranges,
            {
                start,
                end,
                [prop]: value
            }
        ];
        newRanges = simplifyRanges(text, newRanges);

        ranges = newRanges;
        pushState({ text, ranges, images });
        updateRenderList(text, ranges, images);
        updateState();
    }

    function restoreSelection(start = selectionStartTextOnly, end = selectionEndTextOnly, includeImages = false): void {
        const sel = getSelection();
        if (sel) {
            sel.removeAllRanges();
            const range = document.createRange();
            setSelectionOffset(sel, elem, range, 'start', start, includeImages);
            setSelectionOffset(sel, elem, range, 'end', end, includeImages);
            sel.addRange(range);
        }
    }

    function pushState(state: State): void {
        historyStack = historyStack.slice(0, historyIndex + 1);
        historyStack.push(state);
        historyIndex = historyStack.length - 1;
    }

    function undoAction(): void {
        if (historyIndex === 0) {
            return;
        }

        --historyIndex;
        const state = historyStack[historyIndex];
        text = state.text;
        ranges = state.ranges;
        images = state.images;
        updateRenderList(text, ranges, images);
    }

    function redoAction(): void {
        if (historyIndex === historyStack.length - 1) {
            return;
        }

        ++historyIndex;
        const state = historyStack[historyIndex];
        text = state.text;
        ranges = state.ranges;
        images = state.images;
        updateRenderList(text, ranges, images);
    }

    function onImgError(event: Event): void {
        if (event.target && 'classList' in event.target) {
            (event.target as HTMLElement).classList.add('inplace-editor__image_hidden');
        }
    }

    function onKeydown(event: KeyboardEvent): void {
        if (undo.isPressed(event)) {
            undoAction();
        } else if (redo.isPressed(event)) {
            redoAction();
        } else if (multilineSubmit.isPressed(event)) {
            dispatch('close');
        }
    }

    $: if ($selectedLeaf !== leaf) {
        dispatch('close');
    }

    function onInput(): void {
        const state = getState(elem);
        text = state.text;
        ranges = state.ranges;
        images = state.images;
        pushState({
            text,
            ranges,
            images
        });
        dispatch('resize');
    }

    function onPaste(event: ClipboardEvent): void {
        event.preventDefault();
        if (event.clipboardData) {
            let text = event.clipboardData.getData('text/plain');
            text = text.trim();
            document.execCommand('inserttext', false, text);
        }
    }

    function onSelectionChange(): void {
        if (ownerDocument.activeElement === elem) {
            const selection = getSelection();
            selectionStartTextOnly = calcSelectionOffset(selection, elem, 'start', false);
            selectionEndTextOnly = calcSelectionOffset(selection, elem, 'end', false);

            updateState();
        }
    }

    function updateState(): void {
        let start = selectionStartTextOnly;
        let end = selectionEndTextOnly;

        if (start === end) {
            start = 0;
            end = text.length;
        }

        const findVal = (prop: keyof TextRange, val?: unknown) => {
            if (selectionStartTextOnly !== selectionEndTextOnly) {
                return ranges
                    .map(range => {
                        return {
                            range,
                            len: Math.min(range.end, end) - Math.max(range.start, start),
                            val: range[prop]
                        };
                    })
                    .filter(it => it.len > 0 && it.val && (!val || val === it.val))
                    .sort((a, b) => b.len - a.len)[0]?.val;
            }
            return ranges.find(range => {
                return range[prop] && (
                    !val ||
                    val === range[prop]
                ) &&
                range.start <= selectionStartTextOnly &&
                selectionStartTextOnly <= range.end;
            })?.[prop];
        };

        size = findVal('font_size') || rootTextStyles.font_size || 12;
        color = findVal('text_color') || rootTextStyles.text_color || '#000';
        weight = '';
        underline = Boolean(findVal('underline', 'single') || rootTextStyles.underline === 'single');
        strike = Boolean(findVal('strike', 'single') || rootTextStyles.strike === 'single');
    }

    function onAddImage(): void {
        if (disabled || textDisabled) {
            return;
        }

        const start = selectionStartTextOnly;
        const index = images.length;

        file2Dialog().show({
            target: toolbarElem,
            title: $l10nString('file.insert_image'),
            direction: 'right',
            subtype: 'image',
            hasSize: true,
            hasDelete: true,
            value: {
                url: '',
                width: 24,
                height: 24
            },
            callback(val) {
                images = [...images];

                if (val.url) {
                    const img = {
                        start,
                        url: val.url,
                        width: {
                            type: 'fixed',
                            value: val.width || 24
                        },
                        height: {
                            type: 'fixed',
                            value: val.height || 24
                        }
                    } as const;
                    images[index] = img;
                } else {
                    images.length = index;
                }

                pushState({ text, ranges, images });
                updateRenderList(text, ranges, images);
            },
            onHide() {
                if (!images[index]) {
                    images = [...images];
                    images.length = index;
                    pushState({ text, ranges, images });
                    updateRenderList(text, ranges, images);
                    restoreSelection(start, start, false);
                } else {
                    restoreSelection(start + 1, start + 1, false);
                }
            }
        });
    }

    function onWeightChange(): void {
        setRange('font_weight', weight);
    }

    function onFontSizeInput(event: Event): void {
        const text = (event.target as HTMLElement).textContent;
        const size = Number(text);
        if (!Number.isNaN(size)) {
            setRange('font_size', size, selectionStartTextOnly, selectionEndTextOnly);
        }
    }

    function onTextColor(): void {
        const start = selectionStartTextOnly;
        const end = selectionEndTextOnly;

        let value;
        let paletteId;

        if (isPaletteColor(color)) {
            value = '';
            paletteId = valueToPaletteId(color);
        } else {
            value = color;
            paletteId = '';
        }

        color2Dialog().show({
            target: toolbarElem,
            value,
            paletteId,
            disabled,
            showPalette: true,
            callback(value, paletteId) {
                let val;
                if (paletteId) {
                    val = paletteIdToValue(paletteId);
                } else {
                    val = value;
                }
                setRange('text_color', val, start, end);
            }
        });
    }

    function onTextAlign(): void {
        textAlign2Dialog().show({
            target: textAlignButton,
            value: textAlign,
            disabled,
            callback(val) {
                textAlign = val;
            }
        });
    }

    function onUnderline(): void {
        if (disabled) {
            return;
        }

        setRange('underline', underline ? undefined : 'single', selectionStartTextOnly, selectionEndTextOnly);
        restoreSelection();
    }

    function onStrike(): void {
        if (disabled) {
            return;
        }

        setRange('strike', strike ? undefined : 'single', selectionStartTextOnly, selectionEndTextOnly);
        restoreSelection();
    }

    function onActions(): void {
        if (disabled) {
            return;
        }

        let start = selectionStartTextOnly;
        let end = selectionEndTextOnly;

        if (start === end) {
            start = 0;
            end = text.length;
        }

        const index = ranges.length;

        link2Dialog().show({
            target: actionsButton,
            value: '',
            callback(val) {
                ranges = [...ranges];
                if (val) {
                    const range = {
                        start,
                        end,
                        underline: 'single' as const,
                        actions: [{
                            log_id: 'text_link',
                            log_url: actionLogUrlVariable ? `@{${actionLogUrlVariable}}` : undefined,
                            url: val
                        }]
                    };
                    ranges[index] = range;
                } else {
                    ranges.length = index;
                }

                pushState({ text, ranges, images });
                updateRenderList(text, ranges, images);
            }
        });
    }

    onMount(() => {
        setTimeout(() => {
            elem.focus();
        });
        updateState();
    });
</script>

<!-- svelte-ignore a11y_no_static_element_interactions -->
<div
    class="inplace-editor inplace-editor_direction_{$direction}"
    class:inplace-editor_disabled={disabled}
    bind:this={root}
    on:click|stopPropagation
    on:dblclick|stopPropagation
    on:keydown|stopPropagation={onKeydown}
    on:pointerdown|stopPropagation
>
    <div
        class="inplace-editor__toolbar"
        style:transform="translateY({-additionalHeight / 2}px)"
        transition:fade={{ duration: 150 }}
    >
        <div class="inplace-editor__toolbar-inner" bind:this={toolbarElem}>
            <div
                class="inplace-editor__button2 inplace-editor__button2_text"
                title={$l10nString('props.font_size')}
                contenteditable={disabled ? undefined : 'true'}
                autocapitalize="off"
                spellcheck="false"
                {...{
                    autocomplete: 'off',
                    autocorrect: 'off'
                }}
                on:input={onFontSizeInput}
            >
                {size}
            </div>
            <button
                class="inplace-editor__button2 inplace-editor__button2_color"
                title={$l10nString('props.text_color')}
                on:click|preventDefault={onTextColor}
            >
                <ColorPreview {color} mix="inplace-editor__color-preview" />
            </button>
            <!-- svelte-ignore a11y_consider_explicit_label -->
            <button
                class="inplace-editor__button2 inplace-editor__button2_color"
                title={$l10nString('props.text_alignment_horizontal')}
                bind:this={textAlignButton}
                on:click|preventDefault={onTextAlign}
            >
                <div class="inplace-editor__select-icon inplace-editor__select-icon_align_{textAlign}"></div>
            </button>
            <div class="inplace-editor__button2 inplace-editor__button2_select">
                <div class="inplace-editor__select-icon inplace-editor__select-icon_weight"></div>
                <select
                    class="inplace-editor__select"
                    title={$l10nString('props.font_weight')}
                    {disabled}
                    bind:value={weight}
                    on:change={onWeightChange}
                >
                    <option value=""></option>
                    <option value="light">{$l10nString('props.font_weight_light')}</option>
                    <option value="regular">{$l10nString('props.font_weight_normal')}</option>
                    <option value="medium">{$l10nString('props.font_weight_medium')}</option>
                    <option value="bold">{$l10nString('props.font_weight_bold')}</option>
                </select>
            </div>
            <!-- svelte-ignore a11y_consider_explicit_label -->
            <button
                class="inplace-editor__button2"
                class:inplace-editor__button2_toggled={underline}
                title={$l10nString('props.underline')}
                on:click|preventDefault={onUnderline}
            >
                <div class="inplace-editor__select-icon inplace-editor__select-icon_underline"></div>
            </button>
            <!-- svelte-ignore a11y_consider_explicit_label -->
            <button
                class="inplace-editor__button2"
                class:inplace-editor__button2_toggled={strike}
                title={$l10nString('props.strike')}
                on:click|preventDefault={onStrike}
            >
                <div class="inplace-editor__select-icon inplace-editor__select-icon_strike"></div>
            </button>

            <div class="inplace-editor__separator"></div>

            <!-- svelte-ignore a11y_consider_explicit_label -->
            <button
                class="inplace-editor__button2"
                title={$l10nString('props.actions')}
                bind:this={actionsButton}
                on:click|preventDefault={onActions}
            >
                <div class="inplace-editor__select-icon inplace-editor__select-icon_actions"></div>
            </button>
            <!-- svelte-ignore a11y_consider_explicit_label -->
            <button
                class="inplace-editor__button2"
                class:inplace-editor__button2_disabled={textDisabled}
                disabled={textDisabled}
                title={$l10nString('image')}
                on:click|preventDefault={onAddImage}
            >
                <div class="inplace-editor__select-icon inplace-editor__select-icon_image"></div>
            </button>
        </div>
    </div>

    <div
        class="inplace-editor__content inplace-editor__content_align_{textAlign}"
        contenteditable={(disabled || textDisabled) ? undefined : 'true'}
        style={makeStyle(style)}

        bind:this={elem}
        on:input={onInput}
        on:paste={onPaste}

        on:selectionchange={onSelectionChange}
        on:mousemove={onSelectionChange}
        on:mouseup|preventDefault={onSelectionChange}
        on:contextmenu={onSelectionChange}
        on:keyup={onSelectionChange}
        on:keydown={onSelectionChange}
        on:focus={onSelectionChange}
    >
    </div>

    {#if showTextDisabledAlert}
        <div
            class="inplace-editor__disabled-alert-wrapper"
            transition:fly={{ y: 20, duration: 150 }}
        >
            <div class="inplace-editor__disabled-alert">
                <!-- eslint-disable-next-line svelte/no-at-html-tags -->
                {@html alertTailImage}

                {$l10nString('inplaceEditorDisabled')}

                <!-- svelte-ignore a11y_consider_explicit_label -->
                <button
                    class="inplace-editor__disabled-alert-close"
                    title={$l10nString('close')}
                    on:click={() => showTextDisabledAlert = false}
                >
                </button>
            </div>
        </div>
    {/if}
</div>

<style>
    .inplace-editor {
        position: relative;
        pointer-events: auto;
        color: var(--text-primary);
    }

    .inplace-editor__toolbar {
        position: absolute;
        z-index: 1;
        top: -50px;
        left: 50%;
        width: 0;
        display: flex;
        justify-content: center;
    }

    .inplace-editor__toolbar-inner {
        display: flex;
        align-items: center;
        gap: 4px;
        padding: 4px 8px;
        border-radius: 8px;
        background-color: var(--background-primary);
        box-shadow: var(--shadow-16);
    }

    .inplace-editor__select-icon {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: no-repeat 50% 50%;
        background-size: 20px;
        filter: var(--icon-filter);
    }

    .inplace-editor__select-icon_weight {
        background-image: url(../../assets/fontWeight.svg);
    }

    .inplace-editor__select-icon_underline {
        background-image: url(../../assets/underline.svg);
    }

    .inplace-editor__select-icon_strike {
        background-image: url(../../assets/strike.svg);
    }

    .inplace-editor__select-icon_image {
        background-image: url(../../assets/img.svg);
    }

    .inplace-editor__select-icon_actions {
        background-image: url(../../assets/link.svg);
    }

    .inplace-editor__select-icon_align_left {
        background-image: url(../../assets/alignLeft2.svg);
    }

    .inplace-editor_direction_ltr .inplace-editor__select-icon_align_start,
    .inplace-editor_direction_rtl .inplace-editor__select-icon_align_end {
        background-image: url(../../assets/alignLeftLogical2.svg);
    }

    .inplace-editor__select-icon_align_center {
        background-image: url(../../assets/alignCenter2.svg);
    }

    .inplace-editor__select-icon_align_right {
        background-image: url(../../assets/alignRight2.svg);
    }

    .inplace-editor_direction_ltr .inplace-editor__select-icon_align_end,
    .inplace-editor_direction_rtl .inplace-editor__select-icon_align_start {
        background-image: url(../../assets/alignRightLogical2.svg);
    }

    .inplace-editor__select {
        box-sizing: border-box;
        width: 28px;
        height: 28px;
        margin: 0;
        padding: 4px 8px;
        font: inherit;
        font-size: 14px;
        border: none;
        appearance: none;
        opacity: 0;
        cursor: pointer;
    }

    .inplace-editor_disabled .inplace-editor__select {
        cursor: default;
    }

    .inplace-editor__content {
        white-space: pre-wrap;
        outline: 3px solid #00a2ff;
        border-radius: 1px;
        overflow: hidden;
        white-space: pre-wrap;
        word-break: break-word;
    }

    .inplace-editor__content_align_left,
    .inplace-editor_direction_ltr .inplace-editor__content_align_start,
    .inplace-editor_direction_rtl .inplace-editor__content_align_end {
        text-align: left;
    }

    .inplace-editor__content_align_center {
        text-align: center;
    }

    .inplace-editor__content_align_right,
    .inplace-editor_direction_ltr .inplace-editor__content_align_end,
    .inplace-editor_direction_rtl .inplace-editor__content_align_start {
        text-align: right;
    }

    :global(.inplace-editor__image-wrapper) {
        cursor: pointer;
        outline: 1px solid transparent;
        transition: outline-color .15s ease-in-out;
        border-radius: 1px;
    }

    :global(.inplace-editor__image-wrapper:hover) {
        outline-color: #00a2ff;
    }

    :global(.inplace-editor__image) {
        display: inline-block;
        vertical-align: middle;
    }

    :global(.inplace-editor__image_hidden) {
        display: none;
    }

    :global(.inplace-editor__link) {
        cursor: pointer;
    }

    :global(.inplace-editor__link:hover) {
        background: var(--fill-accent-4);
    }

    .inplace-editor__button2 {
        position: relative;
        box-sizing: border-box;
        display: flex;
        align-items: center;
        justify-content: center;
        min-width: 32px;
        height: 32px;
        margin: 0;
        padding: 0;
        font: inherit;
        font-size: 14px;
        line-height: 20px;
        color: inherit;
        background: none;
        border: 1px solid transparent;
        border-radius: 6px;
        cursor: pointer;
        transition: .15s ease-in-out;
        transition-property: background-color, border-color;
    }

    .inplace-editor_disabled .inplace-editor__button2:not(.inplace-editor__button2_color) {
        cursor: default;
        opacity: .3;
    }

    .inplace-editor:not(.inplace-editor_disabled) .inplace-editor__button2:not(.inplace-editor__button2_disabled):hover {
        background-color: var(--fill-accent-2);
    }

    .inplace-editor__button2:focus-visible {
        outline: 1px solid var(--accent-purple);
    }

    .inplace-editor__button2_text {
        padding: 5px;
        cursor: text;
    }

    .inplace-editor_disabled .inplace-editor__button2_text {
        cursor: default;
    }

    .inplace-editor:not(.inplace-editor_disabled) .inplace-editor__button2_text:hover,
    .inplace-editor__button2_text:focus {
        background-color: transparent;
        border-color: var(--fill-transparent-4);
        outline: none;
    }

    .inplace-editor__button2_toggled {
        background: var(--fill-accent-2);
    }

    .inplace-editor__button2_select:focus-within {
        outline: 1px solid var(--accent-purple);
    }

    .inplace-editor__button2_disabled {
        cursor: default;
    }

    .inplace-editor__button2_disabled .inplace-editor__select-icon {
        opacity: .3;
    }

    :global(.inplace-editor__color-preview.inplace-editor__color-preview.inplace-editor__color-preview) {
        width: 20px;
        height: 20px;
        border-radius: 4px;
    }

    .inplace-editor__separator {
        width: 1px;
        height: 24px;
        margin: 0 4px;
        background-color: var(--fill-transparent-3);
    }

    .inplace-editor__disabled-alert-wrapper {
        position: absolute;
        z-index: 1;
        top: calc(100% + 14px);
        left: 50%;
    }

    .inplace-editor__disabled-alert {
        position: absolute;
        top: 0;
        left: 0;
        box-sizing: border-box;
        padding: 8px 28px 8px 12px;
        width: 256px;
        min-width: 100%;
        font-size: 14px;
        line-height: 20px;
        color: var(--text-primary);
        background: var(--background-tertiary);
        border-radius: 8px;
        box-shadow: var(--shadow-32);
        transform: translateX(-50%);
    }

    .inplace-editor__disabled-alert-close {
        width: 16px;
        height: 16px;
        position: absolute;
        top: 6px;
        right: 6px;
        border: none;
        background: no-repeat 50% 50% url(../../assets/closeDialog.svg);
        background-size: 10px;
        appearance: none;
        filter: var(--icon-filter);
        opacity: .7;
        cursor: pointer;
        transition: opacity .15s ease-in-out;
    }

    .inplace-editor__disabled-alert-close:hover,
    .inplace-editor__disabled-alert-close:focus-visible {
        opacity: 1;
    }

    :global(.inplace-editor__alert-tail) {
        position: absolute;
        top: -5px;
        left: calc(50% - 8.5px);
        color: var(--background-tertiary);
    }
</style>
