import { derived, get, writable, type Writable } from 'svelte/store';
import type { DivJson, VariableTrigger } from '@divkitframework/divkit/typings/common';
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
//@ts-ignore
import { parseExpression, walkExpression } from '@divkitframework/divkit/client-devtool';
import type { PaletteItem } from './palette';
import { parseVariableValue, type JsonVariable, type Variable } from './customVariables';
import { type JsonTimer, type Timer } from './timers';
import type { ActionDesc, EditorError, FileLimits, GetTranslationKey, Locale, SafeAreaEmulation, Source, TankerMeta, ValueFilters } from '../../lib';
import type { TreeLeaf } from '../ctx/tree';
import type { BaseCommand } from './commands/base';
import { namedTemplates } from './templates';
import { paletteToDivjson } from './docToDivjson';
import { getTankerKeyRaw, tankerKeyToVariableName } from '../utils/tanker';
import { EMPTY_IMAGE, stringifyObjectAndStoreRanges } from './doc';
import { findLeaf, walk } from '../utils/tree';
import { isSimpleElement } from './schema';
import templateIcon from '../../assets/components/template.svg?url';
import { componentIcon } from '../utils/componentIcon';
import { isUserTemplateWithoutChilds, parseItems } from './userTemplates';
import { calcComponentTree, type HighlightMode } from './structure';
import type { Loc, Range } from '../utils/stringifyWithLoc';
import type { ViewerError } from '../utils/errors';
import { isViewerError, isViewerWarning } from './rendererErrors';
import { AddLeafCommand } from './commands/addLeaf';
import { RemoveLeafCommand } from './commands/removeLeaf';
import { l10nGetter } from './l10n';
import { calcFileSizeMod, getFileSize } from '../utils/fileSize';

export class State {
    palette = writable<PaletteItem[]>([]);
    customVariables = writable<Variable[]>([]);
    variableTriggers = writable<VariableTrigger[]>();
    timers = writable<Timer[]>([]);
    tanker = writable<TankerMeta>({});
    rootLogId = writable('div2_sample_card');
    tree = writable<TreeLeaf>({
        id: 'null',
        childs: [],
        props: {
            json: {}
        }
    });
    #commands = writable<BaseCommand[]>([]);
    #historyIndex = writable(-1);
    currentUndoStore = derived([this.#commands, this.#historyIndex], ([history, historyIndex]) => {
        return history[historyIndex];
    });
    currentRedoStore = derived([this.#commands, this.#historyIndex], ([history, historyIndex]) => {
        return history[historyIndex + 1];
    });
    sources = writable<Source[]>([]);
    #tankerToVarMap = new Map<string, string>();
    tankerKeyToNodes = writable(new Map<string, Set<string>>());
    varToTankerMap = new Map<string, string>();
    #sourcesKeyToNodes = writable(new Map<string, Set<string>>());
    userTemplates = new Map<string, Record<string, unknown>>();
    #userTemplatesStore = writable(this.userTemplates);
    userDefinedTemplates = derived([this.#userTemplatesStore], ([templates]) => {
        return Array.from(templates.keys()).filter(it => !namedTemplates[it]);
    });
    paletteEnabled = writable(true);

    #paletteCounter = 0;
    #variableCounter = 0;
    #timerCounter = 0;
    #genCounter = 0;

    copiedLeaf = writable<TreeLeaf | null>(null);

    selectedLeaf = writable<TreeLeaf | null>(null);
    selectedElem = writable<HTMLElement | null>(null);

    highlightLeaf = writable<TreeLeaf[] | null>(null);
    highlightElem = writable<HTMLElement[] | null>(null);
    highlightMode = writable<HighlightMode>('');
    highlightGradientAngle = writable(0);
    highlightRanges = writable<Range[] | null>(null);
    selectedRanges = writable<Range[] | null>(null);
    highlightLoc = writable<Loc | null>(null);

    #updateScheduled = false;
    #webStructureMap: Map<HTMLElement, string> | null = null;
    simpleComponentsMap: Map<HTMLElement, TreeLeaf> | null = null;

    rendererErrors = writable<Record<string, ViewerError[]>>({});
    fileSizeErrors = writable<Record<string, ViewerError[]>>({});
    totalErrors = derived([this.rendererErrors, this.fileSizeErrors], ([renderer, fileSize]) => {
        const result: Record<string, ViewerError[]> = {};

        for (const key in renderer) {
            result[key] = [...renderer[key], ...(fileSize[key] || [])];
        }
        for (const key in fileSize) {
            if (renderer.hasOwnProperty(key)) {
                continue;
            }
            result[key] = [...(renderer[key] || []), ...fileSize[key]];
        }

        return result;
    });

