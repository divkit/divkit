<script lang="ts">
    import { createEventDispatcher, getContext } from 'svelte';
    import type { Action } from '@divkitframework/divkit/typings/common';
    import { LANGUAGE_CTX, type LanguageContext } from '../../ctx/languageContext';
    import type { ComponentProperty } from '../../data/componentProps';
    import MoveList2 from '../controls/MoveList2.svelte';
    import VideoSourcesItem from './VideoSourcesItem.svelte';
    import AddButton from '../controls/AddButton.svelte';
    import { APP_CTX, type AppContext } from '../../ctx/appContext';

    export let value: Action[];
    export let item: ComponentProperty;

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);
    const { state } = getContext<AppContext>(APP_CTX);
    const { readOnly } = state;

    const dispatch = createEventDispatcher();

    function onChange(): void {
        if (!$readOnly) {
            dispatch('change', {
                item,
                value
            });
        }
    }

    function onAdd(): void {
        if (!$readOnly) {
            dispatch('change', {
                item,
                value: [...(value || []), {
                    type: 'video_source',
                    url: ''
                }]
            });
        }
    }
</script>

<svelte:options immutable={true} />

<div class="video-sources">
    <div class="video-sources__list">
        <MoveList2
            bind:values={value}
            itemView={VideoSourcesItem}
            readOnly={$readOnly}
            on:change={onChange}
            on:reorder={onChange}
        />
    </div>

    <AddButton
        cls="video-sources__add"
        disabled={$readOnly}
        on:click={onAdd}
    >
        {$l10n('add_video')}
    </AddButton>
</div>

<style>
    .video-sources__list {
        margin-left: -20px;
    }
</style>
