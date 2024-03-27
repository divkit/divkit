<script lang="ts">
    import css from './Separator.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivSeparatorData } from '../../types/separator';
    import type { Orientation } from '../../types/orientation';
    import type { ComponentContext } from '../../types/componentContext';
    import Outer from '../utilities/Outer.svelte';
    import { makeStyle } from '../../utils/makeStyle';
    import { pxToEm } from '../../utils/pxToEm';
    import { genClassName } from '../../utils/genClassName';
    import { correctGeneralOrientation } from '../../utils/correctGeneralOrientation';
    import { correctColor } from '../../utils/correctColor';

    export let componentContext: ComponentContext<DivSeparatorData>;
    export let layoutParams: LayoutParams | undefined = undefined;

    let orientation: Orientation = 'horizontal';
    // let background = correctColor('#14000000');
    let background = 'rgba(0,0,0,0.08)';

    $: origJson = componentContext.origJson;

    function rebind(): void {
        orientation = 'horizontal';
        background = 'rgba(0,0,0,0.08)';
    }

    $: if (origJson) {
        rebind();
    }

    $: jsonDelimiterStyle = componentContext.getDerivedFromVars(componentContext.json.delimiter_style);

    $: {
        orientation = correctGeneralOrientation($jsonDelimiterStyle?.orientation, orientation);
    }

    // Avoid transparent separator creation
    $: hasContent = !(
        $jsonDelimiterStyle?.color &&
        (
            $jsonDelimiterStyle.color === 'transparent' ||
            (
                $jsonDelimiterStyle.color.length === 9 &&
                $jsonDelimiterStyle.color.indexOf('#00') === 0
            )
        )
    );

    $: {
        background = correctColor($jsonDelimiterStyle?.color, 1, background);
    }

    $: width = orientation === 'horizontal' ? '100%' : pxToEm(1);
    $: height = orientation === 'horizontal' ? pxToEm(1) : '100%';

    $: style = {
        background,
        width,
        height
    };

    $: mods = {
        orientation
    };
</script>

<Outer
    cls={genClassName('separator', css, mods)}
    {componentContext}
    {layoutParams}
>
    {#if hasContent}
        <span class={css.separator__inner} style={makeStyle(style)}></span>
    {/if}
</Outer>
