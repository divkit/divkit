import { extractLinearGradientParamsFromTransform, extractRadialOrDiamondGradientParams } from '@figma-plugin/helpers';
import { rgbaComponentsToHex } from './utils/rgbaComponentsToHex';
import { checkIsImageNode } from './utils/checkIsImageNode';
import { shouldSkipContainer } from './utils/shouldSkipContainer';
import { getLayoutParams, supportsLayoutParams } from './utils/getLayoutParams';
import { getChildren } from './utils/getChildren';
import { isInsideNode } from './utils/isInsideNode';
import { preprocessNodeName } from './utils/preprocessNodeName';
import { CONTENT_HORIZONTAL_ALIGNMENT, CONTENT_VERTICAL_ALIGNMENT } from './constants/valueMaps';
import { MAIN_SIZES, PREVIEW_SIZES } from './constants/pluginSizes';

import type {
    NodeWithChildren,
    GeneratedNode,
    GeneratedNodeParameter,
    PluginUIScreen
} from './types/common';
import type { FigmaCodeMessage, PreviewError, UIMessage } from './types/messages';
import type {
    DivJson,
    Palette,
    DivVariable,
    DivBaseData,
    Border,
    CornersRadius,
    Background,
    DivContainerData,
    DivSeparatorData,
    DivImageData,
    DivTextData,
    TextStyles
} from './types/divkit';

let pluginUIScreen: PluginUIScreen = 'main';
let currentOpenedNode = '';
let figmaColorsMap: Record<string, string> = {};
let lightColors: Record<string, readonly Paint[]> = {};
let darkColors: Record<string, readonly Paint[]> = {};

let palette: Palette = {
    dark: [],
    light: [],
};
let variables: DivVariable[] = [];
let textCounter = 0;

figma.showUI(__html__, MAIN_SIZES);

const preprocessFigmaColors = async (colors: PaintStyle[]) => {
    try {
        const lightColorName = await figma.clientStorage.getAsync('lightColorName');
        const darkColorName = await figma.clientStorage.getAsync('darkColorName');

        const figmaColorsMap: Record<string, string> = {};
        const lightColors: Record<string, readonly Paint[]> = {};
        const darkColors: Record<string, readonly Paint[]> = {};

        for (const color of colors) {
            if (color.name.includes(lightColorName)) {
                const key = color.name.replace(lightColorName, '');
                lightColors[key] = color.paints;
                figmaColorsMap[color.id] = key;
            }

            if (color.name.includes(darkColorName)) {
                const key = color.name.replace(darkColorName, '');
                darkColors[key] = color.paints;
                figmaColorsMap[color.id] = key;
            }
        }

        return [figmaColorsMap, lightColors, darkColors] as const;
    } catch (error) {
        console.error((error as Error).message);
        figma.notify('Cannot preprocess figma colors');

        return [{}, {}, {}];
    }
};

const checkErrors = (node: BaseNode) => {
    const SUPPORTED_TYPES = ['FRAME', 'GROUP', 'COMPONENT', 'INSTANCE', 'TEXT', 'RECTANGLE', 'VECTOR'];
    const checkNodes = [node, ...getChildren(node)];
    const errors: PreviewError[] = [];

    for (const n of checkNodes) {
        if ('visible' in n && !n.visible) {
            continue;
        }

        const nodeInfo = {
            id: n.id,
            name: n.name,
        };
        if (!SUPPORTED_TYPES.includes(n.type)) {
            errors.push({
                message: `Node with type "${n.type}" is not supported`,
                nodeInfo,
                level: 'error',
            });
        }

        if (
            'fills' in n &&
            n.fills !== figma.mixed &&
            n.fills.some(fill => fill.type === 'GRADIENT_ANGULAR' || fill.type === 'GRADIENT_DIAMOND')
        ) {
            errors.push({
                message: '"Angular" and "Diamond" gradients are not supported',
                nodeInfo,
                level: 'error',
            });
        }

        if (
            'fills' in n &&
            n.fills !== figma.mixed &&
            n.fills.some(fill => fill.type === 'GRADIENT_RADIAL')
        ) {
            errors.push({
                message: '"Radial" gradient can only have the shape of a circle',
                nodeInfo,
                level: 'warn',
            });
        }

        if (supportsChildren(n) && n.children.length === 0) {
            errors.push({
                message: `Empty node with type "${n.type}". Should be deleted or replaced`,
                nodeInfo,
                level: 'warn',
            });
        }

        if (n.type === 'RECTANGLE' && Array.isArray(n.fills) && !n.fills.length) {
            errors.push({
                message: 'Transparent rectangle. Should be deleted or replaced',
                nodeInfo,
                level: 'warn',
            });
        }
    }

    figma.ui.postMessage<FigmaCodeMessage>({
        type: 'showPreviewErrors',
        data: { errors },
    });
};

