<script lang="ts">
    import { getContext } from 'svelte';
    import { namedTemplates } from '../../data/templates';
    import { BASE_COMPONENT_PROPS, COMPONENT_PROPS, ROOT_PROPS, type ComponentProperty, type SiblingComponentProperty } from '../../data/componentProps';
    import SimplePropsList from './SimplePropsList.svelte';
    import { SetPropertyCommand, type SetPropertyItem } from '../../data/commands/setProperty';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import { getTemplateProps } from '../../data/userTemplates';

    const { rootConfigurable, state, rendererApi } = getContext<AppContext>(APP_CTX);
    const { selectedLeaf, readOnly, tree } = state;

    let templateProps: ComponentProperty | undefined;

    $: json = $selectedLeaf?.props.json || null;
    $: processedJson = $selectedLeaf?.props.processedJson || json || null;
    $: parentProcessedJson = $selectedLeaf?.parent?.props.processedJson ||
        $selectedLeaf?.parent?.props.json ||
        null;
    $: evalJson = processedJson && rendererApi().evalJson(processedJson) || null;
    $: parentEvalJson = parentProcessedJson && rendererApi().evalJson(parentProcessedJson) || null;
    $: type = json?.type;
    $: baseType = state.getBaseType(type);
    $: list = !rootConfigurable && $selectedLeaf === $tree && ROOT_PROPS ||
        namedTemplates[type]?.props && [...BASE_COMPONENT_PROPS, ...namedTemplates[type]?.props] ||
        baseType && COMPONENT_PROPS[baseType] ||
        BASE_COMPONENT_PROPS;

    $: {
        const props = type in namedTemplates ? [] : getTemplateProps(state, type);

        templateProps = undefined;

        const mapped: ComponentProperty[] = props.map(it => {
            // todo
            const enableSources = it.editor.type !== 'background2' &&
                it.editor.type !== 'actions2' &&
                it.editor.type !== 'video_sources' &&
                it.editor.type !== 'variable-name';

            return {
                rawName: it.templatePropertyName,
                prop: it.templatePropertyName,
                type: it.editor.type,
                options: it.editor.options,
                constraint: it.editor.constraint,
                enableSources
            } as ComponentProperty; // todo
        });

        if (mapped.length) {
            templateProps = {
                type: 'group',
                rawTitle: type,
                list: mapped
            };
        }
    }

    $: selectedElemProps = json && rendererApi().selectedElemProps() || null;

    async function onChange(event: CustomEvent<{
        value: unknown;
        item: ComponentProperty | SiblingComponentProperty;
    } | {
        values: {
            prop: string;
            value: unknown;
        }[];
        item: ComponentProperty | SiblingComponentProperty;
    }>) {
        if (!$selectedLeaf) {
            return;
        }
        if ($readOnly) {
            console.error('Cannot edit readonly');
            return;
        }

        const changes: SetPropertyItem[] = [];
        const item = event.detail.item;

        const pushChange = ({ property, value }: {
            property: string;
            value: unknown;
        }) => {
            if (property === 'width.type' || property === 'height.type') {
                const prevElemProps = rendererApi().selectedElemProps();

                const sizeProp = property === 'width.type' ? 'width' : 'height';
                if (value === 'fixed') {
                    return [{
                        property: sizeProp,
                        value: {
                            type: 'fixed',
                            value: prevElemProps ? Math.ceil(prevElemProps[sizeProp]) : 100
                        }
                    }];
                } else if (value === 'match_parent') {
                    return [{
                        property: sizeProp,
                        value: {
                            type: value
                        }
                    }];
                } else if (value === 'wrap_content') {
                    return [{
                        property: sizeProp,
                        value: {
                            type: value
                        }
                    }];
                }
                return [];
            } else if (property === 'alignment_horizontal' || property === 'alignment_vertical') {
                const sizeProp = property === 'alignment_horizontal' ? 'width' : 'height';
                const startProp = sizeProp === 'width' ? 'left' : 'top';
                const endProp = sizeProp === 'width' ? 'right' : 'bottom';

                const changes = [{
                    property,
                    value
                }];

                if (json.margins) {
                    if (json.margins[startProp]) {
                        changes.push({
                            property: 'margins.' + startProp,
                            value: undefined
                        });
                    }
                    if (json.margins[endProp]) {
                        changes.push({
                            property: 'margins.' + endProp,
                            value: undefined
                        });
                    }
                }

                return changes;
            }

            return [{
                property,
                value
            }];
        };

        if ('values' in event.detail) {
            event.detail.values.forEach(set => {
                changes.push(...pushChange({
                    property: set.prop,
                    value: set.value
                }).map(it => ({ ...it, leafId: $selectedLeaf.id })));
            });
        } else if ('prop' in item && item.prop) {
            changes.push(...pushChange({
                property: item.prop,
                value: event.detail.value
            }).map(it => ({ ...it, leafId: $selectedLeaf.id })));
        }

        const related = 'related' in item && item.related;
        if (related) {
            related.forEach(({ prop, value }) => {
                changes.push({
                    leafId: $selectedLeaf.id,
                    property: prop,
                    value
                });
            });
        }

        state.pushCommand(new SetPropertyCommand($tree, changes));
    }
</script>

<div class="simple-props">
    {#if list && processedJson}
        <SimplePropsList
            propsList={[...list, ...(templateProps && [templateProps] || [])]}
            {processedJson}
            {parentProcessedJson}
            {evalJson}
            {parentEvalJson}
            {selectedElemProps}
            on:change={onChange}
        />
    {/if}
</div>

<style>
    .simple-props {
        padding-top: 16px;
        padding-bottom: 16px;
    }
</style>