    rendererErrorsOnly = derived(this.totalErrors, store => {
        const res: ViewerError[] = [];

        Object.keys(store).map(key => {
            res.push(...store[key].filter(isViewerError));
        });

        return res;
    });
    rendererWarningsOnly = derived(this.totalErrors, store => {
        const res: ViewerError[] = [];

        Object.keys(store).map(key => {
            res.push(...store[key].filter(isViewerWarning));
        });

        return res;
    });

    locale = writable('');

    customActions = writable<ActionDesc[]>([]);
    previewThemeStore = writable<'light' | 'dark'>('light');
    readOnly = writable<boolean>(false);
    themeStore = writable<'light' | 'dark'>('light');

    divjsonStore = derived(this.tree, root => {
        const {
            shortObject,
            mapJsonToLeaf
        } = this.treeToShortObject(root);

        const usedTemplates = new Set<string>();
        walk(root, leaf => {
            const type = leaf.props.json.type;
            usedTemplates.add(type);
        });

        const fullObject = this.toFullDivjson(usedTemplates, shortObject);

        return {
            object: fullObject,
            fullString: stringifyObjectAndStoreRanges({ object: fullObject, mapJsonToLeaf })
        };
    });

    direction = writable<'ltr' | 'rtl'>('ltr');

    safeAreaEmulation?: SafeAreaEmulation;
    safeAreaEmulationEnabled = writable(false);

    valueFilters?: ValueFilters;

    private getTranslationKey: GetTranslationKey | undefined;

    lang: Writable<Locale> = writable('en');
    l10n = derived(this.lang, lang => {
        return (key: string, overrideLang?: string) => {
            return l10nGetter(overrideLang || lang, key);
        };
    });
    l10nString = derived(this.lang, lang => {
        return (key: string, overrideLang?: string) => {
            const val = l10nGetter(overrideLang || lang, key);

            if (typeof val !== 'string') {
                // todo expose error
                console.error(`Missing translation for key "${key}"`);
                return '';
            }

            return val;
        };
    });

    fitViewportOnCreate?: boolean;

    fileLimits: FileLimits | undefined;

    isGlobalCheckRequired = false;

    constructor({
        locale,
        fileLimits,
        getTranslationKey
    }: {
        locale: Locale;
        fileLimits: FileLimits | undefined;
        getTranslationKey: GetTranslationKey | undefined;
    }) {
        this.lang.set(locale);
        this.fileLimits = fileLimits;

        this.getTranslationKey = getTranslationKey;

        this.selectedLeaf.subscribe(leaf => {
            this.selectedElem.set(leaf?.props.node || null);

            const range = leaf?.props.range as Range;

            if (range) {
                this.highlightLoc.set(range.start);
                this.selectedRanges.set([range]);
            } else {
                this.highlightLoc.set(null);
                this.selectedRanges.set(null);
            }

            this.refreshGlobalChecks();
        });

        this.tree.subscribe(root => {
            const prevSelected = get(this.selectedLeaf);
            if (prevSelected) {
                const newSelected = findLeaf(root, prevSelected.id);
                if (!newSelected) {
                    this.selectedLeaf.set(null);
                }
            }

            const actualLeafs = new Set<string>();
            walk(root, leaf => {
                actualLeafs.add(leaf.id);
            });

            let errorsStore = get(this.rendererErrors);
            let recreate = false;
            for (const leafId in errorsStore) {
                if (!actualLeafs.has(leafId)) {
                    if (!recreate) {
                        recreate = true;
                        errorsStore = { ...errorsStore };
                    }
                    delete errorsStore[leafId];
                }
            }
            if (recreate) {
                this.rendererErrors.set(errorsStore);
            }
        });
    }

