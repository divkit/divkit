<script lang="ts">
    import { getContext } from 'svelte';

    import css from './Separator.module.css';

    import type { LayoutParams } from '../../types/layoutParams';
    import type { DivSeparatorData } from '../../types/separator';
    import type { DivBase, TemplateContext } from '../../../typings/common';
    import type { Orientation } from '../../types/orientation';
    import Outer from '../utilities/Outer.svelte';
    import { makeStyle } from '../../utils/makeStyle';
    import { pxToEm } from '../../utils/pxToEm';
    import { genClassName } from '../../utils/genClassName';
    import { ROOT_CTX, RootCtxValue } from '../../context/root';
    import { correctGeneralOrientation } from '../../utils/correctGeneralOrientation';
    import { correctColor } from '../../utils/correctColor';

    export let json: Partial<DivSeparatorData> = {};
    export let templateContext: TemplateContext;
    export let origJson: DivBase | undefined = undefined;
    export let layoutParams: LayoutParams | undefined = undefined;

    const rootCtx = getContext<RootCtxValue>(ROOT_CTX);

    let orientation: Orientation = 'horizontal';
    // let background = correctColor('#14000000');
    let background = 'rgba(0,0,0,0.08)';

    $: if (json) {
        orientation = 'horizontal';
        background = 'rgba(0,0,0,0.08)';
    }

    $: jsonDelimiterStyle = rootCtx.getDerivedFromVars(json.delimiter_style);

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
    {json}
    {origJson}
    {templateContext}
    {layoutParams}
>
    {#if hasContent}
        <span class={css.separator__inner} style={makeStyle(style)}></span>
    {/if}
</Outer>
