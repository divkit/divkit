<script lang="ts">
    import PanelHeader from './PanelHeader.svelte';
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../data/languageContext';
    import type { TreeLeaf } from '../ctx/tree';
    import { savedStore } from '../data/savedStore';
    import { editorMode } from '../data/editorMode';
    import { initialValueStore, valueStore } from '../data/valueStore';
    import Tree from './Tree.svelte';
    import { sampleWarningStore } from '../data/sampleWarningStore';
    import { wrapJson } from '../utils/divjson';

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);

    const promise = import('../utils/testData');

    let tree: TreeLeaf = {
        id: 'root',
        props: {
            name: 'Root'
        },
        childs: []
    };

    function appendKey(origKey: string, key: string): void {
        const parts = key.split('/');
        let leaf = tree;
        parts.forEach((part, index) => {
            let child: TreeLeaf | undefined = leaf.childs.find(child => child.props.name === part);
            if (!child) {
                child = {
                    id: `root/${parts.slice(0, index + 1).join('/')}`,
                    props: {
                        name: part,
                        key: index === parts.length -1 ? origKey : null
                    },
                    parent: leaf,
                    childs: []
                };
                leaf.childs.push(child);
            }
            leaf = child;
        });
    }

    promise.then(({
        samples,
    }) => {
        const samplesKeys = Object.keys(samples);
        samplesKeys.forEach(key => {
            const trimmedKey = key.replace(/^.*?\/test_data\//, '').replace(/\.json$/, '');

            if (!trimmedKey.endsWith('/templates')) {
                appendKey(key, trimmedKey);
            }
        });
    });

    function treeGetText(leaf: TreeLeaf): string {
        return leaf.props.name;
    }

    async function onSelectionChange(event: CustomEvent<TreeLeaf | null>) {
        if (!$savedStore && !confirm('Unsaved changes will be lost. Continue?')) {
            return;
        }

        const { samples } = await promise;

        const key = event.detail?.props.key;
        if (key) {
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            const card = (samples[key] as any).default;
            let json = wrapJson(card);

            editorMode.set('json');
            const value = JSON.stringify(json, null, 4);
            initialValueStore.set(value);
            valueStore.set(value);

            sampleWarningStore.set(key.includes('base/size_units'));
        }
    }
</script>

<div class="samples">
    <PanelHeader theme="filled">
        <div slot="left">{$l10n('samples')}</div>
    </PanelHeader>

    <div class="samples__content">
        {#await promise}
            <div class="samples__loading"></div>
        {:then}
            <Tree root={tree} showRoot={false} getText={treeGetText} on:selectionchange={onSelectionChange} />
        {/await}
    </div>
</div>

<style>
    .samples__loading {
        width: 40px;
        height: 40px;
        margin: 40px auto;
        background: no-repeat 50% 50% url(../assets/load2.svg);
        background-size: contain;
        animation: rotate 1s infinite linear;
    }

    @keyframes rotate {
        from {
            transform: rotate(0);
        }
        to {
            transform: rotate(1turn);
        }
    }
</style>