const initPlugin = async () => {
    let lightColorName = await figma.clientStorage.getAsync('lightColorName');
    let darkColorName = await figma.clientStorage.getAsync('darkColorName');

    if (!lightColorName) {
        lightColorName = '[Light]';
        await figma.clientStorage.setAsync('lightColorName', lightColorName);
    }

    if (!darkColorName) {
        darkColorName = '[Dark]';
        await figma.clientStorage.setAsync('lightColorName', darkColorName);
    }

    figma.ui.postMessage<FigmaCodeMessage>({
        type: 'updateStyleSettings',
        data: {
            lightName: lightColorName,
            darkName: darkColorName,
        },
    });

    const colors = await figma.clientStorage.getAsync('colors');

    if (colors?.length > 0) {
        const [map, light, dark] = await preprocessFigmaColors(colors);
        figmaColorsMap = map;
        lightColors = light;
        darkColors = dark;
    }

    const generatedNodes = getGeneratedNodesData();

    figma.ui.postMessage<FigmaCodeMessage>({
        type: 'renderGeneratedNodes',
        data: { generatedNodes },
    });

    if (!process.env.__SECRET_INTERNALS) {
        const isTokenSaved = Boolean(await figma.clientStorage.getAsync('personalToken'));

        if (isTokenSaved) {
            checkCurrentSelection();
        } else {
            figma.ui.postMessage<FigmaCodeMessage>({
                type: 'setPluginUIScreen',
                data: { screen: 'settings' },
            });
        }

        figma.ui.postMessage<FigmaCodeMessage>({
            type: 'checkPersonalToken',
            data: { isTokenSaved },
        });
        return;
    }

    checkCurrentSelection();
};

const checkCurrentSelection = () => {
    if (pluginUIScreen === 'settings') {
        return;
    }

    const selection = figma.currentPage.selection[0];
    const generatedNodes = getGeneratedNodesData();

    if (pluginUIScreen === 'main') {
        figma.ui.postMessage<FigmaCodeMessage>({
            type: 'updateMainButtonState',
            data: { isActive: Boolean(selection && supportsChildren(selection)) },
        });
    } else {
        const isSelectionGenerated = generatedNodes.find(node => node.id === selection?.id);
        figma.ui.postMessage<FigmaCodeMessage>({
            type: 'updateParametersButtonState',
            data: { isActive: Boolean(selection) && !isSelectionGenerated },
        });
    }

    if (selection) {
        for (const generatedNode of generatedNodes) {
            const figmaNode = figma.getNodeById(generatedNode.id);

            if (!figmaNode) {
                return;
            }

            if ((selection.id === generatedNode.id || isInsideNode(selection, figmaNode))) {
                if (currentOpenedNode === generatedNode.id) {
                    return;
                }
                const parameters = getNodeParameters(generatedNode.id);
                currentOpenedNode = generatedNode.id;
                figma.ui.postMessage<FigmaCodeMessage>({
                    type: 'renderGeneratedNode',
                    data: {
                        node: generatedNode,
                        parameters,
                    },
                });

                checkErrors(figmaNode);
                return;
            }
        }
    }

    figma.ui.postMessage<FigmaCodeMessage>({
        type: 'setPluginUIScreen',
        data: { screen: 'main' },
    });
    figma.ui.resize(MAIN_SIZES.width, MAIN_SIZES.height);
    figma.ui.postMessage<FigmaCodeMessage>({
        type: 'renderGeneratedNodes',
        data: { generatedNodes },
    });
    currentOpenedNode = '';
};

figma.on('selectionchange', checkCurrentSelection);

