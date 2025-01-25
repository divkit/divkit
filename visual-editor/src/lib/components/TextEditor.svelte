<script lang="ts">
    import { getContext, onDestroy, onMount } from 'svelte';
    import { combineRanges } from '../data/highlightChannel';
    import type { createEditor } from '../data/editor';
    import { APP_CTX, type AppContext } from '../ctx/appContext';
    import { SetJsonCommand } from '../data/commands/setJson';

    let node: HTMLElement;
    let editor: ReturnType<typeof createEditor> | undefined;
    let isSelfClick = false;
    let prevVal = '';

    const { editorFabric, shadowRoot, state } = getContext<AppContext>(APP_CTX);

    const {
        divjsonStore,
        selectedLeaf,
        highlightLeaf,
        highlightElem,
        highlightRanges,
        selectedRanges,
        highlightLoc,
        readOnly,
        themeStore
    } = state;

    $: if (prevVal !== $divjsonStore.fullString && !editor?.isFocused()) {
        editor?.setValue($divjsonStore.fullString);
    }

    $: editor?.setTheme($themeStore);

    $: editor?.setReadOnly($readOnly);

    $: editor?.decorateRanges(combineRanges($highlightRanges, $selectedRanges));

    $: if ($highlightLoc) {
        if (isSelfClick) {
            isSelfClick = false;
        } else {
            editor?.revealLoc($highlightLoc);
        }
    }

    onMount(() => {
        editor = editorFabric({
            node,
            value: $divjsonStore.fullString,
            theme: $themeStore,
            readOnly: $readOnly,
            shadowRoot,
            onChange(value) {
                if (value !== $divjsonStore.fullString) {
                    let json;
                    try {
                        json = JSON.parse(value);
                    } catch (err) {}
                    if (json) {
                        prevVal = value;
                        state.pushCommand(new SetJsonCommand(state, value));
                    }
                }
            },
            onOver(offset) {
                const leaf = offset === null ? null : state.findBestLeaf(offset);
                const range = leaf?.props.range;
                const node = leaf?.props.node;

                if (range && node) {
                    highlightLeaf.set([leaf]);
                    highlightElem.set([node]);
                    highlightRanges.set([range]);
                } else {
                    highlightLeaf.set(null);
                    highlightElem.set(null);
                    highlightRanges.set(null);
                }
            },
            onClick(offset) {
                const leaf = state.findBestLeaf(offset);

                isSelfClick = true;
                selectedLeaf.set(leaf || null);
            }
        });
    });

    onDestroy(() => {
        if (editor) {
            editor.destroy();
            editor = undefined;
        }
    });
</script>

<div class="text-editor" bind:this={node} />

<style>
    .text-editor {
        flex: 1 1 auto;
        white-space: pre;
        overflow: auto;
        min-height: 0;
        width: 0;
        min-width: 100%;
        margin-top: 12px;
        font-size: 80%;
    }

    :global(.cm-highlight) {
        background: var(--fill-transparent-1);
    }

    :global(.cm-select) {
        background: var(--fill-transparent-2);
    }
</style>
