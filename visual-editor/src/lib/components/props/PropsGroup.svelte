<script lang="ts">
    import { getContext } from 'svelte';
    import { capitalize } from '../../utils/capitalize';
    import PropsSubGroup from './PropsSubGroup.svelte';
    import { getPropsList, resolveDesc, type PropItem } from '../../data/schema';
    import { getObjectProperty, setObjectProperty } from '../../utils/objectProperty';
    import { SetPropertyCommand } from '../../data/commands/setProperty';
    import Spoiler2 from '../controls/Spoiler2.svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let group = '';

    const { state } = getContext<AppContext>(APP_CTX);
    const { selectedLeaf, readOnly, tree } = state;

    // todo process templates somehow

    let list: PropItem[] | null = null;

    $: {
        list = null;

        if (group && $selectedLeaf) {
            const desc = resolveDesc(`div-${group}`);
            if (desc) {
                list = getPropsList(desc, $selectedLeaf.props.processedJson) || null;
            } else {
                list = [];
            }
        }
    }

    function onChange(event: CustomEvent<{
        value: unknown;
        prop: string;
    }>) {
        if (!$selectedLeaf) {
            return;
        }
        if ($readOnly) {
            console.error('Cannot edit readonly');
            return;
        }

        state.pushCommand(new SetPropertyCommand($tree, [{
            leafId: $selectedLeaf.id,
            property: event.detail.prop,
            value: event.detail.value
        }]));
    }

    function onAdd(event: CustomEvent<{
        prop: string;
        subtype: string;
    }>) {
        if (!$selectedLeaf) {
            return;
        }
        if ($readOnly) {
            console.error('Cannot edit readonly');
            return;
        }

        const toSet = $selectedLeaf.props.json;

        const val = getObjectProperty(toSet, event.detail.prop);
        let key;
        if (event.detail.subtype === 'custom') {
            if (val && typeof val === 'object') {
                let counter = 0;
                do {
                    key = `prop${counter++}`;
                } while (key in val);
            } else {
                key = 'prop0';
                setObjectProperty(toSet, event.detail.prop, {});
            }
        } else if (Array.isArray(val)) {
            key = val.length;
        } else {
            key = 0;
        }
        state.pushCommand(new SetPropertyCommand($tree, [{
            leafId: $selectedLeaf.id,
            property: event.detail.prop + '[' + key + ']',
            value: event.detail.subtype === 'object' ? {} : ''
        }]));

        tree.set($tree);
    }

    function onDelete(event: CustomEvent<{
        prop: string;
        key: string | number;
        subtype?: string;
    }>) {
        if (!$selectedLeaf) {
            return;
        }
        if ($readOnly) {
            console.error('Cannot edit readonly');
            return;
        }

        const key = event.detail.key;
        let toSet = $selectedLeaf.props.json;
        let val = getObjectProperty(toSet, event.detail.prop);

        if (event.detail.subtype === 'custom') {
            if (val && typeof val === 'object') {
                let val2 = val as Record<string, unknown>;
                val2 = { ...val2 };
                delete val2[key];

                state.pushCommand(new SetPropertyCommand($tree, [{
                    leafId: $selectedLeaf.id,
                    property: event.detail.prop,
                    value: Object.keys(val2).length === 0 ? undefined : val
                }]));
            }
        } else if (Array.isArray(val)) {
            const newVal = val.slice();
            newVal.splice(Number(key), 1);
            state.pushCommand(new SetPropertyCommand($tree, [{
                leafId: $selectedLeaf.id,
                property: event.detail.prop,
                value: val.length === 1 && key === 0 ? undefined : newVal
            }]));
        }
    }

    function onRename(event: CustomEvent<{
        prop: string;
        key: string;
        newName: string;
    }>): void {
        if (!$selectedLeaf) {
            return;
        }
        // todo check is valid and duplicates
        // todo fix [] brackets in name
        const key = event.detail.key;
        let toSet = $selectedLeaf.props.json;
        const val = getObjectProperty(toSet, event.detail.prop);

        if (val && typeof val === 'object') {
            let val2 = val as Record<string, unknown>;
            val2 = { ...val2 };
            val2[event.detail.newName] = val2[key];
            delete val2[key];

            state.pushCommand(new SetPropertyCommand($tree, [{
                leafId: $selectedLeaf.id,
                property: event.detail.prop,
                value: val2
            }]));
        }
    }
</script>

<Spoiler2
    theme="straight"
    mix="props-group"
    open
>
    <div slot="title" class="props-group__summary">
        {capitalize(group)}
    </div>

    <ul class="props-group__content">
        {#if list}
            <PropsSubGroup
                {group}
                {list}
                on:change={onChange}
                on:add={onAdd}
                on:delete={onDelete}
                on:rename={onRename}
            />
        {/if}
    </ul>
</Spoiler2>

<style>
    :global(.props-group) {
        flex: 0 0 auto;
    }

    .props-group__summary {
        padding: 0 9px;
    }

    .props-group__content {
        box-sizing: border-box;
        list-style: none;
        margin: 0;
        padding: 0;
        padding-left: 20px;
        width: 100%;
    }
</style>