figma.ui.onmessage = async (msg: UIMessage) => {
    if (msg.type === 'setFigmaSelection') {
        const figmaNode = figma.getNodeById(msg.data.id);

        if (figmaNode) {
            try {
                figma.currentPage.selection = [figmaNode as SceneNode];
            } catch (error) {
                console.error((error as Error).message);
                figma.notify('Cannot selected node', { error: true });
            }
        }
    }

    if (msg.type === 'resizePlugin') {
        figma.ui.resize(msg.data.width, msg.data.height);
    }

    if (msg.type === 'showFigmaNotification') {
        figma.notify(msg.data.message, { error: msg.data.error });
    }

    if (msg.type === 'setPluginUIScreen') {
        pluginUIScreen = msg.data.screen;
        if (pluginUIScreen === 'main') {
            currentOpenedNode = '';
        }
    }

    if (msg.type === 'createJson') {
        const [localMap, localLight, localDark] = await preprocessFigmaColors(figma.getLocalPaintStyles());
        figmaColorsMap = {
            ...figmaColorsMap,
            ...localMap,
        };
        lightColors = {
            ...lightColors,
            ...localLight,
        };
        darkColors = {
            ...darkColors,
            ...localDark,
        };
        const node = figma.getNodeById(msg.data.nodeId) as NodeWithChildren | null;

        if (!node) {
            const error = `Generation node is not found (id: ${msg.data.nodeId})`;
            console.error(error);
            figma.notify(error, { error: true });
            figma.ui.postMessage<FigmaCodeMessage>({
                type: 'generationError',
            });
            return;
        }

        await uploadAllImages(node, msg.data.needUpdateImages);

        try {
            await buildDivLayoutJsonFrom(node);
        } catch (error) {
            console.error((error as Error).message);
            figma.notify('Something went wrong');
            figma.ui.postMessage<FigmaCodeMessage>({
                type: 'generationError',
            });
        }
    }

    if (msg.type === 'saveStyles') {
        const lightColorName = msg.data.lightName || '[Light]';
        const darkColorName = msg.data.darkName || '[Dark]';
        await figma.clientStorage.setAsync('lightColorName', lightColorName);
        await figma.clientStorage.setAsync('darkColorName', darkColorName);

        figma.ui.postMessage<FigmaCodeMessage>({
            type: 'updateStyleSettings',
            data: {
                lightName: lightColorName,
                darkName: darkColorName,
            },
        });

        if (figma.getLocalPaintStyles().length === 0) {
            figma.notify('This document does not have styles');
        } else {
            try {
                await figma.clientStorage
                    .setAsync(
                        'colors',
                        figma.getLocalPaintStyles().map(it => ({ id: it.id, name: it.name, paints: it.paints }))
                    );
                const colors = await figma.clientStorage.getAsync('colors');

                if (colors?.length > 0) {
                    const [map, light, dark] = await preprocessFigmaColors(colors);
                    figmaColorsMap = map;
                    lightColors = light;
                    darkColors = dark;
                    figma.notify('Styles saved');
                } else {
                    figma.notify('Styles is not saved', { error: true });
                }
            } catch (err) {
                console.error(err);
                figma.notify('Styles is not saved', { error: true });
            }
        }
    }

    if (msg.type === 'clearStyles') {
        try {
            await figma.clientStorage.deleteAsync('colors');

            figmaColorsMap = {};
            lightColors = {};
            darkColors = {};
            figma.notify('Styles deleted');
        } catch (err) {
            console.error(err);
            figma.notify('Styles is not deleted', { error: true });
        }
    }

    if (msg.type === 'createGeneratedNode') {
        const selection = figma.currentPage.selection[0];
        createOrUpdateGeneratedNode(selection);

        const figmaNode = figma.getNodeById(selection.id);

        if (figmaNode) {
            checkErrors(figmaNode);
        }
    }

    if (msg.type === 'removeGeneratedNode') {
        const generatedNodesIds: string[] = getGeneratedNodesIds();
        const newGeneratedNodesIds: string[] = generatedNodesIds.filter(x => x !== msg.data.id);
        figma.currentPage.setPluginData('generatedNodesIds', JSON.stringify(newGeneratedNodesIds));

        const removedNode = figma.getNodeById(msg.data.id);
        const removedParametersIds = JSON.parse(removedNode?.getPluginData('parametersIds') || '[]');
        removedNode?.setPluginData('parametersIds', '');
        removedNode?.setPluginData('jsonString', '');

        if (removedParametersIds?.length) {
            for (const parameterId of removedParametersIds) {
                const node = figma.getNodeById(parameterId);

                if (node) {
                    node.setPluginData('type', '');
                    node.setPluginData('uploadUrl', '');
                }
            }
        }

        const generatedNodes = getGeneratedNodesData();
        figma.ui.postMessage<FigmaCodeMessage>({
            type: 'renderGeneratedNodes',
            data: { generatedNodes },
        });
    }

    if (msg.type === 'openGeneratedNode') {
        const node = figma.getNodeById(msg.data.id);

        if (!node) {
            const error = `Generated node is not found (id: ${msg.data.id})`;
            console.error(error);
            figma.notify(error, { error: true });
            return;
        }

        try {
            figma.currentPage.selection = [node as SceneNode];
            figma.ui.resize(PREVIEW_SIZES.width, PREVIEW_SIZES.height);
        } catch (error) {
            console.error((error as Error).message);
            figma.notify('Cannot open generated node', { error: true });
        }
    }

    if (msg.type === 'addParameter') {
        const selection = figma.currentPage.selection[0];
        const figmaNode = figma.getNodeById(msg.data.id);

        if (figmaNode) {
            const parametersIds = JSON.parse(figmaNode.getPluginData('parametersIds') || '[]');
            parametersIds.push(selection.id);
            figmaNode.setPluginData('parametersIds', JSON.stringify(parametersIds));
            selection.setPluginData('type', 'image');

            const parameters = getNodeParameters(msg.data.id);

            figma.ui.postMessage<FigmaCodeMessage>({
                type: 'renderParameters',
                data: { parameters },
            });
        }
    }

    if (msg.type === 'getParameters') {
        const parameters = getNodeParameters(msg.data.id);

        figma.ui.postMessage<FigmaCodeMessage>({
            type: 'renderParameters',
            data: { parameters },
        });
    }

    if (msg.type === 'removeParameter') {
        const node = figma.getNodeById(msg.data.parameterId);
        const parentNode = figma.getNodeById(msg.data.parentId);

        if (parentNode && node) {
            const parametersIds: string[] = JSON.parse(parentNode.getPluginData('parametersIds') || '[]');
            parentNode.setPluginData('parametersIds', JSON.stringify(parametersIds.filter(parameterId => parameterId !== msg.data.parameterId)));
            node.setPluginData('type', '');
            node.setPluginData('uploadUrl', '');

            const parameters = getNodeParameters(msg.data.parentId);

            figma.ui.postMessage<FigmaCodeMessage>({
                type: 'renderParameters',
                data: { parameters },
            });
        }
    }

    if (msg.type === 'saveToken') {
        try {
            await figma.clientStorage.setAsync('personalToken', msg.data.personalToken);
            figma.notify('Token saved');
            figma.ui.postMessage<FigmaCodeMessage>({
                type: 'checkPersonalToken',
                data: { isTokenSaved: true },
            });
            figma.ui.postMessage<FigmaCodeMessage>({
                type: 'setPluginUIScreen',
                data: { screen: 'main' },
            });
        } catch (err) {
            console.error(err);
            figma.notify('Token is not saved', { error: true });
        }
    }
};

