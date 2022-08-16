<script lang="ts">
    import { highlightElem, highlightPart } from '../data/webStructure';

    export let node: HTMLElement;

    let margin = {
        top: 0,
        right: 0,
        bottom: 0,
        left: 0
    };
    let padding = {
        top: 0,
        right: 0,
        bottom: 0,
        left: 0
    };
    let size = {
        width: 0,
        height: 0
    };

    $: if (node) {
        let bbox = node.getBoundingClientRect();
        let style = getComputedStyle(node);

        margin = {
            top: parseInt(style.marginTop),
            right: parseInt(style.marginRight),
            bottom: parseInt(style.marginBottom),
            left: parseInt(style.marginLeft)
        };
        padding = {
            top: parseInt(style.paddingTop),
            right: parseInt(style.paddingRight),
            bottom: parseInt(style.paddingBottom),
            left: parseInt(style.paddingLeft)
        };
        size = {
            width: bbox.width - padding.right - padding.left,
            height: bbox.height - padding.top - padding.bottom
        };
    }

    function onMouseEnter(): void {
        highlightElem.set(node);
        highlightPart.set(1);
    }

    function onMouseLeave(): void {
        highlightElem.set(null);
        highlightPart.set(1 | 2 | 4);
    }

    function onPaddingMouseEnter(): void {
        highlightPart.set(2);
    }

    function onPaddingMouseLeave(): void {
        highlightPart.set(1);
    }

    function onContentMouseEnter(): void {
        highlightPart.set(4);
    }

    function onContentMouseLeave(): void {
        highlightPart.set(2);
    }
</script>

<div class="structure-box">
    <div class="structure-box__part structure-box__part_margin" class:structure-box__part_highlight={$highlightPart & 1} on:mouseenter={onMouseEnter} on:mouseleave={onMouseLeave}>
        <div class="structure-box__title">margin</div>
        <div class="structure-box__val structure-box__val_top">{margin.top}</div>
        <div class="structure-box__val structure-box__val_bottom">{margin.bottom}</div>
        <div class="structure-box__val structure-box__val_left">{margin.left}</div>

        <div class="structure-box__part structure-box__part_padding" class:structure-box__part_highlight={$highlightPart & 2} on:mouseenter={onPaddingMouseEnter} on:mouseleave={onPaddingMouseLeave}>
            <div class="structure-box__title">padding</div>
            <div class="structure-box__val structure-box__val_top">{padding.top}</div>
            <div class="structure-box__val structure-box__val_bottom">{padding.bottom}</div>
            <div class="structure-box__val structure-box__val_left">{padding.left}</div>

            <div class="structure-box__part structure-box__part_content" class:structure-box__part_highlight={$highlightPart & 4} on:mouseenter={onContentMouseEnter} on:mouseleave={onContentMouseLeave}>
                {size.width}x{size.height}
            </div>

            <div class="structure-box__val structure-box__val_right">{padding.right}</div>
        </div>

        <div class="structure-box__val structure-box__val_right">{margin.right}</div>
    </div>
</div>

<style>
    .structure-box {
        padding: 20px 0 0;
        font-family: "Droid Sans Mono", "monospace", monospace, "Droid Sans Fallback";
        font-size: 14px;
        text-align: center;
    }

    .structure-box__part {
        position: relative;
        display: inline-flex;
        vertical-align: top;
        padding: 24px 0;
        border: 1px dotted #fff;
        background: var(--bg-primary);
    }

    .structure-box__part_highlight.structure-box__part_margin {
        background: rgba(246, 178, 107, 0.66);
    }

    .structure-box__part_highlight.structure-box__part_padding {
        background: rgba(147, 196, 125, 0.55);
    }

    .structure-box__part_highlight.structure-box__part_content {
        background: rgba(111, 168, 220, 0.66);
    }

    .structure-box__part_content {
        padding-right: 24px;
        padding-left: 24px;
    }

    .structure-box__title {
        position: absolute;
        top: 0;
        left: 6px;
        height: 24px;
        display: flex;
        align-items: center;
    }

    .structure-box__val {
        position: absolute;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .structure-box__val_top {
        top: 0;
        right: 0;
        left: 0;
        height: 24px;
    }

    .structure-box__val_bottom {
        right: 0;
        bottom: 0;
        left: 0;
        height: 24px;
    }

    .structure-box__val_right,
    .structure-box__val_left {
        position: static;
        padding: 0 8px;
    }
</style>
