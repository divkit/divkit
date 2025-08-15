<script lang="ts">
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';

    export let name = '';
    export let comma = false;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    export let json: any;
    export let level = 0;
    export let expanded = false;

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);

    $: keys = json && typeof json === 'object' ? Object.keys(json) : [];
    $: isStr = typeof json === 'string';
</script>

{#key json}
    <div class="json-view" class:json-view_first={level === 0}>
        <div class="json-view__row">
            {#if name}
                <div class="json-view__name">
                    "{name}"
                </div>:<div class="json-view__space">{' '}</div>
            {/if}

            {#if Array.isArray(json)}
                {#if expanded}
                    <div class="json-view__row">
                        <!-- svelte-ignore a11y-click-events-have-key-events -->
                        <div class="json-view__collapse" on:click={() => expanded = false} title={$l10n('collapse')}></div>
                        {'['}
                    </div>
                {:else}
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <div class="json-view__toggler" on:click={() => expanded = true} title={$l10n('expand')}>
                        {`[ ${json.length} item${json.length > 1 ? 's' : ''} ]`}
                    </div>
                {/if}
            {:else if json && typeof json === 'object'}
                {#if expanded}
                    <div class="json-view__row">
                        <!-- svelte-ignore a11y-click-events-have-key-events -->
                        <div class="json-view__collapse" on:click={() => expanded = false} title={$l10n('collapse')}></div>
                        {'{'}
                    </div>
                {:else}
                    <!-- svelte-ignore a11y-click-events-have-key-events -->
                    <div class="json-view__toggler" on:click={() => expanded = true}>
                        {'{...}'}
                    </div>
                {/if}
            {:else}
                {@const stringified = JSON.stringify(json)}

                <div class="json-view__val" class:json-view__val_str={isStr} title={stringified}>{stringified}</div>
                {#if comma}
                    ,
                {/if}
            {/if}
        </div>

        {#if expanded}
            {#if Array.isArray(json)}
                {#each json as item, index}
                    <svelte:self json={item} level={level + 1} comma={index + 1 < keys.length} />
                {/each}
                <div class="json-view__row">
                    {']'}{#if comma},{/if}
                </div>
            {:else if json && typeof json === 'object'}
                {#each keys as key, index}
                    <svelte:self name={key} json={json[key]} level={level + 1} comma={index + 1 < keys.length} />
                {/each}
                <div class="json-view__row">
                    {'}'}{#if comma},{/if}
                </div>
            {/if}
        {/if}
    </div>
{/key}

<style>
    .json-view {
        padding-left: 24px;
        font-family: "Droid Sans Mono", "monospace", monospace, "Droid Sans Fallback";
        font-size: 14px;
        line-height: 19px;
    }

    .json-view_first {
        padding-left: 0;
    }

    .json-view__row {
        display: flex;
        align-items: center;
    }

    .json-view__name {
        color: #a31515;
        white-space: nowrap;
    }

    .json-view__space {
        width: 6px;
        flex: 0 0 auto;
    }

    .json-view__val {
        flex: 0 1 auto;
        min-width: 0;
        overflow: hidden;
        text-overflow: ellipsis;
        color: #098658;
        white-space: nowrap;
    }

    .json-view__val_str {
        color: #0451a5;
    }

    .json-view__toggler {
        background: var(--bg-secondary);
        transition: opacity .1s ease-in-out;
        cursor: pointer;
    }

    .json-view__toggler:hover {
        opacity: .5;
    }

    .json-view__collapse {
        align-self: stretch;
        width: 16px;
        margin-right: 6px;
        cursor: pointer;
        background: no-repeat 50% 50% var(--bg-secondary) url(../assets/arrowDown.svg);
        transition: opacity .1s ease-in-out;
    }

    .json-view__collapse:hover {
        opacity: .5;
    }
</style>
