<script lang="ts">
    export let text: string;

    $: parts = text.split(/<\/?code>/);
</script>

{#each parts as item, index}
    {#if index % 2 === 1}
        <span class="features-text__code">{item}</span>
    {:else}
        {item}
    {/if}
{/each}

<style>
    .features-text__code {
        padding: 2px 4px;
        background: rgba(125, 125, 125, 0.2);
        font-family: Droid Sans Mono,"monospace",monospace,Droid Sans Fallback;
        border-radius: 4px;
    }
</style>