const getGeneratedNodesIds = (): string[] => {
    const generatedNodesIds =
        JSON.parse(figma.currentPage.getPluginData('generatedNodesIds') || '[]')
            .filter((id: string) => Boolean(figma.getNodeById(id)));

    figma.currentPage.setPluginData('generatedNodesIds', JSON.stringify(generatedNodesIds));

    return generatedNodesIds;
};

const getGeneratedNodesData = (): GeneratedNode[] => {
    const generatedNodesIds: string[] = getGeneratedNodesIds();
    const generatedNodes: GeneratedNode[] = [];

    for (const id of generatedNodesIds) {
        const figmaNode = figma.getNodeById(id) as SceneNode;

        if (figmaNode) {
            generatedNodes.push({
                id,
                name: figmaNode.name,
                width: Math.ceil(figmaNode.width),
                height: Math.ceil(figmaNode.height),
                jsonString: figmaNode.getPluginData('jsonString'),
                parametersIds: JSON.parse(figmaNode.getPluginData('parametersIds') || '[]'),
            });
        }
    }

    return generatedNodes;
};

const getNodeParameters = (id: string): GeneratedNodeParameter[] => {
    const parameters: GeneratedNodeParameter[] = [];
    const figmaNode = figma.getNodeById(id);
    const parametersIds: string[] = JSON.parse(figmaNode?.getPluginData('parametersIds') || '[]');

    if (parametersIds?.length) {
        parametersIds.forEach(parameterId => {
            const childNode = figma.getNodeById(parameterId);

            if (childNode) {
                parameters.push({
                    id: childNode.id,
                    divkitType: childNode.getPluginData('type'),
                    nodeName: childNode.name,
                });
            }
        });
    }

    return parameters;
};

const supportsChildren = (node: BaseNode): node is NodeWithChildren => (
    node.type === 'FRAME' || node.type === 'INSTANCE' || node.type === 'GROUP' || node.type === 'COMPONENT'
);

const uploadAllImages = (node: NodeWithChildren, needUpdateImages: boolean) => {
    const imagesNodes: BaseNode[] = getChildren(
        node,
        node => {
            if (checkIsImageNode(node)) {
                if (needUpdateImages) {
                    node.setPluginData('uploadUrl', '');
                }

                return !node.getPluginData('uploadUrl');
            }
            return false;
        },
        false
    );

    if (process.env.__UPLOAD_IMAGE_URL) {
        return uploadAllImagesInternal(process.env.__UPLOAD_IMAGE_URL, imagesNodes);
    }

    return uploadAllImagesOnFigma(imagesNodes);
};

const uploadAllImagesInternal = async (uploadUrl: string, imagesNodes: BaseNode[]) => {
    if (imagesNodes.length) {
        try {
            const uploads = imagesNodes.map(async node => {
                const imageData = await (node as SceneNode).exportAsync({
                    format: 'PNG',
                    constraint: {
                        type: 'SCALE',
                        value: 2,
                    },
                });
                const res = await fetch(uploadUrl, {
                    method: 'POST',
                    body: JSON.stringify({
                        imageData: figma.base64Encode(imageData),
                        contentType: 'image/png',
                    }),
                    headers: {
                        'Content-Type': 'application/json',
                    },
                });

                if (!res.ok) {
                    const error = await res.text();
                    throw new Error(`${res.status}: ${error}`);
                }

                return res.json();
            });

            const responses = await Promise.all(uploads);

            for (let i = 0; i < responses.length; i++) {
                const response = responses[i];
                const imageNode = imagesNodes[i];
                const imageUrl = response.ok ? response.url : 'empty://';
                imageNode.setPluginData('uploadUrl', imageUrl);
            }
        } catch (error) {
            console.error(error);
            figma.notify((error as Error).message || 'Error in images uploading', { error: true });
            figma.ui.postMessage<FigmaCodeMessage>({
                type: 'generationError',
            });
        }
    }
};

