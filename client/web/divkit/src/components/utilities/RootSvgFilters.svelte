<script lang="ts">
    import css from './RootSvgFilters.module.css';

    export let svgFiltersMap: Record<string, string>;
</script>

{#if Object.keys(svgFiltersMap).length}
    <svg
        class={css['root-svg-filters']}
        aria-hidden="true"
    >
        <defs>
            {#each [...Object.keys(svgFiltersMap)] as filterKey}
                {@const [filterColor, filterMode] = filterKey.split(':')}
                <filter id={svgFiltersMap[filterKey]}>
                    <feFlood flood-color={filterColor} />

                    {#if filterMode === 'source_in' || filterMode === 'source_atop'}
                        <feComposite in2="SourceGraphic" operator={filterMode.split('_')[1]} />
                    {:else if filterMode === 'multiply'}
                        <feComposite in2="SourceGraphic" operator="arithmetic" k1="1" k2="0" k3="0" k4="0" />
                    {:else}
                        <feBlend in2="SourceGraphic" mode={filterMode} />
                    {/if}
                </filter>
            {/each}
        </defs>
    </svg>
{/if}