    treeToShortObject(leaf: TreeLeaf) {
        const mapJsonToLeaf = new Map();

        const nodeToDivjson = (leaf: TreeLeaf) => {
            const newJson = leaf.props.json;
            const json = {
                ...newJson,
                __leafId: leaf.id
            };

            // todo support tabs and others
            const baseType = this.getBaseType(leaf.props.json?.type);
            let fieldName = leaf.props.fromDataField;
            if (baseType === 'state') {
                fieldName ||= 'states';
                json[fieldName] = leaf.childs?.map((it, index) => {
                    return {
                        state_id: it.props.info?.state_id || index,
                        div: nodeToDivjson(it),
                        ...(it.props.info?.__key !== undefined ? {
                            __key: it.props.info.__key
                        } : undefined)
                    };
                });
            } else if (baseType === 'tabs') {
                fieldName ||= 'items';
                json[fieldName] = leaf.childs?.map(it => {
                    return {
                        title: it.props.info?.title || 'title',
                        title_click_action: it.props.info?.title_click_action,
                        div: nodeToDivjson(it)
                    };
                });
            } else if (leaf.childs?.length) {
                fieldName ||= 'items';
                json[fieldName] = leaf.childs.map(nodeToDivjson);
            } else {
                delete json.items;
            }

            mapJsonToLeaf.set(json, leaf);

            return json;
        };

        const shortObject = {
            card: {
                log_id: get(this.rootLogId),
                states: [{
                    state_id: 0,
                    div: nodeToDivjson(leaf)
                }]
            }
        };

        return {
            shortObject,
            mapJsonToLeaf
        };
    }