const uploadAllImagesOnFigma = async (imagesNodes: BaseNode[]) => {
    const ids = imagesNodes.map(n => n.id).join(',');
    if (ids.length) {
        try {
            const personalToken = await figma.clientStorage.getAsync('personalToken');
            const res = await fetch(`https://api.figma.com/v1/images/${figma.fileKey}?ids=${ids}&scale=2&format=png`, {
                headers: {
                    'x-figma-token': personalToken,
                },
            });

            if (!res.ok) {
                const errorJson: { status: number, err: string } = await res.json();
                throw new Error(`${errorJson.status}: ${errorJson.err}`);
            }

            const data: { images: Record<string, string | null> } = await res.json();
            const images = data.images;

            for (const imageNode of imagesNodes) {
                imageNode.setPluginData('uploadUrl', images[imageNode.id] || 'empty://');
            }
        } catch (error) {
            console.error(error);
            figma.notify((error as Error).message || 'Error in images uploading', { error: true });
            figma.ui.postMessage<FigmaCodeMessage>({
                type: 'generationError',
            });
        }
    }
};

const buildDivLayoutJsonFrom = async (node: NodeWithChildren) => {
    palette = {
        dark: [],
        light: [],
    };
    variables = [];
    textCounter = 0;

    const item = makeContainerItem(node, true);

    const jsonObject: DivJson = {
        card: {
            variables,
            log_id: node.name,
            states: [
                {
                    state_id: 0,
                    div: item,
                },
            ],
        },
    };

    if (palette.light.length && palette.dark.length) {
        jsonObject.palette = palette;
    }

    const jsonString = JSON.stringify(jsonObject, null, 4);
    createOrUpdateGeneratedNode(node, jsonString);
};

const createOrUpdateGeneratedNode = (node: BaseNode, jsonString?: string) => {
    const id = node.id;
    const generatedNodesIds: string[] = getGeneratedNodesIds();
    let nodeIndex = generatedNodesIds.findIndex(x => x === id);

    if (nodeIndex === -1) {
        generatedNodesIds.push(id);
        nodeIndex = generatedNodesIds.length - 1;
    }

    node.setPluginData('jsonString', jsonString || '');
    figma.currentPage.setPluginData('generatedNodesIds', JSON.stringify(generatedNodesIds));

    const generatedNodes = getGeneratedNodesData();
    figma.ui.postMessage<FigmaCodeMessage>({
        type: 'renderGeneratedNodes',
        data: { generatedNodes },
    });

    const parameters = getNodeParameters(id);
    currentOpenedNode = id;
    figma.ui.postMessage<FigmaCodeMessage>({
        type: 'renderGeneratedNode',
        data: {
            node: generatedNodes[nodeIndex],
            parameters,
        },
    });
};

