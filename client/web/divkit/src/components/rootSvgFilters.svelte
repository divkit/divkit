<script lang="ts">
    import css from './rootSvgFilters.module.css';

    export let svgFiltersMap: Record<string, string>;
</script>

{#if Object.keys(svgFiltersMap).length}
    <svg
        class={css['root-svg-filters']}
        aria-hidden="true"
    >
        <defs>
            {#each [...Object.keys(svgFiltersMap)] as filterColor}
                <filter id={svgFiltersMap[filterColor]}>
                    <feFlood flood-color={filterColor} />
                    <feComposite in2="SourceGraphic" operator="in" />
                </filter>
            {/each}
        </defs>
    </svg>
{/if}
