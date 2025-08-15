<script lang="ts">
    import PanelHeader from './PanelHeader.svelte';
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';
    import type { TreeLeaf } from '../ctx/tree';
    import { savedStore } from '../data/savedStore';
    import { editorMode } from '../data/editorMode';
    import { initialValueStore, valueStore } from '../data/valueStore';
    import Tree from './Tree.svelte';
    import { sampleWarningStore } from '../data/sampleWarningStore';

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);

    const samples = require.context('../../../../test_data/samples/', true, /^\.\/.*$/, 'lazy-once');

    const promise = samples(samples.keys()[0]);

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

    const keys = samples.keys();
    keys.forEach(key => {
        const trimmedKey = key.replace(/^\.\//, '').replace(/\.json$/, '');

        if (!trimmedKey.endsWith('/templates')) {
            appendKey(key, trimmedKey);
        }
    });

    function treeGetText(leaf: TreeLeaf): string {
        return leaf.props.name;
    }

    function onSelectionChange(event: CustomEvent<TreeLeaf | null>) {
        if (!$savedStore && !confirm('Unsaved changes will be lost. Continue?')) {
            return;
        }

        const key = event.detail?.props.key;
        if (key) {
            const templatesKey = key.replace(/\/[^/]+\.json/, '/templates.json');
            const jsonPromise = samples(key);
            const templatesPromise = keys.includes(templatesKey) ? samples(templatesKey) : Promise.resolve(null);

            Promise.all([jsonPromise, templatesPromise]).then(([card, templates]) => {
                let json;

                if (card.card) {
                    json = card;
                } else if (templates) {
                    json = {
                        card,
                        templates
                    };
                } else {
                    json = {
                        card
                    };
                }

                editorMode.set('json');
                const value = JSON.stringify(json, null, 4);
                initialValueStore.set(value);
                valueStore.set(value);

                sampleWarningStore.set(key.includes('base/size_units'));
            });
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