const makeContainerItem = (node: NodeWithChildren, isSelection?: boolean): DivContainerData => {
    const containerItem: DivContainerData = {
        type: 'container',
        items: [],
    };
    if (node.type === 'GROUP' || !node.visible) {
        return containerItem;
    }

    const items = makeContainerItems(node);

    if (!process.env.IS_PROD) {
        // For debug purposes
        containerItem.name = node.name;
    }

    if (node.fills !== figma.mixed && node.fillStyleId !== figma.mixed && node.fills.length > 0) {
        const background = makeBackgrounds(node.fills, node.fillStyleId, node.width, node.height);

        if (background.length) {
            containerItem.background = background;
        }
    }

    const border = makeBorders(node);

    if (Object.keys(border).length) {
        containerItem.border = border;
    }

    containerItem.height =
        (node.layoutMode === 'VERTICAL' && node.primaryAxisSizingMode === 'AUTO') ||
            (node.layoutMode === 'HORIZONTAL' && node.counterAxisSizingMode === 'AUTO') ?
            { type: 'wrap_content' } :
            { type: 'fixed', value: Math.round(node.height) };
    containerItem.width =
        (node.layoutMode === 'VERTICAL' && node.counterAxisSizingMode === 'AUTO') ||
            (node.layoutMode === 'HORIZONTAL' && node.primaryAxisSizingMode === 'AUTO') ?
            { type: 'wrap_content' } :
            { type: 'fixed', value: Math.round(node.width) };

    if (node.layoutMode === 'HORIZONTAL') {
        containerItem.orientation = 'horizontal';
        containerItem.content_alignment_horizontal = CONTENT_HORIZONTAL_ALIGNMENT[node.primaryAxisAlignItems];
        containerItem.content_alignment_vertical = CONTENT_VERTICAL_ALIGNMENT[node.counterAxisAlignItems];

        if (node.itemSpacing) {
            items.map((item, index) => {
                if (index) {
                    item.margins = { left: Math.round(node.itemSpacing) };
                }
            });
        }
    } else if (node.layoutMode === 'VERTICAL') {
        containerItem.orientation = 'vertical';
        containerItem.content_alignment_vertical = CONTENT_VERTICAL_ALIGNMENT[node.primaryAxisAlignItems];
        containerItem.content_alignment_horizontal = CONTENT_HORIZONTAL_ALIGNMENT[node.counterAxisAlignItems];

        if (node.itemSpacing) {
            items.map((item, index) => {
                if (index) {
                    item.margins = { top: Math.round(node.itemSpacing) };
                }
            });
        }
    } else {
        containerItem.orientation = 'overlap';

        if (node.layoutAlign !== 'STRETCH') {
            containerItem.content_alignment_vertical = 'center';
            containerItem.content_alignment_horizontal = 'center';
        }
    }

    const paddings: {
        top?: number;
        left?: number;
        bottom?: number;
        right?: number;
    } = {};

    if (node.paddingTop) {
        paddings.top = Math.round(node.paddingTop);
    }

    if (node.paddingBottom) {
        paddings.bottom = Math.round(node.paddingBottom);
    }

    if (node.paddingLeft) {
        paddings.left = Math.round(node.paddingLeft);
    }

    if (node.paddingRight) {
        paddings.right = Math.round(node.paddingRight);
    }

    if (Object.keys(paddings).length >= 1) {
        containerItem.paddings = paddings;
    }

    const layoutParams = getLayoutParams(node);

    if (isSelection && layoutParams.margins) {
        // delete fields for current generated node
        delete layoutParams.margins;
        delete layoutParams.alignment_horizontal;
        delete layoutParams.alignment_vertical;
    }
    const hasAbsoluteChildren = node.children.some(
        child => child.visible && 'layoutPositioning' in child && child.layoutPositioning === 'ABSOLUTE'
    );

    if (hasAbsoluteChildren) {
        const normalItems = items.filter(item => !item.alignment_vertical && !item.alignment_horizontal);
        const absoluteItems = items.filter(item => item.alignment_vertical || item.alignment_horizontal);
        containerItem.items = normalItems;
        return {
            type: 'container',
            orientation: 'overlap',
            items: [
                containerItem,
                ...absoluteItems,
            ],
            ...layoutParams,
        };
    }

    containerItem.items = items;

    return {
        ...containerItem,
        ...layoutParams,
    };
};

const makeContainerItems = (node: NodeWithChildren): DivBaseData[] => {
    const items: DivBaseData[] = [];
    for (const child of node.children) {
        if (!child.visible) { continue }

        if (checkIsImageNode(child)) {
            items.push(makeImageItem(child));
            continue;
        }

        switch (child.type) {
            case 'COMPONENT':
            case 'FRAME':
            case 'INSTANCE':
            case 'GROUP':
                if (shouldSkipContainer(child)) {
                    // Skip container and include only its children
                    items.push(...makeContainerItems(child));
                } else if (child.children.length !== 0) {
                    const item = makeContainerItem(child);
                    if (item) {
                        items.push(item);
                    }
                } else if (child.type !== 'GROUP') {
                    items.push(makeSeparatorItem(child));
                }
                break;
            case 'TEXT':
                items.push(makeTextItem(child));
                break;
            case 'RECTANGLE':
                items.push(makeSeparatorItem(child));
                break;
        }
    }
    return items;
};

const makeSeparatorItem = (node: FrameNode | InstanceNode | RectangleNode | ComponentNode): DivSeparatorData => {
    const separatorItem: DivSeparatorData = {
        type: 'separator',
    };

    if (node.type === 'RECTANGLE' && node.fills !== figma.mixed && node.fillStyleId !== figma.mixed) {
        separatorItem.background = makeBackgrounds(node.fills, node.fillStyleId, node.width, node.height);
        separatorItem.delimiter_style = { color: '#00000000' };
    } else if (node.fills !== figma.mixed && node.fills[0] && node.fills[0].type === 'SOLID') {
        separatorItem.delimiter_style = { color: rgbaComponentsToHex(node.fills[0].color, node.fills[0].opacity) };
    } else {
        separatorItem.alpha = 0;
    }

    separatorItem.height = { type: 'fixed', value: Math.round(node.height) };
    separatorItem.width = { type: 'fixed', value: Math.round(node.width) };

    const border = makeBorders(node);

    if (Object.keys(border).length) {
        separatorItem.border = border;
    }

    const layoutParams = getLayoutParams(node);

    return {
        ...separatorItem,
        ...layoutParams,
    };
};

