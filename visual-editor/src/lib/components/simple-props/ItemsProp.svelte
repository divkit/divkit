<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import type { ComponentProperty } from '../../data/componentProps';
    import MoveList2 from '../controls/MoveList2.svelte';
    import AddButton from '../controls/AddButton.svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';
    import type { Item } from '../../utils/items';
    import ItemsListItem from './ItemsListItem.svelte';
    import { ReplaceLeafsCommand } from '../../data/commands/replaceLeafs';
    import type { TreeLeaf } from '../../ctx/tree';

    export let value: Item[];
    export let item: ComponentProperty;

    let list = value;

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly, selectedLeaf } = state;

    const dispatch = createEventDispatcher();

    $: if (value) {
        updateValue();
    }

    function updateValue(): void {
        list = value;
    }

    function onChange(event: CustomEvent<{
        values: (Item & {
            __key?: string;
        })[];
    }>): void {
        const leaf = $selectedLeaf;
        if (!leaf) {
            return;
        }
        const leaf2 = leaf;
        const defaultStateId = leaf2.props.processedJson?.default_state_id;

        const valueMap = new Map<string, TreeLeaf>();
        list.forEach((it, index) => {
            if (it.div?.__leafId) {
                valueMap.set(it.div.__leafId, leaf2.childs[index]);
            }
        });

        const newLeafs = event.detail.values.map(it => {
            const foundLeaf = it.div?.__leafId !== undefined && valueMap.get(it.div.__leafId);
            if (foundLeaf) {
                if (foundLeaf.props.info?.state_id !== it.state_id) {
                    const oldStateId = foundLeaf.props.info?.state_id;
                    foundLeaf.props.info = {
                        state_id: it.state_id
                    };
                    if (oldStateId === defaultStateId) {
                        dispatch('change', {
                            values: [{
                                prop: 'default_state_id',
                                value: it.state_id
                            }],
                            item
                        });
                    }
                }
                foundLeaf.props.info.__key = it.__key;
                return foundLeaf;
            }

            const leaf = state.getChild(`_new:${it.div.type}`, true);

            if (leaf) {
                leaf.props.info = {
                    state_id: it.state_id,
                    __key: it.__key
                };

                return leaf;
            }
        }) as TreeLeaf[];
        state.pushCommand(new ReplaceLeafsCommand({
            parentId: leaf.id,
            leafs: newLeafs
        }));
        list = event.detail.values;
    }

    function onAdd(): void {
        if ($readOnly) {
            return;
        }

        const leaf = $selectedLeaf;
        if (!leaf) {
            return;
        }

        let counter = 0;
        let stateId = 'state_' + counter;
        while (leaf.childs.some(it => it.props?.info?.state_id === stateId)) {
            ++counter;
            stateId = 'state_' + counter;
        }

        const newLeaf = state.getChild(`_new:${'text'}`, true);
        if (!newLeaf) {
            return;
        }
        newLeaf.props.info = {
            state_id: stateId
        };

        const newLeafs = [
            ...leaf.childs,
            newLeaf
        ] as TreeLeaf[];
        state.pushCommand(new ReplaceLeafsCommand({
            parentId: leaf.id,
            leafs: newLeafs
        }));
    }
</script>

<svelte:options immutable={true} />

<div class="items-list">
    <div class="items-list__list">
        <MoveList2
            values={list.slice()}
            itemView={ItemsListItem}
            readOnly={$readOnly}
            on:change={onChange}
            on:reorder={onChange}
        />
    </div>

    <AddButton
        cls="items-list__add"
        disabled={$readOnly}
        on:click={onAdd}
    >
        {$l10n('add_state')}
    </AddButton>
</div>

<style>
    .items-list__list {
        margin-left: -20px;
    }
</style>