    extractVariables(variables: JsonVariable[], sources?: Source[]): Variable[] {
        const vars: Variable[] = [];
        const knownSources = new Set<string>();

        if (sources) {
            sources.forEach(source => {
                knownSources.add(source.key);
            });
        }

        for (const variable of variables) {
            const { name } = variable;
            if (
                !name ||
                !variable.type ||
                name === 'local_palette' ||
                name.startsWith('tanker_') ||
                knownSources.has(name)
            ) {
                continue;
            }

            vars.push({
                id: this.genVariableId(),
                name,
                type: variable.type,
                value: typeof variable.value === 'string' ? variable.value : JSON.stringify(variable.value)
            });
        }

        return vars;
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    divToLeaf(div: any, parent?: TreeLeaf, info?: unknown): TreeLeaf {
        const type = div.type;
        let childs;
        let infos = [];
        let fromDataField;

        if (type in namedTemplates) {
            childs = [];
        } else {
            ({
                childs,
                infos,
                fromDataField
            } = parseItems(this, div));
        }

        const node: TreeLeaf = {
            id: this.genId(),
            props: {
                json: div
            },
            parent,
            childs: []
        };

        if (info) {
            node.props.info = info;
        }
        if (fromDataField) {
            node.props.fromDataField = fromDataField;
        }

        node.childs = childs.map(((it, index) => this.divToLeaf(it, node, infos[index])));

        return node;
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    setDivJson(json: any): void {
        if (!json) {
            throw new Error('Missing json');
        }

        if (!json.card?.states?.[0]?.div) {
            throw new Error('Incorrect format');
        }

        this.userTemplates.clear();
        if (json.templates && typeof json.templates === 'object') {
            for (const key in json.templates) {
                this.userTemplates.set(key, json.templates[key]);
                this.#userTemplatesStore.set(this.userTemplates);
            }
        }

        if (Array.isArray(json.card?.variables)) {
            this.customVariables.set(this.extractVariables(json.card.variables, get(this.sources)));

            json.card.variables.forEach((variable: JsonVariable) => {
                if (variable.name === 'local_palette' && variable.value && typeof variable.value === 'object') {
                    const newPalette: PaletteItem[] = [];

                    for (const id in variable.value) {
                        // eslint-disable-next-line @typescript-eslint/no-explicit-any
                        const obj = (variable.value as any)[id];
                        if (
                            obj &&
                            typeof obj === 'object' &&
                            typeof obj.name === 'string' &&
                            typeof obj.light === 'string' &&
                            typeof obj.dark === 'string'
                        ) {
                            newPalette.push({
                                id,
                                name: obj.name,
                                light: obj.light,
                                dark: obj.dark
                            });
                        }
                    }

                    this.palette.set(newPalette);
                }
            });
        } else {
            this.customVariables.set([]);
        }

        this.variableTriggers.set(Array.isArray(json.card?.variable_triggers) ? json.card.variable_triggers : []);

        if (Array.isArray(json.card?.timers)) {
            this.timers.set((json.card.timers as JsonTimer[]).filter(it => it.id).map(it => {
                return {
                    ...it,
                    __id: this.genTimerId()
                };
            }));
        } else {
            this.timers.set([]);
        }

        this.tree.set(this.divToLeaf(json.card.states[0].div));

        this.isGlobalCheckRequired = true;

        this.awaitMissingTankerKeys(json).then(res => {
            if (res) {
                this.tree.set(get(this.tree));
            }
        });
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    private async awaitMissingTankerKeys(json: any): Promise<boolean> {
        const awaitVars: string[] = [];

        walk(get(this.tree), leaf => {
            const text = leaf.props.json.text;
            const rawKey = text && getTankerKeyRaw(text);
            const translation = text && this.getTankerKey(text);

            if (rawKey && !translation) {
                awaitVars.push(rawKey);
            }
        });

        if (awaitVars.length && Array.isArray(json?.card?.variables)) {
            const awaitKeys = awaitVars
                .map(varName => {
                    return json.card.variables
                        .find((it?: JsonVariable) => it?.name === varName)
                        ?.value
                        .replace('#{tanker/', '')
                        .replace('}', '');
                })
                .filter(Boolean);

            if (awaitKeys.length) {
                const store: TankerMeta = {};
                for (const key of awaitKeys) {
                    try {
                        const translations = await this.getTranslationKey?.(key);
                        // eslint-disable-next-line max-depth
                        if (translations) {
                            store[key] = translations;
                            this.storeTankerKey(key);
                        }
                    } catch (_err) {
                        // do nothing
                    }
                }

                this.tanker.set({
                    ...get(this.tanker),
                    ...store
                });
                this.refreshGlobalChecks();

                return true;
            }
        }

        return false;
    }

    toFullDivjson(usedTemplates: Set<string>, json: Record<string, unknown>): DivJson {
        const templates: Record<string, unknown> = {};
        for (const [key, value] of this.userTemplates) {
            templates[key] = value;
        }
        for (const key in namedTemplates) {
            if (!this.userTemplates.has(key) && usedTemplates.has(key)) {
                templates[key] = namedTemplates[key].template;
            }
        }

        const variables = [];

        const paletteVal = get(this.palette);
        if (get(this.paletteEnabled)) {
            const value = paletteToDivjson(paletteVal);
            if (Object.keys(value).length) {
                variables.push({
                    type: 'dict',
                    name: 'local_palette',
                    value
                });
            }
        }
        for (const [tankerKey] of get(this.tankerKeyToNodes)) {
            variables.push({
                type: 'string',
                name: tankerKeyToVariableName(tankerKey),
                value: `#{tanker/${tankerKey}}`
            });
        }

        const sourcesMap = new Map();
        for (const source of get(this.sources)) {
            sourcesMap.set(source.key, source);
        }
        for (const [sourceKey] of get(this.#sourcesKeyToNodes)) {
            if (sourcesMap.has(sourceKey)) {
                variables.push({
                    type: 'dict',
                    name: sourceKey,
                    value: sourcesMap.get(sourceKey).example
                });
            }
        }

        for (const customVariable of get(this.customVariables)) {
            try {
                const value = parseVariableValue(customVariable);
                const { type, name } = customVariable;

                variables.push({
                    type,
                    name,
                    value
                });
            } catch (err) {
                // continue
            }
        }

        const card: Record<string, unknown> = (json.card || (json.card = {})) as Record<string, unknown>;

        if (variables.length) {
            card.variables = variables;
        } else {
            delete card.variables;
        }

        const triggers = get(this.variableTriggers);
        if (triggers.length) {
            card.variable_triggers = triggers;
        } else {
            delete card.variable_triggers;
        }

        const timersList = get(this.timers);
        if (timersList.length) {
            card.timers = timersList.map(it => {
                const {
                    id,
                    duration,
                    tick_interval,
                    value_variable,
                    tick_actions,
                    end_actions
                } = it;

                return {
                    id,
                    duration,
                    tick_interval,
                    value_variable,
                    tick_actions,
                    end_actions
                };
            });
        } else {
            delete card.timers;
        }

        return {
            ...json,
            templates
        } as DivJson;
    }

    genPaletteId(): string {
        let newId: string;

        const current = new Set(get(this.palette).map(it => it.id));

        do {
            newId = `color${this.#paletteCounter++}`;
        } while (current.has(newId));

        return newId;
    }

    genVariableId(): string {
        return `var${this.#variableCounter++}`;
    }

    genTimerId(): string {
        return `timer${this.#timerCounter++}`;
    }

    storeTankerKey(key: string): void {
        const variableName = tankerKeyToVariableName(key);

        if (!this.#tankerToVarMap.has(key)) {
            this.#tankerToVarMap.set(key, variableName);
        }
        if (!this.varToTankerMap.has(variableName)) {
            this.varToTankerMap.set(variableName, key);
        }
    }

    getTankerKey(val: string): string {
        const variableName = getTankerKeyRaw(val);

        return variableName && this.varToTankerMap.get(variableName) || '';
    }

    updateUsages() {
        this.updateTankerMap();
        this.updateSourcesMap();
    }

    updateTankerMap(): void {
        const map = get(this.tankerKeyToNodes);
        map.clear();
        walk(get(this.tree), leaf => {
            const text = leaf.props.json.text;
            const key = text && this.getTankerKey(text);

            if (key) {
                const set = map.get(key) || new Set();
                map.set(key, set);
                set.add(leaf.id);
            }
        });
        this.tankerKeyToNodes.set(map);
    }

    updateSourcesMap(): void {
        const map = get(this.#sourcesKeyToNodes);
        map.clear();
        walk(get(this.tree), leaf => {
            for (const prop in leaf.props.json) {
                const val = leaf.props.json[prop];
                if (typeof val === 'string' && val.includes('@{')) {
                    try {
                        const ast = parseExpression(val, {
                            type: 'json'
                        });

                        walkExpression(ast, {
                            // eslint-disable-next-line @typescript-eslint/no-explicit-any
                            Variable(node: any) {
                                const key = node.id.name;
                                const set = map.get(key) || new Set();
                                map.set(key, set);
                            }
                        });
                    } catch (_err) {
                        // do nothing
                    }
                }
            }
        });
        this.#sourcesKeyToNodes.set(map);
    }

    genNewLeaf(type: string, isCentered = false): TreeLeaf {
        let json;

        if (type === 'text') {
            json = {
                type,
                text: 'text',
                font_size: 20
            };
        } else if (type === 'image') {
            json = {
                type,
                image_url: EMPTY_IMAGE,
                width: {
                    type: 'fixed',
                    value: 100
                },
                height: {
                    type: 'fixed',
                    value: 100
                },
                preload_required: true
            };
        } else if (type === 'gif') {
            json = {
                type,
                gif_url: EMPTY_IMAGE,
                width: {
                    type: 'fixed',
                    value: 100
                },
                height: {
                    type: 'fixed',
                    value: 100
                },
                preload_required: true
            };
        } else if (type === 'video') {
            json = {
                type,
                preload_required: true
            };
        } else if (type === 'separator') {
            json = {
                type,
                width: {
                    type: 'match_parent'
                },
                height: {
                    type: 'fixed',
                    value: 10
                }
            };
        } else if (type === 'container') {
            json = {
                type,
                width: {
                    type: 'match_parent'
                },
                height: {
                    type: 'fixed',
                    value: 200
                }
            };
        } else if (type === 'gallery') {
            json = {
                type,
                width: {
                    type: 'match_parent'
                },
                height: {
                    type: 'fixed',
                    value: 200
                }
            };
        } else if (type in namedTemplates && namedTemplates[type].newNode) {
            json = { ...namedTemplates[type].newNode, type };
        } else {
            json = { type };
        }

        const leaf: TreeLeaf = {
            id: this.genId(),
            parent: undefined,
            childs: [],
            props: {
                json
            }
        };

        if (!isSimpleElement(type) && !(type in namedTemplates) && !this.userTemplates.has(type) && type !== 'custom') {
            const child: TreeLeaf = {
                id: this.genId(),
                parent: leaf,
                childs: [],
                props: {
                    info: type === 'state' ? {
                        state_id: 'default'
                    } : undefined,
                    json: {
                        type: 'text',
                        text: 'text'
                        // eslint-disable-next-line @typescript-eslint/no-explicit-any
                    } as any
                }
            };

            if (type === 'gallery' || type === 'container') {
                child.props.json.width = {
                    type: 'fixed',
                    value: 100
                };
            }

            leaf.childs.push(child);
        }

        if (!leaf.props.json.width) {
            leaf.props.json.width = {
                type: 'wrap_content',
                constrained: true
            };
        }

        if (isCentered) {
            leaf.props.json.alignment_horizontal = 'center';
            leaf.props.json.alignment_vertical = 'center';
        }

        return leaf;
    }

    getChild(childId: string, isCentered = false): TreeLeaf | undefined {
        if (childId.startsWith('_new')) {
            const type = childId.split(':')[1];
            return this.genNewLeaf(type, isCentered);
        }

        return findLeaf(get(this.tree), childId);
    }

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    componentIcon(type: string, json?: any): string {
        if (this.userTemplates.has(type) && !(type in namedTemplates)) {
            return templateIcon;
        }

        return componentIcon(type, json);
    }

    getBaseType(type: string | null): string | null {
        if (!type) {
            return type;
        }

        let base: unknown = type;
        while (typeof base === 'string' && this.userTemplates.has(base)) {
            base = this.userTemplates.get(base)?.type;
        }
        return typeof base === 'string' && base || type;
    }

    private refreshGlobalChecks(): void {
        this.updateUsages();

        const fileSizeErrors: Record<string, ViewerError[]> = {};
        const promises: Promise<void>[] = [];
        walk(get(this.tree), leaf => {
            const evalledJson = leaf.props.evalledJson;
            if (!evalledJson || evalledJson.type !== 'gif' && evalledJson.type !== 'image' && evalledJson.type !== 'video') {
                return;
            }

            const check = (value: unknown, fileType: keyof FileLimits) => {
                if (!value || typeof value !== 'string') {
                    return;
                }

                promises.push(getFileSize(value, fileType).then(currentSize => {
                    const level = calcFileSizeMod(currentSize, fileType, Infinity, Infinity, this.fileLimits);

                    if (level) {
                        fileSizeErrors[leaf.id] ||= [];
                        fileSizeErrors[leaf.id].push({
                            level,
                            message: get(this.l10nString)('file.too_big'),
                            stack: [],
                            args: {
                                leafId: leaf.id
                            }
                        });
                    }
                }));
            };

            if (evalledJson.type === 'image') {
                check(evalledJson.image_url, 'image');
                check(evalledJson.preview, 'preview');
            } else if (evalledJson.type === 'gif') {
                check(evalledJson.gif_url, 'image');
                check(evalledJson.preview, 'preview');

                if (evalledJson.extensions) {
                    for (const key in evalledJson.extensions) {
                        // eslint-disable-next-line @typescript-eslint/no-explicit-any
                        const ext: any = evalledJson.extensions[key];
                        if (ext?.id === 'lottie') {
                            check(ext.params?.lottie_url, 'lottie');
                        }
                    }
                }
            } else if (evalledJson.type === 'video') {
                check(evalledJson.preview, 'preview');

                if (evalledJson.video_sources) {
                    for (const key in evalledJson.video_sources) {
                        // eslint-disable-next-line @typescript-eslint/no-explicit-any
                        const source: any = evalledJson.video_sources[key];
                        check(source.url, 'video');
                    }
                }
            }
        });

        Promise.all(promises).then(() => {
            this.fileSizeErrors.set(fileSizeErrors);
        });
    }

    pushCommand(command: BaseCommand): void {
        const history = get(this.#commands);
        const historyIndex = get(this.#historyIndex);
        command.redo(this);

        const prevCommand = history[historyIndex];
        if (prevCommand && prevCommand.canMerge(command)) {
            prevCommand.mergeMeWith(command);
            const newHistory = history.slice(0, historyIndex);
            newHistory.push(prevCommand);
            this.#commands.set(newHistory);
            this.#historyIndex.set(newHistory.length - 1);
        } else {
            const newHistory = history.slice(0, historyIndex + 1);
            newHistory.push(command);
            this.#commands.set(newHistory);
            this.#historyIndex.set(newHistory.length - 1);
        }

        this.tree.set(get(this.tree));
        this.isGlobalCheckRequired = true;
    }

    undo(): void {
        if (!get(this.currentUndoStore)) {
            return;
        }

        const history = get(this.#commands);
        const historyIndex = get(this.#historyIndex);

        const command = history[historyIndex];
        command.undo(this);

        this.#historyIndex.set(historyIndex - 1);

        this.tree.set(get(this.tree));
        this.isGlobalCheckRequired = true;
    }

    redo(): void {
        if (!get(this.currentRedoStore)) {
            return;
        }

        const history = get(this.#commands);
        const historyIndex = get(this.#historyIndex);

        const command = history[historyIndex + 1];
        command.redo(this);

        this.#historyIndex.set(historyIndex + 1);

        this.tree.set(get(this.tree));
        this.isGlobalCheckRequired = true;
    }

    pasteLeaf(source: TreeLeaf, into: TreeLeaf, insertIndex?: number): void {
        const baseType = this.getBaseType(into.props.json.type);
        const canHaveChilds = baseType &&
            !isSimpleElement(baseType) &&
            !isUserTemplateWithoutChilds(this, into.props.json) &&
            !namedTemplates[into.props.json.type];

        const target = canHaveChilds ? into : into.parent;

        if (!target) {
            return;
        }

        const copy = this.duplicateLeaf(source, target);
        const root = get(this.tree);

        if (target?.props.processedJson?.orientation === 'overlap') {
            copy.props.json.margins ||= {};
            copy.props.json.margins.top = (copy.props.json.margins.top || 0) + 10;
            copy.props.json.margins.left = (copy.props.json.margins.left || 0) + 10;
        }

        if (insertIndex === undefined) {
            if (target === into.parent && into.parent) {
                const index = into.parent.childs.indexOf(into);
                insertIndex = index + 1;
            } else {
                insertIndex = target.childs.length;
            }
        }

        this.pushCommand(new AddLeafCommand({
            parentId: target.id,
            insertIndex,
            leaf: copy
        }));

        this.selectedLeaf.set(findLeaf(root, copy.id) || null);
    }

    deleteLeaf(leaf: TreeLeaf): void {
        this.pushCommand(new RemoveLeafCommand(leaf.id));
        this.highlightLeaf.set(null);
        this.highlightElem.set(null);
        this.highlightRanges.set(null);
        const selected = get(this.selectedLeaf);
        if (selected === leaf) {
            this.selectedLeaf.set(null);
            this.selectedElem.set(null);
        }
    }

    #scheduleUpdate(func: () => void): void {
        if (this.#updateScheduled) {
            return;
        }
        this.#updateScheduled = true;
        Promise.resolve().then(() => {
            this.#updateScheduled = false;
            func();
        });
    }

    updateComponents(): void {
        this.#scheduleUpdate(() => {
            const res = calcComponentTree(get(this.tree));
            this.#webStructureMap = res.map;
            this.simpleComponentsMap = res.simpleMap;
        });
    }

    getLeafFromNode(node: HTMLElement): TreeLeaf | undefined {
        const leafId = this.#webStructureMap?.get(node);
        if (!leafId) {
            return;
        }

        return findLeaf(get(this.tree), leafId);
    }

    genId(): string {
        return `id${this.#genCounter++}`;
    }

    currentGenId(): number {
        return this.#genCounter;
    }

    restoreGenId(id: number): void {
        this.#genCounter = id;
    }

    duplicateLeaf(leaf: TreeLeaf, parent: TreeLeaf | undefined = undefined): TreeLeaf {
        const copy: TreeLeaf = {
            id: this.genId(),
            props: { ...leaf.props },
            parent,
            childs: []
        };

        delete copy.props.node;
        delete copy.props.range;
        delete copy.props.processedJson;
        copy.props.json = JSON.parse(JSON.stringify(copy.props.json));

        leaf.childs.forEach(child => {
            copy.childs.push(this.duplicateLeaf(child, copy));
        });

        return copy;
    }

    findBestLeaf(offset: number): TreeLeaf | undefined {
        const root = get(this.tree);
        if (!root) {
            return;
        }

        let bestMatch: {
            len: number;
            leaf: TreeLeaf;
        } | undefined;
        walk(root, leaf => {
            const range = leaf.props.range as Range;
            // +1 means comma at the end
            if (range && range.start.offset <= offset && offset <= range.end.offset + 1) {
                const len = range.end.offset - range.start.offset;
                if (!bestMatch || bestMatch.len > len) {
                    bestMatch = {
                        len,
                        leaf
                    };
                }
            }
        });

        if (bestMatch) {
            return bestMatch.leaf;
        }
    }

    getEditorErrors(): EditorError[] {
        const res: EditorError[] = [];
        const totalErrors = get(this.totalErrors);

        for (const key in totalErrors) {
            res.push(...totalErrors[key].map(it => {
                return {
                    message: it.message,
                    level: it.level || 'error'
                };
            }));
        }

        return res;
    }

    onRender(): void {
        if (this.isGlobalCheckRequired) {
            this.isGlobalCheckRequired = false;
            this.refreshGlobalChecks();
        }
    }
}