const makeImageItem = (node: SceneNode): DivImageData => {
    const imageItem: DivImageData = {
        type: 'image',
        image_url: node.getPluginData('uploadUrl'),
    };
    const parent = node.parent as LayoutMixin;
    imageItem.height = (node.height < parent.height) ? { type: 'fixed', value: Math.round(node.height) } : { type: 'wrap_content' };
    imageItem.width = (node.width < parent.width) ? { type: 'fixed', value: Math.round(node.width) } : { type: 'match_parent' };

    if (supportsLayoutParams(node)) {
        const layoutParams = getLayoutParams(node);

        return {
            ...imageItem,
            ...layoutParams,
        };
    }

    return imageItem;
};

const makeTextStyles = (figmaStyle: StyledTextSegment, width: number, height: number): TextStyles => {
    const textStyles: TextStyles = {};
    textStyles.font_size = figmaStyle.fontSize;
    textStyles.font_weight = figmaStyle.fontName.style.toLowerCase();

    if (figmaStyle.lineHeight.unit === 'PERCENT') {
        textStyles.line_height = Math.round(figmaStyle.fontSize * figmaStyle.lineHeight.value / 100);
    } else if (figmaStyle.lineHeight.unit === 'PIXELS') {
        textStyles.line_height = Math.round(figmaStyle.lineHeight.value);
    }

    textStyles.letter_spacing = figmaStyle.letterSpacing.unit === 'PERCENT' ?
        Math.round(figmaStyle.fontSize * figmaStyle.letterSpacing.value / 100) :
        Math.round(figmaStyle.letterSpacing.value);

    const backgrounds = makeBackgrounds(figmaStyle.fills, figmaStyle.fillStyleId, width, height);

    if (backgrounds[0].type === 'solid') {
        textStyles.text_color = backgrounds[0].color;
    } else {
        textStyles.text_gradient = backgrounds[0];
    }

    return textStyles;
};

const makeTextItem = (textNode: TextNode): DivTextData => {
    const variableName = `text_${preprocessNodeName(textNode.name)}_${++textCounter}`;
    variables.push({
        name: variableName,
        value: textNode.characters,
        type: 'string',
    });
    let textItem: DivTextData = { type: 'text', text: `@{${variableName}}` };
    const styles = textNode.getStyledTextSegments([
        'fontSize',
        'fontName',
        'fills',
        'lineHeight',
        'letterSpacing',
        'fillStyleId',
        'textStyleId',
    ]);
    if (styles.length === 1) {
        textItem = {
            ...textItem,
            ...makeTextStyles(styles[0] as StyledTextSegment, textNode.width, textNode.height),
        };
    } else {
        textItem.ranges = styles.map(style => ({
            start: style.start,
            end: style.end,
            ...makeTextStyles(style as StyledTextSegment, textNode.width, textNode.height),
        }));
    }
    textItem.text_alignment_horizontal = textNode.textAlignHorizontal.toLowerCase();
    textItem.text_alignment_vertical = textNode.textAlignVertical.toLowerCase();

    const layoutParams = getLayoutParams(textNode);

    if (textNode.textAutoResize === 'HEIGHT') {
        textItem.width = { type: 'fixed', value: Math.round(textNode.width) };
        textItem.height = { type: 'wrap_content' };
    } else if (textNode.textAutoResize === 'WIDTH_AND_HEIGHT') {
        textItem.width = { type: 'wrap_content' };
        textItem.height = { type: 'wrap_content' };
    } else {
        textItem.width = { type: 'fixed', value: Math.round(textNode.width) };
        textItem.height = { type: 'fixed', value: Math.round(textNode.height) };
    }

    return {
        ...textItem,
        ...layoutParams,
    };
};

const addPaletteColors = (figmaColorName: string): string => {
    const divkitColorName = preprocessNodeName(figmaColorName);
    const lightColor = lightColors[figmaColorName].filter(it => it.visible)[0];
    const darkColor = darkColors[figmaColorName].filter(it => it.visible)[0];
    const isDayColorInPalette = palette.light.some(it => it.name === divkitColorName);
    const isNightColorInPalette = palette.dark.some(it => it.name === divkitColorName);

    if (!isDayColorInPalette && lightColor?.type === 'SOLID') {
        palette.light.push({
            name: divkitColorName,
            color: rgbaComponentsToHex(lightColor.color, lightColor.opacity),
        });
    }

    if (!isNightColorInPalette && darkColor?.type === 'SOLID') {
        palette.dark.push({
            name: divkitColorName,
            color: rgbaComponentsToHex(darkColor.color, darkColor.opacity),
        });
    }

    return divkitColorName;
};

