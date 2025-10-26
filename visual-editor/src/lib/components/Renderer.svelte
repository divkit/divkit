<script lang="ts">
    /* eslint-disable max-len */
    import { get } from 'svelte/store';
    import { render as divkitRender, SizeProvider, createGlobalVariablesController, createVariable, evalExpression, type DivkitDebugInstance } from '@divkitframework/divkit/client-devtool';
    import type { DivExtensionClass, DivJson } from '@divkitframework/divkit/typings/common';
    import { getContext, onDestroy, tick } from 'svelte';
    import type { AnyVariable, GlobalVariablesController } from '@divkitframework/divkit/typings/variables';
    import {
        getLeafDepth,
        getDepth,
        type HighlightMode,
        type ComponentProps
    } from '../data/structure';
    import { formatSize } from '../utils/formatSize';
    import '@divkitframework/divkit/dist/client.css';
    import { treeLeafContains, findLeaf } from '../utils/tree';
    import { bestSnap, type Snap } from '../utils/snap';
    import { deleteComponent, moveComponentUp, moveComponentDown, moveComponentLeft, moveComponentRight, bigMoveComponentUp, bigMoveComponentDown, bigMoveComponentRight, bigMoveComponentLeft, resizeComponentUp, resizeComponentDown, resizeComponentLeft, resizeComponentRight, bigResizeComponentUp, bigResizeComponentDown, bigResizeComponentRight, bigResizeComponentLeft, copy as copyShortcut, paste as pasteShortcut, cancel } from '../utils/keybinder/shortcuts';
    import { Lottie } from '../data/lottieExt';
    import { templatesCheck } from '../utils/checkDivjson';
    import type { TreeLeaf } from '../ctx/tree';
    import { isEqual } from '../utils/isEqual';
    import { isRangeStyleEqual, type TextImage, type TextRange } from '../utils/range';
    import { namedTemplates } from '../data/templates';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import { APP_CTX, type AppContext } from '../ctx/appContext';
    import { setObjectProperty } from '../utils/objectProperty';
    import { getCroppedBbox } from '../utils/getCroppedBbox';
    import { AddLeafCommand } from '../data/commands/addLeaf';
    import { MoveLeafCommand } from '../data/commands/moveLeaf';
    import { SetPropertyCommand } from '../data/commands/setProperty';
    import type { SetPropertyItem } from '../data/commands/setProperty';
    import { degToRad } from '../utils/degToRad';
    import { type JsonVariable } from '../data/customVariables';
    import type { ViewerError } from '../utils/errors';
    import { isUserTemplateWithoutChilds } from '../data/userTemplates';
    import { getGridPosition, getGridProps, type GridProps } from '../utils/grid';
    import { sizesToFall } from '../utils/sizesToFall';
    import { sum } from '../utils/sum';
    import { Truthy } from '../utils/truthy';
    import type { TankerMeta } from '../../lib';
    import { getRotationFromMatrix } from '../utils/getRotationFromMatrix';
    import { rectAngleIntersection } from '../utils/rectAngleIntersection';
    import { CHESS_EMPTY_IMAGE, DIVKIT_EMPY_IMAGE, EMPTY_IMAGE } from '../data/doc';
    import Select from './Select.svelte';

    export let viewport: string;
    export let theme: 'light' | 'dark';

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
    const {
        rootConfigurable,
        actionLogUrlVariable,
        state,
        setRendererApi,
        inplaceEditor,
        file2Dialog,
        customFontFaces
    } = getContext<AppContext>(APP_CTX);

    const {
        divjsonStore,
        customVariables,
        timers,
        tanker,
        copiedLeaf,
        selectedLeaf,
        selectedElem,
        highlightLeaf,
        highlightElem,
        highlightMode,
        highlightGradientAngle,
        highlightRanges,
        rendererErrors,
        locale,
        readOnly,
        tree,
        sources,
        direction,
        safeAreaEmulation,
        safeAreaEmulationEnabled
    } = state;

    setRendererApi({
        containerProps() {
            const bbox = clonesContainer.getBoundingClientRect();
            return {
                width: bbox.width,
                height: bbox.height
            };
        },
        selectedElemProps() {
            if (!$selectedElem || !clonesContainer) {
                return null;
            }

            const containerBbox = clonesContainer.getBoundingClientRect();
            const elemBbox = $selectedElem.getBoundingClientRect();

            return {
                top: elemBbox.top - containerBbox.top,
                left: elemBbox.left - containerBbox.left,
                width: elemBbox.width,
                height: elemBbox.height
            };
        },
        focus() {
            root.focus({
                preventScroll: true
            });
        },
        evalJson(json) {
            return evalJson(json);
        }
    });

    const SNAP_DIST = 6;
    const BIG_MOVE_OFFSET = 10;
    const BIG_RESIZE = 10;

    interface SnapOffset extends Snap {
        pos: number;
    }

    $: size = viewport.split('x').map(Number);

    let root: HTMLElement;
    let previewWrapper: HTMLElement;
    let rootPreview: HTMLElement;
    let componentClone: HTMLElement | null;
    let componentCloneRect = {
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        width: 0,
        height: 0
    };
    let componentCloneRotation = 0;
    let componentCloneText = '';
    let clonesContainer: HTMLElement;
    let resizeObserver: ResizeObserver | null;
    let instance: DivkitDebugInstance | undefined;
    let snapLineX: number | null = null;
    let snapLineY: number | null = null;
    let scrollX = 0;
    let scrollY = 0;
    let showInplaceEditor = false;
    let isDistanceMode = true;
    let isResize = false;
    let isRotate = false;
    let themeVariable: AnyVariable | null = null;
    let globalVariablesController: GlobalVariablesController | undefined;
    const globalVariables: Record<string, AnyVariable> = {};
    let mountedAndUpdatedLeafs = new Set<string>();
    const components = new Map<HTMLElement, ComponentProps>();
    let isUpdating = false;
    let newRendererErrors: Record<string, ViewerError[]>;

    let gridResize: {
        columns: number[];
        rows: number[];
    } | null = null;

    let highlights: {
        elem: HTMLElement;
        permanent: boolean;
        isRoot: boolean;
        isResize: boolean;
        isRotate: boolean;
        top: number;
        left: number;
        right: number;
        bottom: number;
        width: string;
        height: string;
        widthNum: number;
        heightNum: number;
        margin: string;
        margins: {
            top: number;
            right: number;
            bottom: number;
            left: number;
        },
        padding: string;
        paddings: {
            top: number;
            right: number;
            bottom: number;
            left: number;
        },
        rotation: number;
        emptyFileType: string;
        state?: {
            selected: string;
            selectedValue: string;
            list: {
                text: string;
                value: string;
            }[];
        };
        insets?: string;
        gradientAngle?: string;
        visibleText?: string;
        grid?: {
            columns: number[];
            rows: number[];
        };
    }[] | null = null;

    interface DistanceInfo {
        orientation: 'horizontal' | 'vertical';
        length: number;
        top: number;
        left: number;
        sub?: {
            orientation: 'horizontal' | 'vertical';
            length: number;
            top: number;
            left: number;
        };
    }

    let highlightDistance: DistanceInfo[] | null = null;

    function calcRotationRad(elem: HTMLElement): number {
        let rotation = 0;

        let temp: HTMLElement | null = elem;
        while (temp && temp !== previewWrapper) {
            const computed = window.getComputedStyle(temp);
            rotation += getRotationFromMatrix(computed.transform);
            temp = temp.parentElement;
        }

        return rotation;
    }

    function withoutRotation(elem: HTMLElement, cb: () => void): void {
        const rotationRestore = new Map<HTMLElement, string>();
        let temp: HTMLElement | null = elem;
        while (temp && temp !== previewWrapper) {
            const computed = window.getComputedStyle(temp);
            if (computed.transform) {
                rotationRestore.set(temp, temp.style.transform);
                temp.style.transform = 'none';
            }
            temp = temp.parentElement;
        }

        cb();

        for (const [elem, transform] of rotationRestore) {
            elem.style.transform = transform;
        }
    }

    const updateHighlight = (
        highlightElems: HTMLElement[] | null,
        selectedElem: HTMLElement | null,
        clone: HTMLElement | null,
        bestDragTarget: BestDragTarget | null,
        showInplaceEditor: boolean,
        highlightMode: HighlightMode,
        highlightGradientAngle: number,
        isDistanceMode: boolean,
        isResize: boolean,
        gridResize: {
            columns: number[];
            rows: number[];
        } | null
        // eslint-disable-next-line max-params
    ) => {
        if (!root) {
            // hmr
            return;
        }

        const previewBbox = previewWrapper.getBoundingClientRect();

        let elems: HTMLElement[] = [];
        let distanceToElement: HTMLElement | undefined;
        if (bestDragTarget || showInplaceEditor) {
            elems = [];
        } else if (clone) {
            elems = [clone];
        } else {
            elems = [...new Set([...(highlightElems || []), selectedElem])].filter(Truthy);
            if (
                highlightElems &&
                highlightElems.length === 1 &&
                selectedElem &&
                (selectedElem.offsetWidth || selectedElem.offsetHeight) &&
                isDistanceMode
            ) {
                distanceToElement = highlightElems[0];
            }
        }

        highlights = elems
            .filter(elem => elem.isConnected)
            .map(elem => {
                const props = components.get(elem);
                const processedJson = props?.processedJson;

                const computedStyle = getComputedStyle(elem);
                const margin = computedStyle.margin;
                const marginTop = parseInt(computedStyle.marginTop);
                const marginRight = parseInt(computedStyle.marginRight);
                const marginBottom = parseInt(computedStyle.marginBottom);
                const marginLeft = parseInt(computedStyle.marginLeft);
                const padding = computedStyle.padding;
                const paddingTop = parseInt(computedStyle.paddingTop);
                const paddingRight = parseInt(computedStyle.paddingRight);
                const paddingBottom = parseInt(computedStyle.paddingBottom);
                const paddingLeft = parseInt(computedStyle.paddingLeft);

                let left = 0;
                let top = 0;
                if (elem === clone) {
                    left = componentCloneRect.left - previewBbox.left + scrollX;
                    top = componentCloneRect.top - previewBbox.top + scrollY;
                } else {
                    const parentRotation = elem.parentElement ? calcRotationRad(elem.parentElement) : 0;
                    const origTransform = elem.style.transform;
                    elem.style.transform = `rotate(${-parentRotation}rad)`;
                    const elemBbox = elem.getBoundingClientRect();
                    left = elemBbox.left - previewBbox.left + scrollX;
                    top = elemBbox.top - previewBbox.top + scrollY;
                    elem.style.transform = origTransform;
                }

                const finalBox = {
                    top: top - marginTop,
                    right: left + elem.offsetWidth + marginRight,
                    bottom: top + elem.offsetHeight + marginBottom,
                    left: left - marginLeft,
                };

                let emptyFileType = '';
                let state;
                if (elem === selectedElem && processedJson) {
                    const json = $selectedLeaf?.props.json;
                    if (json) {
                        if (json.type === '_template_lottie') {
                            // if (!json.lottie_params?.lottie_url) {
                            emptyFileType = 'lottie';
                            // }
                        } else if (json.type === '_template_close') {
                            // do nothing
                        } else if (processedJson.type === 'image'/*  && processedJson.image_url === EMPTY_IMAGE */) {
                            emptyFileType = 'image';
                        } else if (processedJson.type === 'gif'/*  && processedJson.gif_url === EMPTY_IMAGE */) {
                            emptyFileType = 'gif';
                        }

                        if (
                            processedJson.type === 'state' &&
                            $selectedLeaf?.props.devapi &&
                            Array.isArray(processedJson.states) &&
                            processedJson.states.length
                        ) {
                            const val = $selectedLeaf?.props.devapi.getState();
                            state = {
                                selected: val,
                                selectedValue: val,
                                list: processedJson.states.map(it => ({
                                    text: it.state_id || '<untitled>',
                                    value: it.state_id
                                }))
                            };
                        } else if (
                            processedJson.type === 'tabs' &&
                            $selectedLeaf?.props.devapi &&
                            Array.isArray(processedJson.items) &&
                            processedJson.items.length
                        ) {
                            const val = $selectedLeaf?.props.devapi.getState();
                            state = {
                                selected: processedJson.items[val]?.title || '<untitled>',
                                selectedValue: val,
                                list: processedJson.items.map((it, index) => ({
                                    text: it.title || '<untitled>',
                                    value: index
                                }))
                            };
                        }
                    }
                }

                let insets;
                let gradientAngle;
                if (highlightMode && elem === selectedElem) {
                    if (highlightMode === 'margins') {
                        insets = [
                            'M 0 0',
                            `h ${finalBox.right - finalBox.left}`,
                            `v ${finalBox.bottom - finalBox.top}`,
                            `h ${-(finalBox.right - finalBox.left)}`,
                            'Z',
                            `M ${marginLeft} ${marginTop}`,
                            `v ${elem.offsetHeight}`,
                            `h ${elem.offsetWidth}`,
                            `v ${-elem.offsetHeight}`,
                            'Z'
                        ].join(' ');
                    } else if (highlightMode === 'paddings') {
                        insets = [
                            `M ${marginLeft} ${marginTop}`,
                            `h ${elem.offsetWidth}`,
                            `v ${elem.offsetHeight}`,
                            `h ${-elem.offsetWidth}`,
                            'Z',
                            `M ${marginLeft + paddingLeft} ${marginTop + paddingTop}`,
                            `v ${elem.offsetHeight - paddingTop - paddingBottom}`,
                            `h ${elem.offsetWidth - paddingLeft - paddingRight}`,
                            `v ${-(elem.offsetHeight - paddingTop - paddingBottom)}`,
                            'Z'
                        ].join(' ');
                    } else if (highlightMode === 'gradient') {
                        const points = rectAngleIntersection(elem.offsetWidth, elem.offsetHeight, highlightGradientAngle / 180 * Math.PI);
                        gradientAngle = `M ${marginLeft + points.from.x} ${marginTop + points.from.y} L ${marginLeft + points.to.x} ${marginTop + points.to.y}`;
                    }
                }

                const isGrid = props?.processedJson?.type === 'grid';
                let gridProps: GridProps | undefined;
                if (isGrid) {
                    withoutRotation(elem, () => {
                        gridProps = getGridProps(elem);
                    });
                }

                return {
                    elem,
                    permanent: elem === selectedElem,
                    isRoot: elem === $tree.props.node && !rootConfigurable,
                    isResize: elem === clone && isResize,
                    isRotate: elem === clone && isRotate,
                    top: finalBox.top,
                    left: finalBox.left,
                    right: previewBbox.width - finalBox.right,
                    bottom: previewBbox.height - finalBox.bottom,
                    width: formatSize(elem.offsetWidth),
                    height: formatSize(elem.offsetHeight),
                    widthNum: elem.offsetWidth,
                    heightNum: elem.offsetHeight,
                    margin,
                    margins: {
                        top: marginTop,
                        right: marginRight,
                        bottom: marginBottom,
                        left: marginLeft
                    },
                    padding,
                    paddings: {
                        top: paddingTop,
                        right: paddingRight,
                        bottom: paddingBottom,
                        left: paddingLeft
                    },
                    rotation: clone === elem ? componentCloneRotation : calcRotationRad(elem) / Math.PI * 180,
                    emptyFileType,
                    state,
                    insets,
                    gradientAngle,
                    visibleText: elem === clone ? componentCloneText : '',
                    grid: gridProps && (gridResize || {
                        columns: sizesToFall(gridProps.columns.slice(0, gridProps.columns.length - 1)),
                        rows: sizesToFall(gridProps.rows.slice(0, gridProps.rows.length - 1))
                    }) || undefined
                };
            });

        if (distanceToElement?.offsetHeight && selectedElem?.offsetHeight) {
            const fromBbox = selectedElem.getBoundingClientRect();
            const toBbox = distanceToElement.getBoundingClientRect();
            const info: DistanceInfo[] = [];

            const INVERSE = {
                top: 'bottom',
                right: 'left',
                bottom: 'top',
                left: 'right'
            } as const;
            const COMPARE_MULTIPLY = {
                top: -1,
                right: 1,
                bottom: 1,
                left: -1
            } as const;
            const MIDDLE = {
                vertical: ['left', 'right'],
                horizontal: ['top', 'bottom']
            } as const;
            const ORIENTATION = {
                top: 'vertical',
                right: 'horizontal',
                bottom: 'vertical',
                left: 'horizontal'
            } as const;
            const SUB_ORIENTATION = {
                top: 'horizontal',
                right: 'vertical',
                bottom: 'horizontal',
                left: 'vertical'
            } as const;

            for (const side of ['top', 'right', 'bottom', 'left'] as const) {
                const orientation = ORIENTATION[side];
                const subOrientation = SUB_ORIENTATION[side];
                const multiply = COMPARE_MULTIPLY[side];

                for (const oppositeSide of [INVERSE[side], side]) {
                    if (fromBbox[side] * multiply < toBbox[oppositeSide] * multiply) {
                        let sub;
                        const middle = (fromBbox[MIDDLE[orientation][0]] + fromBbox[MIDDLE[orientation][1]]) / 2;

                        if (middle < toBbox[MIDDLE[orientation][0]]) {
                            sub = {
                                orientation: subOrientation,
                                length: toBbox[MIDDLE[orientation][0]] - middle,
                                top: subOrientation === 'vertical' ? middle : toBbox[oppositeSide],
                                left: subOrientation === 'horizontal' ? middle : toBbox[oppositeSide]
                            };
                        } else if (middle > toBbox[MIDDLE[orientation][1]]) {
                            sub = {
                                orientation: subOrientation,
                                length: middle - toBbox[MIDDLE[orientation][1]],
                                top: subOrientation === 'vertical' ? toBbox[MIDDLE[orientation][1]] : toBbox[oppositeSide],
                                left: subOrientation === 'horizontal' ? toBbox[MIDDLE[orientation][1]] : toBbox[oppositeSide]
                            };
                        }

                        info.push({
                            orientation: ORIENTATION[side],
                            length: Math.abs(fromBbox[side] - toBbox[oppositeSide]),
                            top: ORIENTATION[side] === 'vertical' ?
                                Math.min(fromBbox[side], toBbox[oppositeSide]) :
                                middle,
                            left: ORIENTATION[side] === 'horizontal' ?
                                Math.min(fromBbox[side], toBbox[oppositeSide]) :
                                middle,
                            sub
                        });

                        break;
                    }
                }
            }

            info.forEach(item => {
                item.top -= previewBbox.top;
                item.left -= previewBbox.left;
                if (item.sub) {
                    item.sub.top -= previewBbox.top;
                    item.sub.left -= previewBbox.left;
                }
            });

            highlightDistance = info;
        } else {
            highlightDistance = null;
        }
    };

    $: if (instance) {
        updateHighlight(
            $highlightElem,
            $selectedElem,
            componentClone,
            bestDragTarget,
            showInplaceEditor,
            $highlightMode,
            $highlightGradientAngle,
            isDistanceMode,
            isResize,
            gridResize
        );
    }

    function updateHighlightNoDeps(): void {
        updateHighlight(
            $highlightElem,
            $selectedElem,
            componentClone,
            bestDragTarget,
            showInplaceEditor,
            $highlightMode,
            $highlightGradientAngle,
            isDistanceMode,
            isResize,
            gridResize
        );
    }

    $: if (instance && viewport) {
        // wait till DOM update and then update sizes
        tick().then(updateHighlightNoDeps);
    }

    $: {
        if (resizeObserver) {
            resizeObserver.disconnect();
            resizeObserver = null;
        }
        const elems = [...new Set([...($highlightElem || []), $selectedElem].filter(Truthy))];
        if (elems.length) {
            const observer = resizeObserver = new ResizeObserver(updateHighlightNoDeps);
            elems.forEach(elem => {
                observer.observe(elem);
            });
        }
    }

    function updateHovered(): void {
        if (!prevCoords || showInplaceEditor) {
            highlightLeaf.set(null);
            highlightElem.set(null);
            highlightRanges.set(null);
            return;
        }

        const elems = new Set(components.keys());
        const x = prevCoords[0];
        const y = prevCoords[1];

        const MIN_HOVER_SIZE = 20;
        let best;
        for (const elem of elems) {
            const type = components.get(elem)?.processedJson?.type;
            if (!type || !elem.parentElement) {
                continue;
            }

            const bbox = getCroppedBbox(elem);
            if (bbox.width <= 0 || bbox.height <= 0) {
                continue;
            }

            let left = bbox.left;
            let right = bbox.right;
            let top = bbox.top;
            let bottom = bbox.bottom;

            if (bbox.width < MIN_HOVER_SIZE) {
                left -= (MIN_HOVER_SIZE - bbox.width) / 2;
                right += (MIN_HOVER_SIZE - bbox.width) / 2;
            }
            if (bbox.height < MIN_HOVER_SIZE) {
                top -= (MIN_HOVER_SIZE - bbox.height) / 2;
                bottom += (MIN_HOVER_SIZE - bbox.height) / 2;
            }

            if (x >= left && x <= right && y >= top && y <= bottom) {
                const depth = getDepth(elem);
                const order = Array.from(elem.parentElement.children).indexOf(elem);
                const space = bbox.width * bbox.height;

                if (
                    !best ||
                    best.space > space ||
                    (best.space === space && best.depth < depth) ||
                    (best.space === space && best.depth === depth && best.order < order)
                ) {
                    best = {
                        elem,
                        space,
                        depth,
                        order
                    };
                }
            }
        }

        if (best) {
            if (!($highlightElem?.length === 1 && $highlightElem[0] === best.elem)) {
                const leaf = state.getLeafFromNode(best.elem);
                if (leaf) {
                    highlightLeaf.set([leaf]);
                    highlightElem.set([best.elem]);
                    const range = leaf.props.range;
                    highlightRanges.set(range ? [range] : null);
                } else {
                    highlightLeaf.set(null);
                    highlightElem.set(null);
                    highlightRanges.set(null);
                }
            }
        } else {
            highlightLeaf.set(null);
            highlightElem.set(null);
            highlightRanges.set(null);
        }
    }

    function onPreviewScroll(): void {
        scrollX = previewWrapper.scrollLeft;
        scrollY = previewWrapper.scrollTop;

        updateHovered();
        updateHighlightNoDeps();
    }

    let prevCoords: [number, number] | null = null;
    function onHighlightOverlayMove(event: MouseEvent): void {
        prevCoords = [event.clientX, event.clientY];
        updateHovered();
    }

    function onHighlightOverlayOut(): void {
        highlightLeaf.set(null);
        highlightElem.set(null);
        highlightRanges.set(null);
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    function evalJson(json: Record<string, any>): Record<string, any> {
        if (!instance) {
            return {};
        }

        const variables = new Map(instance.getDebugVariables());
        for (const name in globalVariables) {
            if (!variables.has(name)) {
                variables.set(name, globalVariables[name]);
            }
        }

        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        const proc = (json: Record<string, any>) => {
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            const res: Record<string, any> = {};

            for (const key in json) {
                const val = json[key];
                if (typeof val === 'string' && val.includes('@{')) {
                    const evalRes = evalExpression(val, {
                        variables,
                        type: 'json'
                    });
                    if (evalRes.type !== 'error') {
                        res[key] = evalRes.value;
                    }
                } else if (val && typeof val === 'object') {
                    res[key] = proc(val);
                } else {
                    res[key] = val;
                }
            }

            return res;
        };

        return proc(json);
    }

    function onComponent({
        type,
        node,
        json,
        origJson,
        devapi
    }: {
        type: 'mount' | 'update' | 'destroy',
        node: HTMLElement | null;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        json?: any;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        origJson?: any;
        devapi?: object;
    }) {
        let shouldUpdate = type === 'mount' || type === 'destroy';
        if ((type === 'update' || type === 'mount') && origJson.__leafId) {
            mountedAndUpdatedLeafs.add(origJson.__leafId);
        }

        if ((type === 'mount' || type === 'update') && origJson.__leafId) {
            if (node && (json?.type === 'gif' || json?.type === 'image')) {
                const img = node.querySelector<HTMLImageElement>('img');

                if (img) {
                    const url = evalJson({ url: json.type === 'gif' ? json.gif_url : json.image_url }).url;
                    if (url === EMPTY_IMAGE) {
                        if (json.preview) {
                            img.src = DIVKIT_EMPY_IMAGE;
                        } else {
                            img.src = CHESS_EMPTY_IMAGE;
                        }
                    }
                }
            }

            const leaf = findLeaf($tree, origJson.__leafId);

            if (leaf) {
                leaf.props.processedJson = json;
                leaf.props.evalledJson = evalJson(json);

                // If a component was moved in the tree
                const isSelected = $selectedLeaf && $selectedLeaf.id === origJson.__leafId;

                if (node) {
                    leaf.props.node = node;
                    if (devapi) {
                        leaf.props.devapi = devapi;
                    }
                    components.set(node, {
                        json: origJson,
                        processedJson: json,
                        node
                    });

                    const prevLeafId = components.get(node)?.json.__leafId;
                    if (prevLeafId !== origJson.__leafId) {
                        shouldUpdate = true;
                    }

                    node.classList.add('renderer__divkit-node');
                    node.classList.toggle('renderer__divkit-gallery', json.type === 'gallery');
                    node.classList.toggle('renderer__divkit-pager', json.type === 'pager');
                    node.classList.toggle('renderer__divkit-tabs', json.type === 'tabs');
                    if (node.tagName === 'A' || node.tagName === 'BUTTON' || node.hasAttribute('tabindex')) {
                        // Disable focus
                        node.setAttribute('inert', '');
                    }

                    if (isSelected && $selectedElem !== node) {
                        selectedElem.set(node);
                    }
                }
                if (isSelected) {
                    // wait processedJson
                    $selectedLeaf = leaf;
                }
            } else {
                shouldUpdate = true;
            }
        } else if (type === 'destroy' && node) {
            components.delete(node);
        }

        if (shouldUpdate) {
            state.updateComponents();
        }
    }

    interface Box {
        top: number;
        left: number;
        width: number;
        height: number;
    }

    interface BestDragTarget {
        dist: number;
        depth: number;
        target: {
            index?: number;
            targetIndex?: number;
            visibleText?: string;
            top: number;
            left: number;
            width: number;
            height: number;
        };
        leaf: TreeLeaf;
        parent: Box;
        childs: Box[];
    }

    let bestDragTarget: BestDragTarget | null = null;

    function getBestDropTarget(x: number, y: number) {
        const elems = [...components.keys()];
        let best: BestDragTarget | null = null;
        const previewBbox = previewWrapper.getBoundingClientRect();

        for (const elem of elems) {
            const props = components.get(elem);
            if (!props) {
                continue;
            }
            const json = props.json;
            const processedJson = props.processedJson;
            const leaf = findLeaf($tree, json.__leafId);
            if (!leaf) {
                continue;
            }
            const depth = getLeafDepth(leaf);

            if (
                json.type in namedTemplates ||
                !(processedJson.type === 'container' || processedJson.type === 'gallery' || processedJson.type === 'grid') ||
                processedJson.orientation === 'overlap'
            ) {
                continue;
            }

            if (isUserTemplateWithoutChilds(state, json)) {
                continue;
            }

            const bbox = elem.getBoundingClientRect();

            if (
                x < bbox.left || x > bbox.right ||
                y < bbox.top || y > bbox.bottom
            ) {
                continue;
            }

            const orientation = processedJson.orientation || (processedJson.type === 'container' ? 'vertical' : 'horizontal');

            const parentComputed = getComputedStyle(elem);
            const paddingTop = parseFloat(parentComputed.paddingTop);
            const paddingRight = parseFloat(parentComputed.paddingRight);
            const paddingBottom = parseFloat(parentComputed.paddingBottom);
            const paddingLeft = parseFloat(parentComputed.paddingLeft);

            const targets: {
                index?: number;
                targetIndex?: number;
                visibleText?: string;
                top: number;
                left: number;
                width: number;
                height: number;
            }[] = [];
            const childBboxes: DOMRect[] = [];

            if (processedJson.type === 'container' || processedJson.type === 'gallery') {
                targets.push({
                    index: 0,
                    top: bbox.top + paddingTop,
                    left: bbox.left + paddingLeft,
                    width: orientation === 'horizontal' ? 0 : (bbox.width - paddingLeft - paddingRight),
                    height: orientation === 'horizontal' ? (bbox.height - paddingTop - paddingBottom) : 0
                });

                const children: HTMLElement[] = leaf.childs.map(child => {
                    return child.props.node;
                });

                Array.from(children).forEach((child, index) => {
                    const childBbox = child.getBoundingClientRect();
                    const computed = getComputedStyle(child);
                    const marginRight = parseFloat(computed.marginRight);
                    const marginBottom = parseFloat(computed.marginBottom);

                    // todo support gallery gap and container's separators

                    targets.push({
                        index: index + 1,
                        top: orientation === 'horizontal' ? (bbox.top + paddingTop) : (childBbox.bottom + marginBottom),
                        left: orientation === 'horizontal' ? (childBbox.right + marginRight) : (bbox.left + paddingLeft),
                        width: orientation === 'horizontal' ? 0 : (bbox.width - paddingLeft - paddingRight),
                        height: orientation === 'horizontal' ? (bbox.height - paddingTop - paddingBottom) : 0
                    });

                    childBboxes.push(childBbox);
                });
            } else if (processedJson.type === 'grid') {
                const children: HTMLElement[] = leaf.childs.map(child => {
                    return child.props.node;
                });

                children.forEach((child, index) => {
                    const childBbox = child.getBoundingClientRect();

                    const targetBox = {
                        top: childBbox.top,
                        left: childBbox.left,
                        width: childBbox.width,
                        height: childBbox.height
                    };

                    targets.push({
                        targetIndex: index,
                        visibleText: String(index + 1),
                        ...targetBox
                    });

                    childBboxes.push(childBbox);
                });
            }

            targets.forEach(target => {
                // eslint-disable-next-line no-nested-ternary
                const distX = x < target.left ?
                    target.left - x :
                    (x < target.left + target.width ? 0 : Math.abs(x - (target.left + target.width)));
                // eslint-disable-next-line no-nested-ternary
                const distY = y < target.top ?
                    target.top - y :
                    (y < target.top + target.height ? 0 : Math.abs(y - (target.top + target.height)));
                const dist = distX * distX + distY * distY;

                if (!best || (depth > best.depth) || (depth === best.depth && best.dist > dist)) {
                    target.left -= previewBbox.left;
                    target.top -= previewBbox.top;
                    best = {
                        dist,
                        depth,
                        target,
                        leaf,
                        parent: {
                            left: bbox.left - previewBbox.left,
                            top: bbox.top - previewBbox.top,
                            width: bbox.width,
                            height: bbox.height
                        },
                        childs: childBboxes.map(bbox => {
                            return {
                                left: bbox.left - previewBbox.left,
                                top: bbox.top - previewBbox.top,
                                width: bbox.width,
                                height: bbox.height
                            };
                        })
                    };
                }
            });
        }

        return best;
    }

    function onDragOver(event: DragEvent): void {
        if ($readOnly) {
            return;
        }

        event.preventDefault();
        bestDragTarget = !event.ctrlKey && getBestDropTarget(event.clientX, event.clientY) || null;
        if (event.dataTransfer) {
            event.dataTransfer.dropEffect = 'move';
        }
    }

    function onDragLeave(event: DragEvent): void {
        if (event.relatedTarget && event.relatedTarget instanceof Node && root.contains(event.relatedTarget)) {
            return;
        }
        bestDragTarget = null;
    }

    function onDrop(event: DragEvent): void {
        if ($readOnly) {
            return;
        }

        const movedChildId = event.dataTransfer?.getData('application/divnode');
        if (!movedChildId) {
            return;
        }

        const movedChild = state.getChild(movedChildId);
        if (!movedChild) {
            return;
        }

        let leafTarget;
        let insertIndex: number | null = null;
        let targetIndex: number | null = null;
        let changes;
        if (bestDragTarget) {
            const target = bestDragTarget;
            bestDragTarget = null;

            leafTarget = target.leaf;
            if (target.target.targetIndex !== undefined) {
                targetIndex = target.target.targetIndex;
            } else if (target.target.index !== undefined) {
                insertIndex = target.target.index;
            } else {
                return;
            }
            delete movedChild.props.json.margins;
        } else {
            leafTarget = $tree;
            insertIndex = $tree.childs.length;
            const pageX = event.pageX - window.scrollX;
            const pageY = event.pageY - window.scrollY;
            const previewBbox = previewWrapper.getBoundingClientRect();

            changes = [{
                property: 'alignment_horizontal',
                value: 'left'
            }, {
                property: 'alignment_vertical',
                value: 'top'
            }, {
                property: 'margins',
                value: {
                    top: Math.max(0, Math.round(pageY - previewBbox.top - 20)),
                    left: Math.max(0, Math.round(pageX - previewBbox.left - 20))
                }
            }];
        }

        if (!leafTarget) {
            return;
        }

        if (treeLeafContains(movedChild, leafTarget)) {
            return;
        }

        const deleteIndex = movedChild.parent ? movedChild.parent.childs.indexOf(movedChild) : -1;

        if (targetIndex !== null) {
            insertIndex = targetIndex;
        } else if (
            movedChild.parent &&
            movedChild.parent === leafTarget &&
            insertIndex !== null &&
            insertIndex > deleteIndex
        ) {
            --insertIndex;
        } else {
            insertIndex = leafTarget.childs.length;
        }

        if (movedChild.parent) {
            state.pushCommand(new MoveLeafCommand(state, {
                newParentId: leafTarget.id,
                insertIndex,
                leafId: movedChild.id,
                changes
            }));
        } else {
            if (changes) {
                changes.forEach(({ property, value }) => {
                    setObjectProperty(movedChild.props.json, property, value);
                });
            }

            state.pushCommand(new AddLeafCommand({
                parentId: leafTarget.id,
                insertIndex,
                leaf: movedChild
            }));
        }

        tick().then(() => {
            $selectedLeaf = findLeaf($tree, movedChild.id) || null;

            root.focus({
                preventScroll: true
            });
        });
    }

    function cleanInstance(): void {
        instance?.$destroy();
        instance = undefined;
    }

    $: if ($customVariables) {
        cleanInstance();
    }

    $: if ($timers) {
        cleanInstance();
    }

    $: if ($direction) {
        cleanInstance();
    }

    function rerender({
        divjson,
        tanker,
        locale,
        theme,
        direction,
        safeAreaEmulationEnabled
    }: {
        divjson: DivJson;
        tanker: TankerMeta;
        locale: string;
        theme: string;
        direction: 'ltr' | 'rtl';
        safeAreaEmulationEnabled: boolean;
    }): void {
        const updateTanker = () => {
            if (!globalVariablesController) {
                return;
            }

            const vars = instance?.getDebugAllVariables();

            for (const [variableName, tankerKey] of state.varToTankerMap) {
                const variableInstance = vars?.get(variableName);
                const value = tanker[tankerKey]?.[locale] || '';
                if (value) {
                    if (variableInstance) {
                        variableInstance.set(value);
                    } else {
                        const newVariable = createVariable(variableName, 'string', value);
                        globalVariables[variableName] = newVariable;
                        globalVariablesController.setVariable(newVariable);
                    }
                }
            }
        };

        mountedAndUpdatedLeafs = new Set();
        newRendererErrors = {};
        isUpdating = true;

        if (instance) {
            const vars = instance.getDebugAllVariables();
            const paletteVariable = vars.get('local_palette');
            if (paletteVariable) {
                const variableDesc = divjson.card.variables?.find((it: JsonVariable) => it.name === 'local_palette');
                paletteVariable.setValue(variableDesc?.value || {});
            }
            if (themeVariable) {
                themeVariable.set(theme);
            }

            if (safeAreaEmulation) {
                for (const side in safeAreaEmulation) {
                    const key = side as keyof typeof safeAreaEmulation;
                    const instance = vars.get(safeAreaEmulation[key].name);
                    if (instance) {
                        instance.setValue(safeAreaEmulationEnabled ? safeAreaEmulation[key].value : 0);
                    }
                }
            }

            instance.setData(divjson);
        } else {
            rendererErrors.set({
                '': templatesCheck(divjson)
            });

            globalVariablesController = createGlobalVariablesController();
            themeVariable = createVariable('theme', 'string', theme);
            globalVariables.theme = themeVariable;
            globalVariablesController.setVariable(themeVariable);

            if (actionLogUrlVariable) {
                const logVariable = createVariable(actionLogUrlVariable, 'string', 'https://');
                globalVariables.actionLogUrlVariable = logVariable;
                globalVariablesController.setVariable(logVariable);
            }

            const paletteFallback = createVariable('local_palette', 'dict', {});
            globalVariablesController.setVariable(paletteFallback);

            for (const source of $sources) {
                const sourceVariable = createVariable(source.key, 'dict', source.example);
                globalVariables[source.key] = sourceVariable;
                globalVariablesController.setVariable(sourceVariable);
            }

            const createdTankerVars = new Set<string>();
            for (const [variableName, tankerKey] of state.varToTankerMap) {
                const value = tanker[tankerKey]?.[locale] || '';
                if (value) {
                    const newVariable = createVariable(variableName, 'string', value);
                    globalVariables[variableName] = newVariable;
                    globalVariablesController.setVariable(newVariable);
                    createdTankerVars.add(variableName);
                }
            }

            if (createdTankerVars.size) {
                const card = divjson.card;
                if (card.variables) {
                    card.variables = card.variables.filter(it => !createdTankerVars.has(it.name));
                }
            }

            if (safeAreaEmulation) {
                for (const side in safeAreaEmulation) {
                    const key = side as keyof typeof safeAreaEmulation;
                    const instance = createVariable(
                        safeAreaEmulation[key].name,
                        'number',
                        safeAreaEmulationEnabled ? safeAreaEmulation[key].value : 0
                    );
                    globalVariablesController.setVariable(instance);
                }
            }

            instance = divkitRender({
                id: 'preview',
                target: rootPreview,
                json: divjson,
                globalVariablesController,
                onComponent,
                platform: 'desktop',
                extensions: new Map<string, DivExtensionClass>([
                    ['size_provider', SizeProvider],
                    ['lottie', Lottie],
                ]),
                direction,
                devtoolCreateHierarchy: 'eager',
                typefaceProvider(fontFamily) {
                    return customFontFaces.find(it => it.value === fontFamily)?.value || '';
                },
                onError(event) {
                    const additional = event.error.additional || {};
                    const leafId = (additional.fullpath as {
                        origJson: Record<string, unknown>;
                    }[])?.find(it => it.origJson?.__leafId)?.origJson?.__leafId as string || '';
                    const leaf = state.getChild(leafId);

                    const args = Object.keys(additional)
                        .filter(it => it !== 'json' && it !== 'origJson' && it !== 'fullpath')
                        .reduce((acc: Record<string, unknown>, item: string) => {
                            acc[item] = additional[item];
                            return acc;
                        }, {});

                    if (leafId) {
                        args.leafId = leafId;
                    }

                    const store = isUpdating ? newRendererErrors : get(rendererErrors);
                    let list = store[leafId] || [];
                    const isConnected = leaf?.props.node?.isConnected;

                    const errorObj: ViewerError = {
                        message: event.error.message,
                        stack: (event.error.stack || '').split('\n'),
                        level: event.error.level,
                        args
                    };
                    if (isConnected) {
                        list.push(errorObj);
                    } else if (event.error.level !== 'warn') {
                        list = [errorObj];
                    }
                    if (isUpdating) {
                        newRendererErrors[leafId] = list;
                    } else {
                        rendererErrors.set({
                            ...store,
                            [leafId]: list
                        });
                    }
                }
            });
        }

        tick().then(() => {
            updateTanker();
            updateHighlightNoDeps();

            tick().then(() => {
                isUpdating = false;

                let resultErrors = {
                    ...get(rendererErrors)
                };

                for (const leafId of mountedAndUpdatedLeafs) {
                    delete resultErrors[leafId];
                }

                resultErrors = {
                    ...resultErrors,
                    ...newRendererErrors
                };

                rendererErrors.set(resultErrors);
            });
        });
    }

    $: if ($divjsonStore.object.card?.states?.[0]?.div && rootPreview && $locale !== undefined) {
        rerender({
            divjson: $divjsonStore.object,
            tanker: $tanker,
            locale: $locale,
            theme,
            direction: $direction,
            safeAreaEmulationEnabled: $safeAreaEmulationEnabled
        });
    }

    let cancelCurrent = () => {};
    function cancelOperation(): void {
        cancelCurrent();
    }

    function onPointerDown(event: PointerEvent): void {
        if (event.button !== 0) {
            return;
        }

        if (
            event.target instanceof Element &&
            (
                event.target.matches('.renderer__divkit-gallery > div:not(:first-child) *') ||
                event.target.matches('.renderer__divkit-pager > div:not(:first-child) *') ||
                event.target.matches('.renderer__divkit-tabs > div:first-child > div:nth-child(3) > button')
            )
        ) {
            return;
        }

        if (componentClone) {
            return;
        }

        const node = $highlightElem?.[0];
        if (!node) {
            if ($selectedElem) {
                const bbox = $selectedElem.getBoundingClientRect();
                const x = event.clientX;
                const y = event.clientY;

                if (!(
                    x >= bbox.left && x <= bbox.right &&
                    y >= bbox.top && y <= bbox.bottom
                )) {
                    selectedLeaf.set(null);
                }
            }
            return;
        }
        const leaf = state.getLeafFromNode(node);
        const leafParent = leaf?.parent;
        if (!leaf) {
            return;
        }
        const json = leaf.props.json;
        const processedJson = leaf.props.processedJson;

        root.focus({
            preventScroll: true
        });
        selectedLeaf.set(leaf);
        selectedElem.set(node);
        highlightLeaf.set(null);
        highlightElem.set(null);
        highlightRanges.set(null);

        if (leaf === $tree || $readOnly || !leafParent) {
            return;
        }

        let isMoved = false;
        const rotation = processedJson.transform?.rotation || 0;
        const oldTransform = node.style.transform;
        node.style.transform = '';
        const startBbox = node.getBoundingClientRect();
        node.style.transform = oldTransform;
        const computedStyle = getComputedStyle(node);
        const containerBbox = clonesContainer.getBoundingClientRect();
        const startEventX = event.clientX;
        const startEventY = event.clientY;
        const offsetWidth = parseFloat(computedStyle.width);
        const offsetHeight = parseFloat(computedStyle.height);
        const startPosX = startBbox.left - containerBbox.left;
        const startPosY = startBbox.top - containerBbox.top;
        event.preventDefault();
        event.stopPropagation();

        const computed = getComputedStyle(node);
        componentClone = node.cloneNode(true) as HTMLElement;
        componentClone.classList.add('renderer__component-clone');
        componentClone.style.transform = `translate(${startPosX}px, ${startPosY}px) rotate(${rotation}deg)`;
        componentClone.style.width = `${Math.ceil(offsetWidth)}px`;
        componentClone.style.height = `${Math.ceil(offsetHeight)}px`;
        componentClone.style.fontSize = computed.fontSize;
        componentClone.style.opacity = '0';
        clonesContainer.appendChild(componentClone);
        componentCloneRect = {
            top: startPosY + containerBbox.top,
            left: startPosX + containerBbox.left,
            right: startPosX + offsetWidth + containerBbox.left,
            bottom: startPosY + offsetHeight + containerBbox.top,
            width: offsetWidth,
            height: offsetHeight
        };
        componentCloneRotation = rotation;

        const maxX = containerBbox.width - startBbox.width;
        const maxY = containerBbox.height - startBbox.height;

        let targetX = startPosX;
        let targetY = startPosY;

        let dropSpecialX: string | undefined;
        let dropSpecialY: string | undefined;

        let snapX: Snap[] = [{
            val: containerBbox.width / 2,
            type: 'center',
            special: 'horizontalCenter'
        }];
        let snapY: Snap[] = [{
            val: containerBbox.height / 2,
            type: 'center',
            special: 'verticalCenter'
        }];

        const simpleElements = [...(state.simpleComponentsMap?.keys() || [])];
        simpleElements.forEach(elem => {
            const bbox = elem.getBoundingClientRect();
            snapX.push({
                val: bbox.left - containerBbox.left,
                type: 'start',
            }, {
                val: bbox.right - containerBbox.left,
                type: 'end',
            }, {
                val: (bbox.left + bbox.right) / 2 - containerBbox.left,
                type: 'center'
            });
            snapY.push({
                val: bbox.top - containerBbox.top,
                type: 'start'
            }, {
                val: bbox.bottom - containerBbox.top,
                type: 'end'
            }, {
                val: (bbox.top + bbox.bottom) / 2 - containerBbox.top,
                type: 'center'
            });
        });

        snapX = snapX.map(it => ({ ...it, val: Math.round(it.val) }));
        snapY = snapY.map(it => ({ ...it, val: Math.round(it.val) }));
        snapX = snapX.sort((a, b) => a.val - b.val);
        snapY = snapY.sort((a, b) => a.val - b.val);

        bestDragTarget = null;

        const pointermove = (event: PointerEvent) => {
            const dist = (event.clientX - startEventX) * (event.clientX - startEventX) +
                (event.clientY - startEventY) * (event.clientY - startEventY);
            if (dist > 16) {
                isMoved = true;
            }
            if (!isMoved || !componentClone) {
                event.preventDefault();
                return;
            }

            const snapToAxis = event.shiftKey;

            bestDragTarget = !event.ctrlKey && getBestDropTarget(event.clientX, event.clientY) || null;

            node.style.opacity = '0';
            snapLineX = null;
            snapLineY = null;

            if (bestDragTarget) {
                componentClone.style.opacity = '0';
            } else {
                componentClone.style.opacity = '';

                let dx = event.clientX - startEventX;
                let dy = event.clientY - startEventY;

                if (snapToAxis) {
                    if (Math.abs(dx) > Math.abs(dy)) {
                        dy = 0;
                    } else {
                        dx = 0;
                    }
                }

                targetX = dx + startPosX;
                targetY = dy + startPosY;

                targetX = Math.max(0, Math.min(maxX, targetX));
                targetY = Math.max(0, Math.min(maxY, targetY));

                const attachmentsX: SnapOffset[] = [{
                    val: targetX + startBbox.width / 2,
                    pos: startBbox.width / 2,
                    type: 'center'
                }, {
                    val: targetX,
                    pos: 0,
                    type: 'start'
                }, {
                    val: targetX + startBbox.width,
                    pos: startBbox.width,
                    type: 'end'
                }];
                const attachmentsY: SnapOffset[] = [{
                    val: targetY + startBbox.height / 2,
                    pos: startBbox.height / 2,
                    type: 'center'
                }, {
                    val: targetY,
                    pos: 0,
                    type: 'start'
                }, {
                    val: targetY + startBbox.height,
                    pos: startBbox.height,
                    type: 'end'
                }];

                let snappedTargetX: Snap | undefined;
                let snapXOffset: number | undefined;
                let snappedTargetY: Snap | undefined;
                let snapYOffset: number | undefined;
                attachmentsX.forEach(item => {
                    if (!snappedTargetX) {
                        snappedTargetX = bestSnap(snapX, item, SNAP_DIST);
                        if (snappedTargetX) {
                            snapXOffset = item.pos;
                        }
                    }
                });
                attachmentsY.forEach(item => {
                    if (!snappedTargetY) {
                        snappedTargetY = bestSnap(snapY, item, SNAP_DIST);
                        if (snappedTargetY) {
                            snapYOffset = item.pos;
                        }
                    }
                });

                if (snappedTargetX) {
                    snapLineX = snappedTargetX.val;
                    dropSpecialX = snappedTargetX.special;
                } else {
                    snapLineX = null;
                    dropSpecialX = undefined;
                }
                if (snappedTargetY) {
                    snapLineY = snappedTargetY.val;
                    dropSpecialY = snappedTargetY.special;
                } else {
                    snapLineY = null;
                    dropSpecialY = undefined;
                }

                targetX = (snappedTargetX && snapXOffset !== undefined) && (snappedTargetX.val - snapXOffset) || targetX;
                targetY = (snappedTargetY && snapYOffset !== undefined) && (snappedTargetY.val - snapYOffset) || targetY;

                targetX = Math.max(0, Math.min(maxX, targetX));
                targetY = Math.max(0, Math.min(maxY, targetY));

                componentClone.style.transform = `translate(${targetX}px, ${targetY}px) rotate(${rotation}deg)`;
                componentCloneRect = {
                    ...componentCloneRect,
                    top: targetY + containerBbox.top,
                    left: targetX + containerBbox.left,
                    right: targetX + offsetWidth + containerBbox.left,
                    bottom: targetY + offsetHeight + containerBbox.top
                };
            }

            event.preventDefault();
        };
        cancelCurrent = () => {
            cancelCurrent = () => {};
            document.body.removeEventListener('pointermove', pointermove);
            document.body.removeEventListener('pointerup', pointerup);
            document.body.removeEventListener('pointercancel', pointerup);
            node.style.opacity = '';
            if (componentClone) {
                clonesContainer.removeChild(componentClone);
            }
            componentClone = null;
            bestDragTarget = null;
        };
        const pointerup = () => {
            const dragTarget = bestDragTarget;

            cancelCurrent();

            const moveX = targetX !== startPosX;
            const moveY = targetY !== startPosY;

            snapLineX = null;
            snapLineY = null;

            if (!dragTarget && !moveX && !moveY) {
                return;
            }

            if (dragTarget) {
                let leafTarget;
                let insertIndex: number | null = null;
                let targetIndex: number | null = null;
                const target = dragTarget;

                leafTarget = target.leaf;
                if (target.target.targetIndex !== undefined) {
                    targetIndex = target.target.targetIndex;
                } else if (target.target.index !== undefined) {
                    insertIndex = target.target.index;
                } else {
                    return;
                }

                const changes: {
                    property: string;
                    value: unknown;
                }[] = [];
                const newParentJson = leafTarget && findLeaf($tree, leafTarget.id)?.props.processedJson;
                if (
                    leaf.props.json.margins &&
                    newParentJson &&
                    newParentJson.type === 'container' &&
                    newParentJson.orientation === 'overlap'
                ) {
                    changes.push({
                        property: 'margins',
                        value: undefined
                    });
                }

                if (leafTarget && !treeLeafContains(leaf, leafTarget)) {
                    const deleteIndex = leafParent.childs.indexOf(leaf);

                    if (targetIndex !== null) {
                        insertIndex = targetIndex;
                    } else if (
                        leafParent === leafTarget &&
                        insertIndex !== null &&
                        insertIndex > deleteIndex
                    ) {
                        --insertIndex;
                    }

                    state.pushCommand(new MoveLeafCommand(state, {
                        leafId: leaf.id,
                        newParentId: leafTarget.id,
                        insertIndex: insertIndex !== null ? insertIndex : leafTarget.childs.length,
                        changes
                    }));
                }
            } else {
                const changes: {
                    property: string;
                    value: unknown;
                }[] = [];

                let hAlign = processedJson.alignment_horizontal || 'left';
                let vAlign = processedJson.alignment_vertical || 'top';
                const isMatchParentWidth = processedJson.width?.type === 'match_parent' || !processedJson.width;
                const isMatchParentHeight = processedJson.height?.type === 'match_parent';

                if (dropSpecialX === 'horizontalCenter') {
                    hAlign = 'center';
                    changes.push({
                        property: 'alignment_horizontal',
                        value: hAlign
                    });
                } else if (moveX && hAlign !== 'left' && hAlign !== 'right') {
                    hAlign = 'left';
                    changes.push({
                        property: 'alignment_horizontal',
                        value: hAlign
                    });
                }
                if (dropSpecialY === 'verticalCenter') {
                    vAlign = 'center';
                    changes.push({
                        property: 'alignment_vertical',
                        value: vAlign
                    });
                } else if (moveY && vAlign !== 'top' && vAlign !== 'bottom') {
                    vAlign = 'top';
                    changes.push({
                        property: 'alignment_vertical',
                        value: vAlign
                    });
                }

                const oldMargins = json.margins || {};
                const margins: Record<string, number> = {};
                if (moveX) {
                    if (hAlign === 'left' || isMatchParentWidth) {
                        margins.left = Math.round(targetX);
                    }
                    if (hAlign === 'right' || isMatchParentWidth) {
                        margins.right = Math.floor(containerBbox.width - targetX - startBbox.width);
                    }
                } else {
                    margins.left = oldMargins.left;
                    margins.right = oldMargins.right;
                }
                if (moveY) {
                    if (vAlign === 'top' || isMatchParentHeight) {
                        margins.top = Math.round(targetY);
                    }
                    if (vAlign === 'bottom' || isMatchParentHeight) {
                        margins.bottom = Math.floor(containerBbox.height - targetY - startBbox.height);
                    }
                } else {
                    margins.top = oldMargins.top;
                    margins.bottom = oldMargins.bottom;
                }
                changes.push({
                    property: 'margins',
                    value: margins
                });

                state.pushCommand(new MoveLeafCommand(state, {
                    leafId: leaf.id,
                    newParentId: $tree.id,
                    insertIndex: $tree.childs.length,
                    changes
                }));
            }

            $tree = $tree;
        };

        document.body.addEventListener('pointermove', pointermove);
        document.body.addEventListener('pointerup', pointerup);
        document.body.addEventListener('pointercancel', pointerup);
    }

    function onCanvasResize(event: PointerEvent, type: 'right' | 'down' | 'down-right'): void {
        if (event.button !== 0) {
            return;
        }

        const isXResize = type === 'right' || type === 'down-right';
        const isYResize = type === 'down' || type === 'down-right';

        const startEventX = event.clientX;
        const startEventY = event.clientY;

        const startWidth = size[0];
        const startHeight = size[1];

        const pointermove = (event: PointerEvent) => {
            let newWidth = startWidth;
            let newHeight = startHeight;

            if (isXResize) {
                newWidth += (event.clientX - startEventX) * 2;
                newWidth = Math.round(Math.max(100, newWidth));
            }
            if (isYResize) {
                newHeight += event.clientY - startEventY;
                newHeight = Math.round(Math.max(100, newHeight));
            }

            viewport = `${newWidth}x${newHeight}`;

            event.preventDefault();
        };

        const pointerup = () => {
            document.body.removeEventListener('pointermove', pointermove);
            document.body.removeEventListener('pointerup', pointerup);
            document.body.removeEventListener('pointercancel', pointerup);
        };

        document.body.addEventListener('pointermove', pointermove);
        document.body.addEventListener('pointerup', pointerup);
        document.body.addEventListener('pointercancel', pointerup);
    }

    function onResize(event: PointerEvent, type: 'right' | 'down' | 'down-right'): void {
        if ($readOnly) {
            return;
        }

        if (event.button !== 0) {
            return;
        }

        if (componentClone) {
            return;
        }

        const node = $selectedElem;
        if (!node) {
            return;
        }
        const leaf = state.getLeafFromNode(node) || null;
        if (!leaf) {
            return;
        }

        const parentLeaf = leaf.parent;

        isResize = true;

        const processedJson = leaf.props.processedJson;
        const computedStyle = getComputedStyle(node);

        const isGridChild = parentLeaf?.props.processedJson?.type === 'grid';
        const gridIsMatchParentWidth = isGridChild &&
            (!parentLeaf.props.processedJson.width || parentLeaf.props.processedJson.width.type === 'match_parent');
        const gridIsMatchParentHeight = isGridChild && parentLeaf.props.processedJson.height.type === 'match_parent';
        let gridProps: GridProps | undefined;
        if (isGridChild && parentLeaf?.props.node) {
            withoutRotation(parentLeaf.props.node, () => {
                gridProps = getGridProps(parentLeaf.props.node);
            });
        }
        const gridPosition = isGridChild && getGridPosition(node);
        const gridMaxColumnSpan = (gridProps && gridPosition) ? gridProps.columnsCount - gridPosition.columnStart : 0;
        const gridMaxRowSpan = gridProps ? /* gridProps.rowsCount - gridPosition.rowStart */ Infinity : 0;
        const gridMaxItemWidth = (gridProps && gridPosition) ?
            gridProps.columns[gridPosition.columnStart] -
                parseFloat(computedStyle.marginLeft) -
                parseFloat(computedStyle.marginRight) :
            0;
        const gridMaxItemHeight = (gridProps && gridPosition) ?
            gridProps.rows[gridPosition.rowStart] -
                parseFloat(computedStyle.marginTop) -
                parseFloat(computedStyle.marginBottom) :
            0;
        let columnSpan = gridPosition ? gridPosition.columnSpan : 1;
        let rowSpan = gridPosition ? gridPosition.rowSpan : 1;
        let gridReachMaxWidth = false;
        let gridReachMaxHeight = false;

        if (isGridChild) {
            componentCloneText = `${columnSpan}x${rowSpan}`;
        }

        const isXResize = type === 'right' || type === 'down-right';
        const isYResize = type === 'down' || type === 'down-right';

        const rotation = processedJson.transform?.rotation || 0;
        const oldTransform = node.style.transform;
        node.style.transform = '';
        const startBbox = node.getBoundingClientRect();
        node.style.transform = oldTransform;
        const containerBbox = clonesContainer.getBoundingClientRect();
        const parentBbox = parentLeaf?.props.node.getBoundingClientRect();
        const startEventX = event.clientX;
        const startEventY = event.clientY;
        const offsetWidth = parseFloat(computedStyle.width);
        const offsetHeight = parseFloat(computedStyle.height);
        const startPosX = startBbox.left - containerBbox.left;
        const startPosY = startBbox.top - containerBbox.top;
        event.preventDefault();
        event.stopPropagation();

        const computed = getComputedStyle(node);
        componentClone = node.cloneNode(true) as HTMLElement;
        componentClone.classList.add('renderer__component-clone');
        componentClone.style.transform = `translate(${startPosX}px, ${startPosY}px) rotate(${rotation || 0}deg)`;
        componentClone.style.width = `${Math.ceil(offsetWidth)}px`;
        componentClone.style.height = `${Math.ceil(offsetHeight)}px`;
        componentClone.style.fontSize = computed.fontSize;
        clonesContainer.appendChild(componentClone);
        node.style.opacity = '0';
        componentCloneRect = {
            top: startPosY + containerBbox.top,
            left: startPosX + containerBbox.left,
            right: startPosX + offsetWidth + containerBbox.left,
            bottom: startPosY + offsetHeight + containerBbox.top,
            width: offsetWidth,
            height: offsetHeight
        };
        componentCloneRotation = rotation || 0;

        const rotationRad = componentCloneRotation / 180 * Math.PI;
        const rotationSin = Math.sin(rotationRad);
        const rotationCos = Math.cos(rotationRad);

        const center = {
            x: startPosX + offsetWidth / 2,
            y: startPosY + offsetHeight / 2
        };

        const topLeftCorner = {
            x: center.x + (- offsetWidth / 2) * rotationCos - (- offsetHeight / 2) * rotationSin,
            y: center.y + (- offsetWidth / 2) * rotationSin + (- offsetHeight / 2) * rotationCos,
        };

        const maxX = parentBbox ?
            parentBbox.right - containerBbox.left - startPosX :
            size[0] - startPosX;
        // eslint-disable-next-line no-nested-ternary
        const maxY = isGridChild ?
            Infinity :
            (
                parentBbox ?
                    parentBbox.bottom - containerBbox.top - startPosY :
                    size[1] - startPosY
            );

        let targetX = startPosX + startBbox.width;
        let targetY = startPosY + startBbox.height;
        let targetWidth = startBbox.width;
        let targetHeight = startBbox.height;

        let snapX: Snap[] = [];
        let snapY: Snap[] = [];

        let shouldExpandX = false;
        let shouldExpandY = false;

        const simpleElements = [...(state.simpleComponentsMap?.keys() || [])];
        simpleElements.forEach(elem => {
            const bbox = elem.getBoundingClientRect();
            snapX.push({
                val: bbox.right - containerBbox.left,
                type: 'end',
            });
            snapY.push({
                val: bbox.bottom - containerBbox.top,
                type: 'end'
            });
        });

        snapX = snapX.map(it => ({ ...it, val: Math.round(it.val) }));
        snapY = snapY.map(it => ({ ...it, val: Math.round(it.val) }));
        snapX = snapX.sort((a, b) => a.val - b.val);
        snapY = snapY.sort((a, b) => a.val - b.val);

        // Position diff changes from the rotation
        let diffX = 0;
        let diffY = 0;

        const pointermove = (event: PointerEvent) => {
            if (!componentClone) {
                return;
            }

            const snapToAxis = event.shiftKey;
            let dx = event.clientX - startEventX;
            let dy = event.clientY - startEventY;

            if (snapToAxis && isXResize && isYResize) {
                const middle = (dx + dy) / 2;
                dx = middle;
                dy = middle;
            }

            const d = Math.sqrt(dx * dx + dy * dy);
            let angle = Math.atan2(dy, dx);
            angle -= rotationRad;
            const rotatedDx = d * Math.cos(angle);
            const rotatedDy = d * Math.sin(angle);
            targetX = rotatedDx + startBbox.width;
            targetY = rotatedDy + startBbox.height;

            targetX = Math.max(0, Math.min(maxX, targetX));
            targetY = Math.max(0, Math.min(maxY, targetY));

            shouldExpandX = targetX === maxX;
            shouldExpandY = targetY === maxY;

            if (snapToAxis && isXResize && isYResize && targetX !== targetY) {
                targetX = targetY = Math.min(targetX, targetY);
            }

            targetX += startPosX;
            targetY += startPosY;

            const attachmentsX: SnapOffset[] = [{
                val: targetX,
                pos: 0,
                type: 'end'
            }];
            const attachmentsY: SnapOffset[] = [{
                val: targetY,
                pos: 0,
                type: 'end'
            }];

            let snappedTargetX: Snap | undefined;
            let snapXOffset: number | undefined;
            let snappedTargetY: Snap | undefined;
            let snapYOffset: number | undefined;
            if (isXResize) {
                attachmentsX.forEach(item => {
                    if (!snappedTargetX) {
                        snappedTargetX = bestSnap(snapX, item, SNAP_DIST);
                        if (snappedTargetX) {
                            snapXOffset = item.pos;
                        }
                    }
                });
            }
            if (isYResize) {
                attachmentsY.forEach(item => {
                    if (!snappedTargetY) {
                        snappedTargetY = bestSnap(snapY, item, SNAP_DIST);
                        if (snappedTargetY) {
                            snapYOffset = item.pos;
                        }
                    }
                });
            }

            if (snappedTargetX) {
                snapLineX = snappedTargetX.val;
            } else {
                snapLineX = null;
            }
            if (snappedTargetY) {
                snapLineY = snappedTargetY.val;
            } else {
                snapLineY = null;
            }

            const calcedWidth = (snappedTargetX && snapXOffset !== undefined && (snappedTargetX.val - snapXOffset) || targetX) - startPosX;
            const calcedHeight = (snappedTargetY && snapYOffset !== undefined && (snappedTargetY.val - snapYOffset) || targetY) - startPosY;
            if (isXResize) {
                if (!gridProps || !gridPosition || calcedWidth <= gridMaxItemWidth) {
                    targetWidth = calcedWidth;
                    targetWidth = Math.max(0, Math.min(maxX, targetWidth));
                    gridReachMaxWidth = false;
                } else {
                    let sum = 0;
                    columnSpan = 0;
                    for (columnSpan = 0; columnSpan < gridMaxColumnSpan; ++columnSpan) {
                        const currentColumnWidth = gridProps.columns[gridPosition.columnStart + columnSpan];
                        if (columnSpan >= 1 && calcedWidth < sum + currentColumnWidth / 3) {
                            break;
                        }
                        sum += currentColumnWidth;
                    }

                    targetWidth = gridProps.columns.slice(
                        gridPosition.columnStart,
                        gridPosition.columnStart + columnSpan
                    ).reduce((acc, item) => {
                        return acc + item;
                    }, 0) - parseFloat(computedStyle.marginLeft) - parseFloat(computedStyle.marginRight);
                    gridReachMaxWidth = true;
                }
            }
            if (isYResize) {
                if (!gridProps || !gridPosition || calcedHeight <= gridMaxItemHeight) {
                    targetHeight = calcedHeight;
                    targetHeight = Math.max(0, Math.min(maxY, targetHeight));
                    gridReachMaxHeight = false;
                } else {
                    let sum = 0;
                    rowSpan = 0;
                    const totalHeight = gridProps.rows.reduce((acc, item) => {
                        return acc + item;
                    }, 0);
                    const getRowHeight = (index: number): number => {
                        const props = gridProps as GridProps;
                        if (index < props.rows.length) {
                            return props.rows[index];
                        }

                        return totalHeight / props.rows.length;
                    };
                    for (rowSpan = 0; rowSpan < gridMaxRowSpan; ++rowSpan) {
                        const currentColumnHeight = getRowHeight(gridPosition.rowStart + rowSpan);
                        if (rowSpan >= 1 && calcedHeight < sum + currentColumnHeight / 3) {
                            break;
                        }
                        sum += currentColumnHeight;
                    }

                    targetHeight = 0;
                    for (let i = gridPosition.rowStart; i < gridPosition.rowStart + rowSpan; ++i) {
                        targetHeight += getRowHeight(i);
                    }
                    targetHeight = targetHeight -
                        parseFloat(computedStyle.marginTop) -
                        parseFloat(computedStyle.marginBottom);

                    gridReachMaxHeight = true;
                }
            }

            if (isGridChild) {
                if (columnSpan > 1 || rowSpan > 1) {
                    targetWidth = Math.max(targetWidth, gridMaxItemWidth);
                    targetHeight = Math.max(targetHeight, gridMaxItemHeight);
                }
                componentCloneText = `${columnSpan}x${rowSpan}`;
            }
            componentClone.style.width = `${targetWidth}px`;
            componentClone.style.height = `${targetHeight}px`;

            if (componentCloneRotation && !isGridChild) {
                const center = {
                    x: startPosX + targetWidth / 2,
                    y: startPosY + targetHeight / 2
                };

                const topLeftCorner2 = {
                    x: center.x + (- targetWidth / 2) * rotationCos - (- targetHeight / 2) * rotationSin,
                    y: center.y + (- targetWidth / 2) * rotationSin + (- targetHeight / 2) * rotationCos,
                };

                diffX = topLeftCorner.x - topLeftCorner2.x;
                diffY = topLeftCorner.y - topLeftCorner2.y;

                componentClone.style.transform = `translate(${startPosX + diffX}px, ${startPosY + diffY}px) rotate(${componentCloneRotation}deg)`;

                componentCloneRect = {
                    top: startPosY + diffY + containerBbox.top,
                    left: startPosX + diffX + containerBbox.left,
                    right: startPosX + diffX + targetWidth + containerBbox.left,
                    bottom: startPosY + diffY + targetHeight + containerBbox.top,
                    width: targetWidth,
                    height: targetHeight
                };
            } else {
                componentCloneRect = {
                    ...componentCloneRect,
                    right: startPosX + targetWidth + containerBbox.left,
                    bottom: startPosY + targetHeight + containerBbox.top,
                    width: targetWidth,
                    height: targetHeight
                };
            }

            event.preventDefault();
        };
        cancelCurrent = () => {
            cancelCurrent = () => {};
            document.body.removeEventListener('pointermove', pointermove);
            document.body.removeEventListener('pointerup', pointerup);
            document.body.removeEventListener('pointercancel', pointerup);
            node.style.opacity = '';
            if (componentClone) {
                clonesContainer.removeChild(componentClone);
            }
            componentClone = null;
            componentCloneText = '';
            bestDragTarget = null;
            isResize = false;
        };
        const pointerup = () => {
            cancelCurrent();

            snapLineX = null;
            snapLineY = null;

            const changes: SetPropertyItem[] = [];

            const leafId = $selectedLeaf?.id;
            if (!leafId) {
                return;
            }

            if (isGridChild) {
                if (isXResize) {
                    if (gridReachMaxWidth && gridIsMatchParentWidth) {
                        changes.push({
                            leafId,
                            property: 'width',
                            value: {
                                type: 'match_parent',
                                weight: gridReachMaxWidth ? 1 : undefined
                            }
                        });
                    } else {
                        changes.push({
                            leafId,
                            property: 'width',
                            value: {
                                type: 'fixed',
                                value: Math.ceil(targetWidth)
                            }
                        });
                    }

                    if (gridReachMaxWidth && gridPosition && columnSpan !== gridPosition.columnSpan) {
                        changes.push({
                            leafId,
                            property: 'column_span',
                            value: columnSpan > 1 ? columnSpan : undefined
                        });
                    }
                }

                if (isYResize) {
                    if (gridReachMaxHeight && gridIsMatchParentHeight) {
                        changes.push({
                            leafId,
                            property: 'height',
                            value: {
                                type: 'match_parent',
                                weight: gridReachMaxHeight ? 1 : undefined
                            }
                        });
                    } else {
                        changes.push({
                            leafId,
                            property: 'height',
                            value: {
                                type: 'fixed',
                                value: Math.ceil(targetHeight)
                            }
                        });
                    }

                    if (gridReachMaxHeight && gridPosition && rowSpan !== gridPosition.rowSpan) {
                        changes.push({
                            leafId,
                            property: 'row_span',
                            value: rowSpan > 1 ? rowSpan : undefined
                        });
                    }
                }
            } else {
                if (isXResize) {
                    if (shouldExpandX) {
                        changes.push({
                            leafId,
                            property: 'width',
                            value: {
                                type: 'match_parent'
                            }
                        });
                    } else {
                        changes.push({
                            leafId,
                            property: 'width',
                            value: {
                                type: 'fixed',
                                value: Math.ceil(targetWidth)
                            }
                        });
                    }
                }
                if (isYResize) {
                    if (shouldExpandY) {
                        changes.push({
                            leafId,
                            property: 'height',
                            value: {
                                type: 'match_parent'
                            }
                        });
                    } else {
                        changes.push({
                            leafId,
                            property: 'height',
                            value: {
                                type: 'fixed',
                                value: Math.ceil(targetHeight)
                            }
                        });
                    }
                }
            }

            if (changes.length) {
                state.pushCommand(new SetPropertyCommand($tree, changes));
            }
        };

        document.body.addEventListener('pointermove', pointermove);
        document.body.addEventListener('pointerup', pointerup);
        document.body.addEventListener('pointercancel', pointerup);
    }

    function onGridResize(event: PointerEvent, type: 'column' | 'row', index: number): void {
        if ($readOnly) {
            return;
        }

        if (event.button !== 0) {
            return;
        }

        if (componentClone) {
            return;
        }

        const node = $selectedElem;
        if (!node) {
            return;
        }
        const leaf = state.getLeafFromNode(node) || null;
        if (!leaf) {
            return;
        }

        const isGrid = leaf?.props.processedJson?.type === 'grid';
        if (!isGrid) {
            return;
        }

        let props: GridProps | undefined;
        withoutRotation(node, () => {
            props = getGridProps(node);
        });
        if (!props) {
            return;
        }
        const gridProps: GridProps = props;
        const startEventX = event.clientX;
        const startEventY = event.clientY;
        const sizeProp = type === 'column' ? 'columns' : 'rows';

        let size = props[sizeProp][index];
        const startSize = size;
        const minSize = 0;
        const maxSize = size + props[sizeProp][index + 1];

        const pointermove = (event: PointerEvent) => {
            const dx = event.clientX - startEventX;
            const dy = event.clientY - startEventY;

            let targetSize;
            if (type === 'column') {
                targetSize = startSize + dx;
            } else {
                targetSize = startSize + dy;
            }
            size = Math.max(minSize, Math.min(maxSize, targetSize));

            gridResize = {
                columns: gridProps.columns.slice(0, gridProps.columns.length - 1),
                rows: gridProps.rows.slice(0, gridProps.rows.length - 1)
            };

            gridResize[sizeProp][index] = size;
            if (index + 1 < gridResize[sizeProp].length) {
                gridResize[sizeProp][index + 1] = maxSize - size;
            }

            gridResize.columns = sizesToFall(gridResize.columns);
            gridResize.rows = sizesToFall(gridResize.rows);

            event.preventDefault();
        };
        cancelCurrent = () => {
            cancelCurrent = () => {};
            document.body.removeEventListener('pointermove', pointermove);
            document.body.removeEventListener('pointerup', pointerup);
            document.body.removeEventListener('pointercancel', pointerup);
            gridResize = null;
        };
        const pointerup = () => {
            cancelCurrent();

            const changes: SetPropertyItem[] = [];

            const gridStartProp = type === 'column' ? 'columnStart' : 'rowStart';
            const gridSpanProp = type === 'column' ? 'columnSpan' : 'rowSpan';
            const sizeProp = type === 'column' ? 'width' : 'height';
            const prevSizes = gridProps[type === 'column' ? 'columns' : 'rows'].slice();
            const sizes = prevSizes.slice();

            sizes[index] = size;
            sizes[index + 1] = maxSize - size;

            leaf.childs.forEach(child => {
                const leafId = child.props.processedJson?.__leafId;
                if (!child.props.node || !leafId) {
                    return;
                }

                const gridProps = getGridPosition(child.props.node);
                if (
                    index + 1 === gridProps[gridStartProp] ||
                    index + 1 === gridProps[gridStartProp] + gridProps[gridSpanProp]
                ) {
                    const prevCellSize = sum(
                        prevSizes.slice(
                            gridProps[gridStartProp],
                            gridProps[gridStartProp] + gridProps[gridSpanProp]
                        )
                    );
                    let childSize = sum(
                        sizes.slice(
                            gridProps[gridStartProp],
                            gridProps[gridStartProp] + gridProps[gridSpanProp]
                        )
                    );
                    const margins = child.props.processedJson.margins;
                    if (child.props.processedJson[sizeProp]?.type === 'fixed') {
                        const curSize = child.props.processedJson[sizeProp].value;
                        childSize -= (prevCellSize - curSize);
                    } else if (type === 'column') {
                        childSize -= Number(margins?.left || 0) + Number(margins?.right || 0);
                    } else {
                        childSize -= Number(margins?.top || 0) + Number(margins?.bottom || 0);
                    }
                    if (child.props.processedJson[sizeProp]?.type === 'fixed' && Math.abs(childSize - child.props.processedJson[sizeProp].value) < 1) {
                        return;
                    }
                    changes.push({
                        leafId,
                        property: sizeProp,
                        value: {
                            type: 'fixed',
                            value: childSize
                        }
                    });
                }
            });

            if (changes.length) {
                state.pushCommand(new SetPropertyCommand($tree, changes));
            }
        };

        document.body.addEventListener('pointermove', pointermove);
        document.body.addEventListener('pointerup', pointerup);
        document.body.addEventListener('pointercancel', pointerup);
    }

    function onRotate(event: PointerEvent): void {
        if ($readOnly) {
            return;
        }

        if (event.button !== 0) {
            return;
        }

        if (componentClone) {
            return;
        }

        const node = $selectedElem;
        if (!node) {
            return;
        }
        const leaf = state.getLeafFromNode(node) || null;
        if (!leaf) {
            return;
        }
        const processedJson = leaf.props.processedJson;

        const rotation = processedJson.transform?.rotation || 0;
        const oldTransform = node.style.transform;
        node.style.transform = '';
        const startBbox = node.getBoundingClientRect();
        node.style.transform = oldTransform;
        const computedStyle = getComputedStyle(node);
        const containerBbox = clonesContainer.getBoundingClientRect();
        const offsetWidth = parseFloat(computedStyle.width);
        const offsetHeight = parseFloat(computedStyle.height);
        const startPosX = startBbox.left - containerBbox.left;
        const startPosY = startBbox.top - containerBbox.top;
        event.preventDefault();
        event.stopPropagation();

        const computed = getComputedStyle(node);
        componentClone = node.cloneNode(true) as HTMLElement;
        componentClone.classList.add('renderer__component-clone');
        componentClone.style.transform = `translate(${startPosX}px, ${startPosY}px) rotate(${rotation || 0}deg)`;
        componentClone.style.width = `${Math.ceil(offsetWidth)}px`;
        componentClone.style.height = `${Math.ceil(offsetHeight)}px`;
        componentClone.style.fontSize = computed.fontSize;
        clonesContainer.appendChild(componentClone);
        node.style.opacity = '0';
        componentCloneRect = {
            top: startPosY + containerBbox.top,
            left: startPosX + containerBbox.left,
            right: startPosX + offsetWidth + containerBbox.left,
            bottom: startPosY + offsetHeight + containerBbox.top,
            width: offsetWidth,
            height: offsetHeight
        };
        componentCloneRotation = rotation || 0;
        if (componentCloneRotation < 0) {
            componentCloneRotation += 360;
        }
        isRotate = true;

        const center = {
            x: startBbox.left + offsetWidth / 2,
            y: startBbox.top + offsetHeight / 2
        };

        const pointermove = (event: PointerEvent) => {
            if (!componentClone) {
                return;
            }

            const snapToAxis = event.shiftKey;
            const dx = event.clientX - center.x;
            const dy = event.clientY - center.y;
            let angle = Math.atan2(dy, dx);

            if (snapToAxis) {
                const fraction = Math.PI / 4;
                angle = Math.round(angle / fraction) * fraction;
            }

            componentCloneRotation = angle / Math.PI * 180 + 90;
            if (componentCloneRotation < 0) {
                componentCloneRotation += 360;
            }
            componentClone.style.transform = `translate(${startPosX}px, ${startPosY}px) rotate(${componentCloneRotation}deg)`;

            event.preventDefault();
        };
        cancelCurrent = () => {
            cancelCurrent = () => {};
            document.body.removeEventListener('pointermove', pointermove);
            document.body.removeEventListener('pointerup', pointerup);
            document.body.removeEventListener('pointercancel', pointerup);
            node.style.opacity = '';
            if (componentClone) {
                clonesContainer.removeChild(componentClone);
            }
            componentClone = null;
            bestDragTarget = null;
            isRotate = false;
        };
        const pointerup = () => {
            cancelCurrent();

            const leafId = $selectedLeaf?.id;
            if (!leafId) {
                return;
            }

            let rotation = Math.round(componentCloneRotation);
            if (rotation < 0) {
                rotation += 360;
            }

            state.pushCommand(new SetPropertyCommand($tree, [{
                leafId,
                property: 'transform',
                value: {
                    rotation
                }
            }]));
        };

        document.body.addEventListener('pointermove', pointermove);
        document.body.addEventListener('pointerup', pointerup);
        document.body.addEventListener('pointercancel', pointerup);
    }

    function moveComponentOffset(leaf: TreeLeaf, offset: {
        x: number;
        y: number;
    }): void {
        const json = leaf.props.json;
        const processedJson = leaf.props.processedJson;
        let hAlign = processedJson.alignment_horizontal || 'left';
        let vAlign = processedJson.alignment_vertical || 'top';

        const previewBbox = previewWrapper.getBoundingClientRect();
        const node = leaf.props.node;
        const bbox = node.getBoundingClientRect();

        const newMargins = { ...(json.margins || {}) };

        const changes: SetPropertyItem[] = [];

        if (hAlign === 'center') {
            hAlign = 'left';
            changes.push({
                leafId: leaf.id,
                property: 'alignment_horizontal',
                value: hAlign
            });
            delete newMargins.right;
            newMargins.left = Math.ceil(bbox.left - previewBbox.left);
        }
        if (vAlign === 'center') {
            vAlign = 'top';
            changes.push({
                leafId: leaf.id,
                property: 'alignment_vertical',
                value: vAlign
            });
            delete newMargins.bottom;
            newMargins.top = Math.ceil(bbox.top - previewBbox.top);
        }

        if (hAlign === 'left') {
            newMargins.left ||= 0;
            newMargins.left += offset.x;
            if (newMargins.right !== undefined) {
                newMargins.right -= offset.x;
            }
        } else {
            newMargins.right ||= 0;
            newMargins.right -= offset.x;
            if (newMargins.left !== undefined) {
                newMargins.left += offset.x;
            }
        }
        if (vAlign === 'top') {
            newMargins.top ||= 0;
            newMargins.top += offset.y;
            if (newMargins.bottom !== undefined) {
                newMargins.bottom -= offset.y;
            }
        } else {
            newMargins.bottom ||= 0;
            newMargins.bottom -= offset.y;
            if (newMargins.top !== undefined) {
                newMargins.top += offset.y;
            }
        }

        const max = {
            left: previewBbox.width - bbox.width,
            right: previewBbox.width - bbox.width,
            top: previewBbox.height - bbox.height,
            bottom: previewBbox.height - bbox.height
        };
        for (const side of ['left', 'right', 'top', 'bottom'] as const) {
            if (newMargins[side]) {
                newMargins[side] = Math.max(0, Math.min(max[side], newMargins[side]));
            }
        }

        if (changes || !isEqual(json.margins, newMargins)) {
            changes.push({
                leafId: leaf.id,
                property: 'margins',
                value: newMargins
            });
            state.pushCommand(new SetPropertyCommand($tree, changes));
        }
    }

    function moveComponentOrder(leaf: TreeLeaf, offset: {
        x: number;
        y: number;
    }): void {
        const parent = leaf.parent;
        if (!parent) {
            return;
        }

        const parentType = parent.props.processedJson.type;
        // todo other types
        const parentOrientation = parent.props.processedJson.orientation ||
            (parentType === 'container' ? 'vertical' : 'horizontal');

        const index = parent.childs.indexOf(leaf);
        let newIndex;

        if (parentOrientation === 'horizontal' && offset.x !== 0) {
            newIndex = index + offset.x;
        } else if (parentOrientation === 'vertical' && offset.y !== 0) {
            newIndex = index + offset.y;
        }

        if (newIndex !== undefined && newIndex >= 0 && newIndex <= parent.childs.length) {
            state.pushCommand(new MoveLeafCommand(state, {
                leafId: leaf.id,
                newParentId: parent.id,
                insertIndex: newIndex
            }));
        }
    }

    function resizeComponent(leaf: TreeLeaf, resize: {
        x: number;
        y: number;
    }): void {
        const parentLeaf = leaf.parent;
        if (!parentLeaf) {
            return;
        }

        const processedJson = leaf.props.processedJson;
        const node = leaf.props.node;
        const bbox = node.getBoundingClientRect();
        const parentBbox = parentLeaf.props.node.getBoundingClientRect();

        const prop = resize.x ? 'width' : 'height';

        let sizeType = processedJson[prop]?.type;
        if (!sizeType) {
            sizeType = prop === 'width' ? 'match_parent' : 'wrap_content';
        }

        const maxSize = parentBbox[prop === 'width' ? 'right' : 'bottom'] -
            bbox[prop === 'width' ? 'left' : 'top'];
        const currentSize = bbox[prop];
        const newSize = Math.max(0, Math.min(maxSize, Math.round(currentSize + resize[prop === 'width' ? 'x' : 'y'])));

        if (Math.abs(currentSize - newSize) >= 1) {
            state.pushCommand(new SetPropertyCommand($tree, [{
                leafId: leaf.id,
                property: prop,
                value: newSize === maxSize ? {
                    type: 'match_parent'
                } : {
                    type: 'fixed',
                    value: newSize
                }
            }]));
        }
    }

    function onKeydown(event: KeyboardEvent): void {
        const leaf = $selectedLeaf;

        if (!leaf || $readOnly) {
            return;
        }

        const moveUp = moveComponentUp.isPressed(event) || bigMoveComponentUp.isPressed(event);
        const moveDown = moveComponentDown.isPressed(event) || bigMoveComponentDown.isPressed(event);
        const moveLeft = moveComponentLeft.isPressed(event) || bigMoveComponentLeft.isPressed(event);
        const moveRight = moveComponentRight.isPressed(event) || bigMoveComponentRight.isPressed(event);

        const isBigMove = bigMoveComponentUp.isPressed(event) || bigMoveComponentDown.isPressed(event) ||
            bigMoveComponentLeft.isPressed(event) || bigMoveComponentRight.isPressed(event);

        const resizeUp = resizeComponentUp.isPressed(event) || bigResizeComponentUp.isPressed(event);
        const resizeDown = resizeComponentDown.isPressed(event) || bigResizeComponentDown.isPressed(event);
        const resizeLeft = resizeComponentLeft.isPressed(event) || bigResizeComponentLeft.isPressed(event);
        const resizeRight = resizeComponentRight.isPressed(event) || bigResizeComponentRight.isPressed(event);

        const isBigResize = bigResizeComponentUp.isPressed(event) || bigResizeComponentDown.isPressed(event) ||
            bigResizeComponentLeft.isPressed(event) || bigResizeComponentRight.isPressed(event);

        if (deleteComponent.isPressed(event)) {
            if (leaf.parent) {
                state.deleteLeaf(leaf);
            }
        } else if (moveUp || moveDown || moveLeft || moveRight) {
            const offset = {
                // eslint-disable-next-line no-nested-ternary
                x: moveRight ? 1 : (moveLeft ? -1 : 0),
                // eslint-disable-next-line no-nested-ternary
                y: moveDown ? 1 : (moveUp ? -1 : 0)
            };
            if (leaf.parent === $tree) {
                if (isBigMove) {
                    offset.x *= BIG_MOVE_OFFSET;
                    offset.y *= BIG_MOVE_OFFSET;
                }
                moveComponentOffset(leaf, offset);
            } else {
                moveComponentOrder(leaf, offset);
            }
        } else if (resizeUp || resizeDown || resizeLeft || resizeRight) {
            const resize = {
                // eslint-disable-next-line no-nested-ternary
                x: resizeRight ? 1 : (resizeLeft ? -1 : 0),
                // eslint-disable-next-line no-nested-ternary
                y: resizeDown ? 1 : (resizeUp ? -1 : 0)
            };
            if (isBigResize) {
                resize.x *= BIG_RESIZE;
                resize.y *= BIG_RESIZE;
            }
            resizeComponent(leaf, resize);
        } else if (copyShortcut.isPressed(event)) {
            copy();
        } else if (pasteShortcut.isPressed(event)) {
            paste();
        } else if (cancel.isPressed(event)) {
            cancelOperation();
        } else {
            return;
        }

        event.preventDefault();
        event.stopPropagation();
    }

    let mousedownX = 0;
    let mousedownY = 0;
    let wasSelected: TreeLeaf | null = null;
    function onRootMousedown(event: MouseEvent): void {
        mousedownX = event.pageX;
        mousedownY = event.pageY;
        wasSelected = $selectedLeaf;
    }

    function onRootClick(event: MouseEvent): void {
        const dist = (event.pageX - mousedownX) * (event.pageX - mousedownX) +
            (event.pageY - mousedownY) * (event.pageY - mousedownY);

        if (event.target === root && dist < 64 && wasSelected && wasSelected === $selectedLeaf) {
            selectedLeaf.set(null);
        }
    }

    function onRootDoubleClick(event: MouseEvent): void {
        if (componentClone) {
            return;
        }

        const leaf = $selectedLeaf;
        if (!leaf) {
            return;
        }
        const json = leaf.props.json;
        const processedJson = leaf.props.processedJson;
        const evalText = evalJson(processedJson).text;

        if (!(json.type === 'text' || json.type in namedTemplates && namedTemplates[json.type].inlineTextEditorProp)) {
            return;
        }

        const node = leaf.props.node;
        if (!node) {
            return;
        }

        root.focus({
            preventScroll: true
        });
        selectedLeaf.set(leaf);
        selectedElem.set(node);
        highlightLeaf.set(null);
        highlightElem.set(null);
        highlightRanges.set(null);
        event.preventDefault();

        const rotation = processedJson.transform?.rotation || 0;
        const oldTransform = node.style.transform;
        node.style.transform = '';
        const startBbox = node.getBoundingClientRect();
        node.style.transform = oldTransform;
        const computedStyle = getComputedStyle(node);
        const containerBbox = clonesContainer.getBoundingClientRect();
        const offsetWidth = parseFloat(computedStyle.width);
        const offsetHeight = parseFloat(computedStyle.height);
        const paddingTop = parseFloat(computedStyle.paddingTop);
        const paddingRight = parseFloat(computedStyle.paddingRight);
        const paddingBottom = parseFloat(computedStyle.paddingBottom);
        const paddingLeft = parseFloat(computedStyle.paddingLeft);
        const startPosX = startBbox.left - containerBbox.left + paddingLeft;
        const startPosY = startBbox.top - containerBbox.top + paddingTop;
        const parentBbox = node.parentElement.getBoundingClientRect();

        componentClone = node.cloneNode(true) as HTMLElement;
        componentClone.classList.add('renderer__component-clone');
        componentClone.classList.add('renderer__component-clone_inplace');
        componentClone.style.transform = `translate(${startBbox.left - containerBbox.left}px, ${startBbox.top - containerBbox.top}px) rotate(${rotation}deg)`;
        componentClone.style.paddingRight = `${paddingRight}px`;
        componentClone.style.paddingLeft = `${paddingLeft}px`;
        componentClone.style.width = `${Math.ceil(offsetWidth - paddingLeft - paddingRight)}px`;
        componentClone.style.height = `${Math.ceil(offsetHeight - paddingTop - paddingBottom)}px`;
        componentClone.style.fontSize = computedStyle.fontSize;
        Array.from(componentClone.children).forEach(child => {
            if (child instanceof HTMLElement) {
                child.style.opacity = '0';
            }
        });
        clonesContainer.appendChild(componentClone);

        const style = {
            left: `${startBbox.left + paddingLeft}px`,
            top: `${startBbox.top + paddingTop}px`,
            width: `${Math.ceil(offsetWidth - paddingLeft - paddingRight)}px`,
            maxWidth: `${parentBbox.right - startBbox.left - paddingLeft - paddingRight}px`,
            fontSize: computedStyle.fontSize
        };

        if (leaf.props.processedJson.width?.type === 'wrap_content') {
            style.width = 'max-content';
            if (!leaf.props.processedJson.width.constrained) {
                style.maxWidth = '';
            }
        }

        componentCloneRect = {
            top: startPosY + containerBbox.top,
            left: startPosX + containerBbox.left,
            right: startPosX + offsetWidth + containerBbox.left,
            bottom: startPosY + offsetHeight + containerBbox.top,
            width: offsetWidth,
            height: offsetHeight
        };
        componentCloneRotation = rotation;

        node.style.opacity = '0';

        showInplaceEditor = true;

        let text = json.text;
        const tankerKey = state.getTankerKey(text);
        if (tankerKey) {
            text = $tanker[tankerKey]?.[$locale] || '';
            if (!text) {
                return;
            }
        }

        inplaceEditor().show({
            text: tankerKey ? text : evalText,
            json: processedJson,
            leaf,
            rotation,
            disabled: $readOnly,
            textDisabled: $readOnly || Boolean(tankerKey) || text !== evalText,
            style,
            onResoze: onInplaceEditorResize,
            callback: onInplaceEditorClose
        });
    }

    function onInplaceEditorResize(size: {
        width: number;
        height: number;
    }): void {
        if (componentClone) {
            componentClone.style.width = size.width + 'px';
            componentClone.style.height = size.height + 'px';
        }
    }

    function onInplaceEditorClose(value: {
        leaf: TreeLeaf,
        text: string;
        ranges: TextRange[];
        images: TextImage[];
        textAlign: string;
    }): void {
        showInplaceEditor = false;

        if (componentClone) {
            clonesContainer.removeChild(componentClone);
            componentClone = null;
        }

        const text = value.text;
        const ranges = value.ranges;
        const images = value.images;
        const textAlign = value.textAlign;
        const origText = value.leaf.props.processedJson.text;
        const evalText = evalJson({ text: origText }).text;

        const json = value.leaf.props.json;
        const node = value.leaf.props.node;

        const prop = json.type === 'text' ?
            'text' :
            namedTemplates[json.type].inlineTextEditorProp;

        node.style.opacity = '';

        if ($readOnly || !prop) {
            return;
        }

        const oldState = {
            text: json.text,
            ranges: json.ranges,
            images: json.images,
            textAlign: json.text_alignment_horizontal || 'left'
        };
        const tankerKey = state.getTankerKey(json.text);
        let changed = false;

        const newState: {
            text?: string;
            ranges?: unknown[];
            images?: unknown[];
            textAlign?: string;
        } = {};

        const isTextEditDisabled = tankerKey || origText !== evalText;
        newState.text = isTextEditDisabled ? oldState.text : text;
        newState.textAlign = textAlign;
        const changes: {
            property: string;
            value: unknown;
        }[] = [];

        if (ranges.length) {
            if (ranges[0].start === 0 && ranges[ranges.length - 1].end === text.length) {
                const applyRangeToRoot = ranges[0];

                for (const key in applyRangeToRoot) {
                    if (key !== 'start' && key !== 'end' && json[key] !== applyRangeToRoot[key as keyof TextRange]) {
                        changes.push({
                            property: key,
                            value: applyRangeToRoot[key as keyof TextRange]
                        });
                        changed = true;
                    }
                }

                const filtered = ranges.filter(range => !isRangeStyleEqual(range, applyRangeToRoot));
                if (filtered.length) {
                    filtered.forEach(range => {
                        for (const key in range) {
                            if (key !== 'start' && key !== 'end' && range[key as keyof TextRange] === applyRangeToRoot[key as keyof TextRange]) {
                                delete range[key as keyof TextRange];
                            }
                        }
                    });
                    newState.ranges = filtered;
                }
            } else {
                newState.ranges = ranges;
            }
        }

        if (images.length) {
            newState.images = images;
        }

        if (changed || !isEqual(oldState, newState)) {
            state.pushCommand(new SetPropertyCommand($tree, [{
                leafId: value.leaf.id,
                property: prop,
                value: newState.text
            }, {
                leafId: value.leaf.id,
                property: 'ranges',
                value: newState.ranges
            }, {
                leafId: value.leaf.id,
                property: 'images',
                value: newState.images
            }, {
                leafId: value.leaf.id,
                property: 'text_alignment_horizontal',
                value: newState.textAlign !== 'left' ? newState.textAlign : undefined
            }, ...changes.map(change => ({
                leafId: value.leaf.id,
                property: change.property,
                value: change.value
            }))]));
        }
    }

    function copy(): void {
        if ($selectedLeaf) {
            copiedLeaf.set($selectedLeaf);
        }
    }

    function paste(): void {
        if ($selectedLeaf && $copiedLeaf) {
            state.pasteLeaf($copiedLeaf, $selectedLeaf);
        }
    }

    function onUploadImage(): void {
        const leaf = $selectedLeaf;
        const elem = $selectedElem;
        if (!leaf || !elem) {
            return;
        }

        const json = leaf.props.json;
        const processedJson = leaf.props.processedJson;
        if (!processedJson) {
            return;
        }

        let subtype;
        let title;
        let url;
        if (json.type === '_template_lottie') {
            subtype = 'lottie' as const;
            title = $l10n('props.lottie_url');
            url = json.lottie_params?.lottie_url || '';
        } else if (processedJson.type === 'image') {
            subtype = 'image' as const;
            title = $l10n('props.image_url');
            url = processedJson.image_url;
        } else if (processedJson.type === 'gif') {
            subtype = 'gif' as const;
            title = $l10n('props.image_url');
            url = processedJson.gif_url;
        } else {
            return;
        }
        const evalledUrl = evalJson({
            url
        }).url;
        const isExpression = evalledUrl !== url;

        file2Dialog().show({
            target: elem,
            title,
            value: {
                url: evalledUrl
            },
            subtype,
            hasSize: false,
            disabled: $readOnly || isExpression,
            callback(value) {
                let property;
                if (subtype === 'image') {
                    property = 'image_url';
                } else if (subtype === 'gif') {
                    property = 'gif_url';
                } else if (subtype === 'lottie') {
                    property = 'lottie_params.lottie_url';
                }

                if (property) {
                    state.pushCommand(new SetPropertyCommand($tree, [{
                        leafId: leaf.id,
                        property,
                        value: value.url
                    }]));
                }
            }
        });
    }

    function selectOffsetState(offset: number): void {
        const devapi = $selectedLeaf?.props?.devapi;
        const processedJson = $selectedLeaf?.props?.processedJson;
        let items;
        if (processedJson?.type === 'state') {
            items = processedJson.states;
        } else if (processedJson?.type === 'tabs') {
            items = processedJson.items;
        }
        if (!devapi || !processedJson || !Array.isArray(items)) {
            return;
        }

        let index = processedJson.type === 'state' ?
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            items.findIndex((it: any) => it.state_id === devapi.getState()) :
            devapi.getState();
        if (index < 0) {
            index = 0;
        } else {
            index += offset;
            if (index >= items.length) {
                index = 0;
            } else if (index < 0) {
                index = items.length - 1;
            }
        }

        const newState = processedJson.type === 'state' ?
            items[index]?.state_id :
            index;
        if (newState !== undefined) {
            devapi.setState(newState);
        }
    }

    function selectPrevState(): void {
        selectOffsetState(-1);
    }

    function selectNextState(): void {
        selectOffsetState(1);
    }

    function onSelectedStateChange(event: CustomEvent<string>): void {
        const devapi = $selectedLeaf?.props?.devapi;

        if (devapi) {
            devapi.setState(event.detail);
        }
    }

    function onWindowResize(): void {
        if (!showInplaceEditor) {
            return;
        }

        const leaf = $selectedLeaf;
        const node = leaf?.props.node;

        if (!node) {
            return;
        }

        const startBbox = node.getBoundingClientRect();
        const computedStyle = getComputedStyle(node);
        const paddingTop = parseFloat(computedStyle.paddingTop);
        const paddingLeft = parseFloat(computedStyle.paddingLeft);

        inplaceEditor().setPosition(
            `${startBbox.left + paddingLeft}px`,
            `${startBbox.top + paddingTop}px`
        );
    }

    onDestroy(() => {
        instance?.$destroy();
    });
</script>

<svelte:window on:resize={onWindowResize} />

<!-- svelte-ignore a11y_no_noninteractive_tabindex -->
<!-- svelte-ignore a11y_no_static_element_interactions -->
<div
    bind:this={root}
    class="renderer"
    class:renderer_rotate={isRotate}
    tabindex="0"
    on:dragover={onDragOver}
    on:dragleave|preventDefault={onDragLeave}
    on:drop={onDrop}
    on:keydown={onKeydown}
    on:pointerdown|capture={onRootMousedown}
    on:click={onRootClick}
    on:dblclick|preventDefault={onRootDoubleClick}
>
    <div
        class="renderer__container"
        style:--width="{size[0]}px"
        style:--height="{size[1]}px"
    >
        <!-- svelte-ignore a11y_mouse_events_have_key_events -->
        <div
            bind:this={previewWrapper}
            class="renderer__content"
            on:scroll|capture={onPreviewScroll}
            on:mousemove={onHighlightOverlayMove}
            on:mouseout={onHighlightOverlayOut}
        >
            <div class="renderer__content-bg"></div>
            <div
                bind:this={rootPreview}
                class="renderer__content-inner"
                on:pointerdown={onPointerDown}
            ></div>
        </div>

        <div
            class="renderer__resizer renderer__resizer_vh"
            on:pointerdown|stopPropagation|preventDefault={event => onCanvasResize(event, 'down-right')}
        ></div>
        <div
            class="renderer__resizer renderer__resizer_h"
            on:pointerdown|stopPropagation|preventDefault={event => onCanvasResize(event, 'right')}
        ></div>
        <div
            class="renderer__resizer renderer__resizer_v"
            on:pointerdown|stopPropagation|preventDefault={event => onCanvasResize(event, 'down')}
        ></div>
    </div>

    <div class="renderer__overlay-elemens renderer__overlay-root">
        <div
            class="renderer__overlay-center"
            style:width="{size[0]}px"
            style:height="{size[1]}px"
        >
            <div class="renderer__clones-container" bind:this={clonesContainer}></div>

            <div class="renderer__snap-container">
                {#if snapLineX}
                    <div class="renderer__snap renderer__snap_vertical" style:left="{snapLineX}px"></div>
                {/if}
                {#if snapLineY}
                    <div class="renderer__snap renderer__snap_horizontal" style:top="{snapLineY}px"></div>
                {/if}
            </div>

            {#if bestDragTarget}
                <div
                    class="renderer__drag-target"
                    style:top="{bestDragTarget.target.top}px"
                    style:left="{bestDragTarget.target.left}px"
                    style:width="{bestDragTarget.target.width}px"
                    style:height="{bestDragTarget.target.height}px"
                >
                    {#if bestDragTarget.target.visibleText}
                        <div class="renderer__drag-target-text">
                            {bestDragTarget.target.visibleText}
                        </div>
                    {/if}
                </div>

                <div
                    class="renderer__drag-target-parent"
                    style:top="{bestDragTarget.parent.top}px"
                    style:left="{bestDragTarget.parent.left}px"
                    style:width="{bestDragTarget.parent.width}px"
                    style:height="{bestDragTarget.parent.height}px"
                ></div>

                {#each bestDragTarget.childs as child}
                    <div
                        class="renderer__drag-target-child"
                        style:top="{child.top}px"
                        style:left="{child.left}px"
                        style:width="{child.width}px"
                        style:height="{child.height}px"
                    ></div>
                {/each}
            {/if}

            {#if highlights}
                {#each highlights as highlight (highlight.elem)}
                    <div class="renderer__highlight-inner">
                        <div
                            class="renderer__highlight"
                            style:left="{highlight.left + highlight.margins.left}px"
                            style:top="{highlight.top + highlight.margins.top}px"
                            style:width="{highlight.widthNum}px"
                            style:height="{highlight.heightNum}px"
                            style:margin-top="{-scrollY}px"
                            style:margin-left="{-scrollX}px"
                        >
                            <div
                                class="renderer__highlight-border"
                                class:renderer__highlight-border_insets={highlight.insets}
                                style:transform="rotate({highlight.rotation}deg)"
                            >
                                {#if highlight.insets}
                                    <svg
                                        class="renderer__highlight-insets"
                                        xmlns="http://www.w3.org/2000/svg"
                                        width={highlight.widthNum + highlight.margins.left + highlight.margins.right}
                                        height={highlight.heightNum + highlight.margins.top + highlight.margins.bottom}
                                        style:top="{-highlight.margins.top}px"
                                        style:left="{-highlight.margins.left}px"
                                    >
                                        <defs>
                                            <pattern id="pattern"
                                                width="6"
                                                height="10"
                                                patternUnits="userSpaceOnUse"
                                                patternTransform="rotate(45 50 50)"
                                            >
                                                <line style="stroke:var(--accent-purple)" stroke-width="2px" y2="10"/>
                                            </pattern>
                                        </defs>
                                        <path
                                            fill="url(#pattern)"
                                            d={highlight.insets}
                                            stroke-width="1"
                                            style="stroke:var(--accent-purple)"
                                        ></path>
                                    </svg>
                                {/if}

                                {#if highlight.grid && !highlight.insets && !$readOnly}
                                    <div
                                        class="renderer__highlight-grid"
                                        class:renderer__highlight-grid_permanent={highlight.permanent}
                                    >
                                        {#each highlight.grid.columns as column, index}
                                            <div
                                                class="renderer__highlight-grid-column"
                                                style:left="{highlight.paddings.left + column}px"
                                                on:pointerdown|stopPropagation|preventDefault={event => onGridResize(event, 'column', index)}
                                            ></div>
                                        {/each}
                                        {#each highlight.grid.rows as row, index}
                                            <div
                                                class="renderer__highlight-grid-row"
                                                style:top="{highlight.paddings.top + row}px"
                                                on:pointerdown|stopPropagation|preventDefault={event => onGridResize(event, 'row', index)}
                                            ></div>
                                        {/each}
                                    </div>
                                {/if}

                                {#if highlight.gradientAngle}
                                    <svg
                                        class="renderer__highlight-gradient-angle"
                                        xmlns="http://www.w3.org/2000/svg"
                                        width={highlight.widthNum + highlight.margins.left + highlight.margins.right}
                                        height={highlight.heightNum + highlight.margins.top + highlight.margins.bottom}
                                        style:top="{-highlight.margins.top}px"
                                        style:left="{-highlight.margins.left}px"
                                    >
                                        <path
                                            d={highlight.gradientAngle}
                                            stroke-width="3"
                                            stroke-dasharray="12 12"
                                            style="stroke:var(--accent-purple)"
                                        ></path>
                                    </svg>
                                {/if}

                                {#if highlight.permanent && !highlight.isRoot && !highlight.insets && !$readOnly}
                                    <div class="renderer__highlight-border-bottom-right"
                                        on:pointerdown|stopPropagation|preventDefault={event => onResize(event, 'down-right')}
                                    ></div>
                                    <div class="renderer__highlight-border-right"
                                        on:pointerdown|stopPropagation|preventDefault={event => onResize(event, 'right')}
                                    ></div>
                                    <div class="renderer__highlight-border-bottom"
                                        on:pointerdown|stopPropagation|preventDefault={event => onResize(event, 'down')}
                                    ></div>
                                {/if}
                                {#if
                                    (highlight.permanent || highlight.isRotate) &&
                                    !highlight.isRoot &&
                                    !highlight.insets &&
                                    !$readOnly
                                }
                                    <div class="renderer__highlight-border-rotate"
                                        on:pointerdown|stopPropagation|preventDefault={onRotate}
                                    ></div>
                                {/if}

                                {#if highlight.isRotate}
                                    <div class="renderer__highlight-rotate-center"></div>
                                    <div class="renderer__highlight-rotate-axis"></div>
                                    <div
                                        class="renderer__highlight-rotate-axis renderer__highlight-rotate-axis_vertical"
                                        style:--rotation="{-componentCloneRotation}deg"
                                    ></div>
                                    <div
                                        class="renderer__highlight-rotate-angle"
                                        style:--offset-x="{54 * Math.cos(degToRad(270 - componentCloneRotation / 2))}px"
                                        style:--offset-y="{54 * Math.sin(degToRad(270 - componentCloneRotation / 2))}px"
                                        style:--rotation="{-componentCloneRotation}deg"
                                    >
                                        {Math.round(componentCloneRotation)}°
                                    </div>
                                    <svg
                                        class="renderer__highlight-rotate-arc-wrapper"
                                        xmlns="http://www.w3.org/2000/svg"
                                        viewBox="0 0 64 64"
                                    >
                                        <path
                                            d="M 32 {32 - 24} A 24 24 0 {componentCloneRotation >= 180 ? 1 : 0} 0 {32 + 24 * Math.cos(degToRad(-componentCloneRotation - 90))} {32 + 24 * Math.sin(degToRad(-componentCloneRotation - 90))}"
                                            class="renderer__highlight-rotate-arc"
                                            stroke-width="3"
                                            stroke-linecap="round"
                                            fill="none"
                                        ></path>
                                    </svg>
                                {/if}

                                {#if highlight.visibleText}
                                    <div class="renderer__highlight-text">
                                        {highlight.visibleText}
                                    </div>
                                {/if}

                                {#if highlight.emptyFileType && !$readOnly}
                                    <button
                                        class="renderer__upload-file"
                                        class:renderer__upload-file_big={highlight.widthNum >= 220}
                                        style:--rotation="{-highlight.rotation}deg"
                                        on:click={onUploadImage}
                                    >
                                        <div class="renderer__upload-file-icon"></div>
                                        <div class="renderer__upload-file-text">
                                            {$l10n(`file.${highlight.emptyFileType}_upload`)}
                                        </div>
                                    </button>
                                {/if}
                            </div>
                        </div>
                    </div>
                    {#if highlight.permanent && !highlight.insets || highlight.isResize}
                        <div
                            class="renderer__highlight-info"
                            style:left="{highlight.left + highlight.margins.left + highlight.widthNum / 2 - scrollX}px"
                            style:top="{highlight.top + highlight.margins.top + highlight.heightNum - scrollY}px"
                        >
                            <div class="renderer__highlight-info-inner">
                                {highlight.width}x{highlight.height}
                            </div>
                        </div>

                        {#if highlight.state}
                            <div
                                class="renderer__state-info"
                                style:left="{highlight.left + highlight.margins.left - scrollX}px"
                                style:top="{highlight.top + highlight.margins.top - scrollY}px"
                            >
                                <div class="renderer__state-info-buttons">
                                    <button
                                        class="renderer__highlight-info-inner renderer__highlight-info-inner_button"
                                        on:click={selectPrevState}
                                    >
                                        &lt;
                                    </button>
                                    <button
                                        class="renderer__highlight-info-inner renderer__highlight-info-inner_button"
                                        on:click={selectNextState}
                                    >
                                        &gt;
                                    </button>
                                </div>
                                <Select
                                    mix="renderer__highlight-dropdown"
                                    theme="preview"
                                    value={highlight.state.selectedValue}
                                    items={highlight.state.list}
                                    on:change={onSelectedStateChange}
                                />
                            </div>
                        {/if}
                    {/if}
                {/each}
            {/if}

            {#if highlightDistance}
                {#each highlightDistance as item}
                    <div class="renderer__highlight-inner">
                        <div
                            class="renderer__highlight-distance renderer__highlight-distance_orientation_{item.orientation}"
                            style:left="{item.left}px"
                            style:top="{item.top}px"
                            style:width={item.orientation === 'horizontal' ? `${item.length}px` : ''}
                            style:height={item.orientation === 'horizontal' ? '' : `${item.length}px`}
                            style:margin-top="{-scrollY}px"
                            style:margin-left="{-scrollX}px"
                        >
                            <div class="renderer__highlight-distance-info">
                                {formatSize(item.length)}
                            </div>
                        </div>

                        {#if item.sub}
                            <div
                                class="renderer__highlight-distance renderer__highlight-distance_sub renderer__highlight-distance_orientation_{item.sub.orientation}"
                                style:left="{item.sub.left}px"
                                style:top="{item.sub.top}px"
                                style:width={item.sub.orientation === 'horizontal' ? `${item.sub.length}px` : ''}
                                style:height={item.sub.orientation === 'horizontal' ? '' : `${item.sub.length}px`}
                                style:margin-top="{-scrollY}px"
                                style:margin-left="{-scrollX}px"
                            >
                            </div>
                        {/if}
                    </div>
                {/each}
            {/if}
        </div>
    </div>
</div>

<style>
    .renderer {
        position: relative;
        width: 100%;
        height: 100%;
        outline: none;

        --resizer-size: 16px;
    }

    .renderer_rotate {
        cursor: url(../../assets/rotate.svg) 12 12, grab;
    }

    .renderer_rotate .renderer__container,
    .renderer_rotate .renderer__overlay-elemens {
        pointer-events: none;
    }

    .renderer__container {
        display: grid;
        margin: 28px auto 0;
        width: min-content;
        grid-template-columns: auto var(--resizer-size);
        grid-template-rows: auto var(--resizer-size);
    }

    .renderer__resizer {
        position: relative;
        background: var(--fill-transparent-1);
        transition: background-color .15s ease-in-out;
    }

    .renderer__resizer::before {
        position: absolute;
        top: 3px;
        right: 3px;
        bottom: 3px;
        left: 3px;

        content: '';

        background: no-repeat 50% 50%;
        background-size: contain;
        opacity: .3;
        filter: var(--icon-filter);
    }

    .renderer__resizer:hover,
    .renderer__resizer_vh:hover ~ .renderer__resizer_v,
    .renderer__resizer_vh:hover ~ .renderer__resizer_h {
        background-color: var(--fill-transparent-3);
    }

    .renderer__resizer:active,
    .renderer__resizer_vh:active ~ .renderer__resizer_v,
    .renderer__resizer_vh:active ~ .renderer__resizer_h {
        background-color: var(--fill-transparent-4);
    }

    .renderer__resizer_v {
        cursor: row-resize;
    }

    .renderer__resizer_v::before {
        background-image: url(../../assets/resizeV.svg);
        background-size: 36px 9px;
    }

    .renderer__resizer_h {
        cursor: col-resize;
    }

    .renderer__resizer_h::before {
        background-image: url(../../assets/resizeH.svg);
        background-size: 9px 36px;
    }

    .renderer__resizer_vh {
        grid-area: 2 / 2 / 3 / 3;
        cursor: se-resize;
    }

    .renderer__resizer_vh:before {
        background-image: url(../../assets/resizeVH.svg);
        background-size: 6px;
    }

    .renderer__content {
        outline: 1px solid var(--fill-transparent-1);
        position: relative;
        width: var(--width);
        height: var(--height);
        padding: 0;
        overflow: auto;
        touch-action: none;
        color: #000;
        background: #fff;
    }

    .renderer__content-bg {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: 0 0 repeat url(../../assets/alpha.png);
        opacity: .3;
    }

    .renderer__content-inner {
        position: relative;
        display: flex;
        min-width: 100%;
        min-height: 100%;
        color: #000;
    }

    .renderer__content-inner > :global(*) {
        width: 100%;
        pointer-events: none;
        user-select: none;
    }

    .renderer__overlay-elemens {
        pointer-events: none;
        color: #000;
    }

    .renderer__overlay-root {
        position: absolute;
        top: -72px;
        right: 0;
        bottom: 0;
        left: 0;
        overflow: hidden;
    }

    .renderer__overlay-center {
        position: absolute;
        top: 72px;
        left: 0;
        right: var(--resizer-size);
        margin: 28px auto 0;
    }

    .renderer__highlight-inner {
        position: absolute;
        top: 0;
        right: 0;
        bottom: 0;
        left: 0;
    }

    .renderer__highlight {
        position: absolute;
        display: flex;
    }

    .renderer__highlight-border {
        position: absolute;
        top: 0;
        left: 0;
        box-sizing: border-box;
        width: calc(100% + 6px);
        height: calc(100% + 6px);
        margin-top: -3px;
        margin-left: -3px;
        border: 3px solid var(--accent-purple);
        border-radius: 4px;
    }

    .renderer__highlight-border_insets {
        border-color: transparent;
    }

    .renderer__highlight-border-right {
        position: absolute;
        top: 0;
        right: -8px;
        width: 14px;
        height: 100%;
        cursor: e-resize;
        pointer-events: auto;
    }

    .renderer__highlight-border-right::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        box-sizing: border-box;
        width: 14px;
        height: 14px;
        margin: auto;
        border: 3px solid var(--accent-purple);
        border-radius: 1024px;
        background: #fff;
    }

    .renderer__highlight-border-bottom {
        position: absolute;
        left: 0;
        bottom: -8px;
        width: 100%;
        height: 14px;
        cursor: s-resize;
        pointer-events: auto;
    }

    .renderer__highlight-border-bottom::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        box-sizing: border-box;
        width: 14px;
        height: 14px;
        margin: auto;
        border: 3px solid var(--accent-purple);
        border-radius: 1024px;
        background: #fff;
    }

    .renderer__highlight-border-bottom-right {
        position: absolute;
        right: -7px;
        bottom: -7px;
        width: 14px;
        height: 14px;
        pointer-events: auto;
        cursor: se-resize;
    }

    .renderer__highlight-border-rotate {
        position: absolute;
        top: -30px;
        left: calc(50% - 7px);
        box-sizing: border-box;
        width: 14px;
        height: 14px;
        border: 3px solid var(--accent-purple);
        border-radius: 1024px;
        background: #fff;
        pointer-events: auto;
        cursor: url(../../assets/rotate.svg) 12 12, grab;
    }

    .renderer__highlight-border-rotate::before {
        content: '';
        position: absolute;
        z-index: -1;
        top: 0;
        left: 3px;
        width: 3px;
        height: 27px;
        background: var(--accent-purple);
        pointer-events: none;
    }

    .renderer__highlight-info {
        position: absolute;
        transform: translateX(-50%);
        margin-top: 10px;
    }

    .renderer__highlight-info-inner {
        padding: 4px 8px;
        background: var(--accent-purple);
        font-size: 12px;
        border-radius: 4px;
        color: #fff;
        font-family: var(--monospace-font);
    }

    .renderer__highlight-info-inner_button {
        margin: 0;
        cursor: pointer;
        pointer-events: auto;
        transition: background-color .15s ease-in-out;
        border: none;
        font-family: inherit;
        appearance: none;
    }

    .renderer__highlight-info-inner_button:hover {
        background-color: var(--accent-purple-hover);
    }

    .renderer__highlight-info-inner_button:active {
        background-color: var(--accent-purple-active);
    }

    .renderer__state-info {
        position: absolute;
        transform: translateY(-100%);
        display: flex;
        gap: 6px;
        margin-top: -10px;
    }

    .renderer__state-info-buttons {
        display: flex;
        gap: 2px;
    }

    :global(.renderer__highlight-dropdown) {
        pointer-events: auto;
    }

    .renderer__highlight-insets {
        position: relative;
        display: block;
    }

    .renderer__highlight-gradient-angle {
        position: relative;
        display: block;
    }

    .renderer__highlight-distance {
        position: absolute;
        border-width: 0;
    }

    .renderer__highlight-distance_orientation_horizontal {
        border-bottom: 1px solid var(--accent-purple);
    }

    .renderer__highlight-distance_orientation_vertical {
        border-right: 1px solid var(--accent-purple);
    }

    .renderer__highlight-distance_sub {
        border-style: dotted;
    }

    .renderer__highlight-distance-info {
        position: absolute;
        padding: 4px 8px;
        background: var(--accent-purple);
        font-size: 12px;
        border-radius: 4px;
        color: #fff;
        font-family: var(--monospace-font);
    }

    .renderer__highlight-distance_orientation_horizontal .renderer__highlight-distance-info {
        top: 4px;
        left: 50%;
        transform: translateX(-50%);
    }

    .renderer__highlight-distance_orientation_vertical .renderer__highlight-distance-info {
        top: 50%;
        left: 4px;
        transform: translateY(-50%);
    }

    .renderer__highlight-rotate-axis {
        position: absolute;
        top: -3px;
        left: calc(50% - 1.5px);
        height: calc(50% + 3px);
        width: 3px;
        border-radius: 1024px;
        background: var(--accent-purple);
        transform-origin: 50% 100%;
        transform: rotate(var(--rotation, 0));
    }

    .renderer__highlight-rotate-axis_vertical::before {
        position: absolute;
        top: 0;
        left: calc(1.5px - 10px);
        width: 20px;
        height: 3px;
        border-radius: 1024px;
        background: var(--accent-purple);
        content: '';
    }

    .renderer__highlight-rotate-center {
        position: absolute;
        top: 50%;
        left: 50%;
        width: 16px;
        height: 16px;
        border-radius: 1024px;
        background: var(--accent-purple);
        transform: translate(-50%, -50%);
    }

    .renderer__highlight-rotate-arc-wrapper {
        position: absolute;
        top: calc(50% - 32px);
        left: calc(50% - 32px);
        width: 64px;
        height: 64px;
        overflow: visible;
    }

    .renderer__highlight-rotate-arc {
        stroke: var(--accent-purple);
    }

    .renderer__highlight-rotate-angle {
        position: absolute;
        top: calc(50% + var(--offset-y));
        left: calc(50% + var(--offset-x));
        padding: 4px 8px;
        background: var(--accent-purple);
        font-size: 12px;
        border-radius: 4px;
        color: #fff;
        font-family: var(--monospace-font);
        transform: translate(-50%, -50%) rotate(var(--rotation));
    }

    .renderer__highlight-text {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        font-weight: bold;
        font-size: 20px;
    }

    .renderer__highlight-grid-column,
    .renderer__highlight-grid-row {
        position: absolute;
    }

    .renderer__highlight-grid_permanent .renderer__highlight-grid-column,
    .renderer__highlight-grid_permanent .renderer__highlight-grid-row {
        pointer-events: auto;
    }

    .renderer__highlight-grid-column {
        top: 0;
        width: 8px;
        height: 100%;
        margin-left: -4px;
        cursor: e-resize;
    }

    .renderer__highlight-grid-column::before {
        position: absolute;
        top: 0;
        left: 50%;
        width: 0;
        height: 100%;
        border-right: 1px dashed var(--accent-purple);
        content: '';
        transition: transform .3s ease-in-out;
    }

    .renderer__highlight-grid-column:hover::before {
        transform: scaleX(4);
    }

    .renderer__highlight-grid-row {
        left: 0;
        width: 100%;
        height: 8px;
        margin-top: -4px;
        cursor: s-resize;
    }

    .renderer__highlight-grid-row::before {
        position: absolute;
        top: 50%;
        left: 0;
        width: 100%;
        height: 0;
        border-top: 1px dashed var(--accent-purple);
        content: '';
        transition: transform .3s ease-in-out;
    }

    .renderer__highlight-grid-row:hover::before {
        transform: scaleY(4);
    }

    .renderer__drag-target {
        position: absolute;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: -2px;
        border: 2px solid var(--accent-purple);
        background: repeating-linear-gradient(
            135deg,
            transparent 0,
            transparent 4px,
            var(--accent-purple) 4px,
            var(--accent-purple) 6px
        );
        border-radius: 2px;
    }

    .renderer__drag-target-text {
        font-weight: bold;
        font-size: 20px;
    }

    .renderer__drag-target-parent {
        position: absolute;
        margin: -1px;
        border: 1px solid var(--accent-purple);
    }

    .renderer__drag-target-child {
        position: absolute;
        box-sizing: border-box;
        border: 3px solid var(--accent-purple);
        background: repeating-linear-gradient(
            135deg,
            transparent 0,
            transparent 4px,
            var(--accent-purple) 4px,
            var(--accent-purple) 6px
        );
        opacity: .4;
    }

    .renderer__clones-container {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        pointer-events: none;
        overflow: hidden;
    }

    :global(.renderer__component-clone) {
        position: absolute !important;
        top: 0;
        left: 0;
        box-sizing: border-box !important;
        margin: 0 !important;
    }

    :global(.renderer__component-clone_inplace) {
        box-sizing: content-box !important;
    }

    .renderer__snap-container {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        overflow: hidden;
    }

    .renderer__snap {
        position: absolute;
        border: 0px dotted var(--accent-purple);
    }

    .renderer__snap_vertical {
        top: 0;
        border-right-width: 1px;
        width: 0;
        height: 100%;
    }

    .renderer__snap_horizontal {
        left: 0;
        width: 100%;
        height: 0;
        border-bottom-width: 1px;
    }

    .renderer__upload-file {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%) rotate(var(--rotation));

        display: flex;
        align-items: center;
        gap: 8px;
        margin: 0;
        padding: 6px;
        font: inherit;
        font-size: 14px;
        line-height: 20px;
        font-weight: 500;
        color: #fff;
        border: none;
        border-radius: 6px;
        background: var(--accent-purple);
        white-space: nowrap;
        pointer-events: auto;
        transition: background-color .15s ease-in-out;
        cursor: pointer;
    }

    .renderer__upload-file:hover {
        background-color: var(--accent-purple-hover);
    }

    .renderer__upload-file:active {
        background-color: var(--accent-purple-active);
    }

    .renderer__upload-file-icon {
        width: 20px;
        height: 20px;
        background: no-repeat 50% 50% url(../../assets/upload.svg);
        background-size: contain;
    }

    .renderer__upload-file_big {
        padding-right: 12px;
        padding-left: 12px;
    }

    .renderer__upload-file-text {
        display: none;
    }

    .renderer__upload-file_big .renderer__upload-file-text {
        display: block;
    }

    :global(.renderer__divkit-gallery > div > div > div) {
        pointer-events: auto;
    }

    :global(.renderer__divkit-gallery > div:not(:first-child)) {
        pointer-events: auto;
    }

    :global(.renderer__divkit-gallery > div > div > div > .renderer__divkit-node) {
        pointer-events: none;
    }

    :global(.renderer__divkit-pager > div > div > div) {
        pointer-events: auto;
    }

    :global(.renderer__divkit-pager > div:not(:first-child)) {
        pointer-events: auto;
    }

    :global(.renderer__divkit-pager > div > div > div > .renderer__divkit-node) {
        pointer-events: none;
    }

    :global(.renderer__divkit-tabs > div:first-child > span) {
        pointer-events: auto;
    }
</style>
