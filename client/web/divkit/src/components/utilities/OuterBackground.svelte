<script lang="ts">
    import css from './OuterBackground.module.css';

    import type { Style } from '../../types/general';
    import type { Background } from '../../types/background';
    import type { MaybeMissing } from '../../expressions/json';
    import type { Direction } from '../../../typings/common';
    import type { ComponentContext } from '../../types/componentContext';
    import { getBackground } from '../../utils/background';
    import { makeStyle } from '../../utils/makeStyle';
    import { getCssFilter } from '../../utils/filters';

    export let direction: Direction;
    export let componentContext: ComponentContext;
    export let background: MaybeMissing<Background[]> = [];
    export let radius = '';

    $: styles = background.map(bg => {
        const stl: Style = {};
        const obj: {
            image_url?: string;
            style: Style;
        } = {
            style: stl
        };

        if (bg.type === 'nine_patch_image' && bg.insets) {
            stl['border-image'] = `url("${bg.image_url}") ${bg.insets.top || 0} ${bg.insets.right || 0} ${bg.insets.bottom || 0} ${bg.insets.left || 0} fill`;
            stl['border-image-width'] = 'auto';
        } else {
            const res = getBackground([bg]);

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

                if (Array.isArray(bg.filters) && bg.filters.length) {
                    stl.filter = getCssFilter(bg.filters, componentContext.logError);

                    if (direction === 'rtl' && bg.filters.some(it => it.type === 'rtl_mirror')) {
                        stl.transform = 'scale(-1,1)';
                    }
                }
            }
        }

        return obj;
    });

    function onImgError(event: Event): void {
        if (event.target && 'classList' in event.target) {
            (event.target as HTMLElement).classList.add(css['outer-background__item_hidden']);
        }
    }
</script>

<span
    class={css['outer-background'] + (radius ? ' ' + css['outer-background_clip'] : '')}
    style:border-radius={radius}
>
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
