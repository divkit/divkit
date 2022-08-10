<script lang="ts">
    import { slide } from 'svelte/transition';
    import { selectedLeaf } from '../data/webStructure';
    import JsonView from './JsonView.svelte';
    import { valueStore } from '../data/valueStore';

    let expanded = false;

    $: origJson = $selectedLeaf?.props.origJson;
    let jsonList: {
        title: string;
        json: any;
    }[] = [];

    function updateJsonList(origJson: any) {
        jsonList = [];

        if (!origJson) {
            return;
        }

        jsonList.push({
            title: 'Source',
            json: origJson
        });

        let json;
        try {
            json = JSON.parse($valueStore);
        } catch (err) {
            return;
        }

        let item = origJson;
        while (item?.type && json?.templates && item.type in json.templates) {
            jsonList.push({
                title: item.type,
                json: json.templates[item.type]
            });
            item = json.templates[item.type];
        }
    }
    $: updateJsonList(origJson);

    function toggle(): void {
        expanded = !expanded;
    }
</script>

<div class="structure-templates">
    <div class="structure-templates__title" on:click={toggle}>
        Applied templates
    </div>
    {#if expanded}
        <div class="structure-templates__content" transition:slide>
            {#if jsonList.length}
                {#each jsonList as item}
                    <div class="structure-templates__item-title">
                        {item.title}
                    </div>
                    <JsonView json={item.json} expanded={true} />
                {/each}
            {:else}
                Nothing to display
            {/if}
        </div>
    {/if}
</div>

<style>
    .structure-templates {
        display: flex;
        flex-direction: column;
        flex: 0 1 auto;
        min-height: 43px;
        max-height: 300px;
        background: var(--bg-secondary);
        border-top: 1px solid var(--separator);
    }

    .structure-templates__title {
        flex: 0 0 auto;
        padding: 12px 12px;
        cursor: pointer;
        user-select: none;
        transition: background .1s ease-in-out;
    }

    .structure-templates__title:hover {
        text-decoration: underline;
    }

    .structure-templates__content {
        overflow: auto;
        padding: 8px 12px;
        background: var(--bg-primary);
    }

    .structure-templates__item-title {
        margin-bottom: 10px;
    }

    .structure-templates__item-title:not(:first-child) {
        margin-top: 30px;
    }
</style>
