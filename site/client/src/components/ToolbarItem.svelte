<script lang="ts">
    export let icon: string;
    export let selected: boolean;
    export let text: string;
</script>

<button class="toolbar-item" class:toolbar-item_selected={selected} on:click>
    <div class="toolbar-item__border">
        <div class="toolbar-item__icon toolbar-item__icon_{icon}"></div>
    </div>

    <div class="toolbar-item__tooltip">
        {text}
    </div>
</button>

<style>
    .toolbar-item {
        position: relative;
        display: flex;
        flex: 0 0 auto;
        align-items: center;
        justify-content: center;
        height: 36px;
        background: none;
        border: none;
        font: inherit;
        color: inherit;
    }

    .toolbar-item:not(.toolbar-item_selected) {
        cursor: pointer;
    }

    .toolbar-item__border {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 36px;
        height: 36px;
        border-radius: 8px;
        transition: background-color .15s ease-in-out;
    }

    .toolbar-item:not(.toolbar-item_selected):hover .toolbar-item__border {
        background-color: rgba(0, 0, 0, .12);
    }

    .toolbar-item_selected .toolbar-item__border {
        background: #5959E8;
    }

    .toolbar-item__icon {
        width: 20px;
        height: 20px;
        filter: invert(100%);
        background: no-repeat 50% 50%;
    }

    .toolbar-item_selected .toolbar-item__icon {
        filter: none;
    }

    .toolbar-item__icon_components {
        background-image: url(../assets/divkitpro/components.svg);
    }

    .toolbar-item__icon_palette {
        background-image: url(../assets/divkitpro/palette.svg);
    }

    .toolbar-item__icon_sources {
        background-image: url(../assets/divkitpro/sources.svg);
    }

    .toolbar-item__tooltip {
        position: absolute;
        z-index: 1;
        left: 100%;
        padding: 8px 12px;
        font-size: 14px;
        line-height: 20px;
        color: #000;
        border-radius: 8px;
        background: #fff;
        filter: drop-shadow(0 1px 8px rgba(0, 0, 0, 0.14));
        pointer-events: none;
        transform: translateX(1rem);
        transition: .2s ease-in-out;
        transition-property: visibility, opacity, transform;
        opacity: 0;
        visibility: hidden;
    }

    .toolbar-item:hover .toolbar-item__tooltip {
        opacity: 1;
        visibility: visible;
        transform: translateX(0);
    }

    .toolbar-item__tooltip::before {
        position: absolute;
        z-index: -1;
        top: 10px;
        left: -4px;
        width: 16px;
        height: 16px;
        background: inherit;
        transform: rotate(45deg);
        content: '';
    }
</style>