const makeBackgrounds = (
    fills: readonly Paint[],
    styleId: string,
    shapeWidth: number,
    shapeHeight: number
): Background[] => {
    const slicedStyleId = styleId.slice(0, styleId.lastIndexOf(',') + 1);
    const figmaColorName = styleId ? figmaColorsMap[slicedStyleId] : '';
    if (figmaColorName) {
        const divkitColorName = addPaletteColors(figmaColorName);

        return [{
            type: 'solid',
            color: `@{${divkitColorName}}`,
        }];
    }

    const backgrounds: Background[] = [];
    for (const paint of fills) {
        if (!paint.visible) {
            continue;
        }

        switch (paint.type) {
            case 'SOLID': {
                const hexColor = rgbaComponentsToHex(paint.color, paint.opacity);

                backgrounds.push({
                    type: 'solid',
                    color: hexColor,
                });
                break;
            }
            case 'GRADIENT_LINEAR':
            case 'GRADIENT_ANGULAR': {
                const gradientParams = extractLinearGradientParamsFromTransform(
                    shapeWidth,
                    shapeHeight,
                    paint.gradientTransform
                );
                const x1 = gradientParams.start[0];
                const y1 = gradientParams.start[1];
                const x2 = gradientParams.end[0];
                const y2 = gradientParams.end[1];
                const angle = -Math.round((180 / Math.PI) * Math.atan2(y2 - y1, x2 - x1));
                backgrounds.push({
                    type: 'gradient',
                    colors: paint.gradientStops.map(
                        stop => rgbaComponentsToHex(stop.color, stop.color.a * (paint.opacity ?? 1))
                    ),
                    angle,
                });
                break;
            }
            case 'GRADIENT_DIAMOND':
            case 'GRADIENT_RADIAL': {
                const gradientParams = extractRadialOrDiamondGradientParams(
                    shapeWidth,
                    shapeHeight,
                    paint.gradientTransform
                );
                const angle = gradientParams.rotation * Math.PI / 180;
                const cos = Math.cos(angle);
                const sin = Math.sin(angle);
                const rx = gradientParams.radius[0] / shapeWidth;
                const ry = gradientParams.radius[1] / shapeHeight;
                const radiusX = Math.abs((rx * cos - ry * sin) * shapeWidth);
                const radiusY = Math.abs((rx * sin - ry * cos) * shapeHeight);
                const radius = Math.round((radiusX + radiusY) / 2);

                backgrounds.push({
                    type: 'radial_gradient',
                    colors: paint.gradientStops.map(
                        stop => rgbaComponentsToHex(stop.color, stop.color.a * (paint.opacity ?? 1))
                    ),
                    center_x: { type: 'fixed', value: Math.round(gradientParams.center[0]) },
                    center_y: { type: 'fixed', value: Math.round(gradientParams.center[1]) },
                    radius: { type: 'fixed', value: radius },
                });
                break;
            }
        }
    }
    return backgrounds;
};

const makeBorders = (node: FrameNode | InstanceNode | ComponentNode | RectangleNode): Border => {
    const border: Border = {};

    if (node.topLeftRadius || node.topRightRadius || node.bottomLeftRadius || node.bottomRightRadius) {
        const cornersRadius: CornersRadius = {};

        if (node.topLeftRadius) {
            cornersRadius['top-left'] = Math.round(node.topLeftRadius);
        }

        if (node.topRightRadius) {
            cornersRadius['top-right'] = Math.round(node.topRightRadius);
        }

        if (node.bottomLeftRadius) {
            cornersRadius['bottom-left'] = Math.round(node.bottomLeftRadius);
        }

        if (node.bottomRightRadius) {
            cornersRadius['bottom-right'] = Math.round(node.bottomRightRadius);
        }

        border.corners_radius = cornersRadius;
    } else if (node.cornerRadius !== figma.mixed && node.cornerRadius > 0) {
        border.corner_radius = Math.round(node.cornerRadius);
    }

    if (node.strokes.length > 0 && typeof node.strokeWeight === 'number' && node.strokes[0].type === 'SOLID') {
        border.stroke = {
            color: rgbaComponentsToHex(node.strokes[0].color, node.strokes[0].opacity),
            width: Math.round(node.strokeWeight),
        };
    }

    for (const effect of node.effects) {
        if (effect.type === 'DROP_SHADOW' && effect.visible && effect.color) {
            border.has_shadow = 1;
            border.shadow = {
                offset: {
                    x: {
                        value: Math.round(effect.offset.x),
                    },
                    y: {
                        value: Math.round(effect.offset.y),
                    },
                },
                blur: Math.round(effect.radius),
                color: rgbaComponentsToHex(effect.color),
                alpha: effect.color.a,
            };
            break;
        }
    }

    return border;
};

initPlugin();
