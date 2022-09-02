<script lang="ts">
    import css from './OuterBackground.module.css';

    import type { Style } from '../../types/general';
    import type { Background } from '../../types/background';
    import type { MaybeMissing } from '../../expressions/json';
    import { getBackground } from '../../utils/background';
    import { makeStyle } from '../../utils/makeStyle';

    export let background: MaybeMissing<Background[]> = [];

    const styles: {
        image_url?: string;
        style: Style;
    }[] = background.map(bg => {
        const stl: Style = {};
        const res = getBackground([bg]);
        const obj: {
            image_url?: string;
            style: Style;
        } = {
            style: stl
        };

        if (bg.type === 'solid') {
            stl['background-color'] = res.color;
        }
        if (bg.type === 'gradient') {
            stl['background-image'] = res.image;
        }
        if (bg.type === 'image') {
            stl.opacity = Number(bg.alpha);
            obj.image_url = bg.image_url;
            stl['object-fit'] = res.size;
            stl['object-position'] = res.position;
        }

        return obj;
    });

    function onImgError(event: Event): void {
        if (event.target && 'classList' in event.target) {
            (event.target as HTMLElement).classList.add(css['outer-background__item_hidden']);
        }
    }
</script>

<span class={css['outer-background']}>
    {#each styles as item}
        {#if item.image_url}
            <img
                src={item.image_url}
                alt=""
                aria-hidden="true"
                loading="lazy"
                decoding="async"
                class={css['outer-background__item']}
                style={makeStyle(item.style)}
                on:error={onImgError}
            >{:else}<span class={css['outer-background__item']} style={makeStyle(item.style)}></span>
        {/if}
    {/each}
</span>
