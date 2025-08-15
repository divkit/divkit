<script lang="ts">
    export let theme: 'transparent' | 'filled' = 'transparent';
</script>

<div class="panel-header" class:panel-header_theme_filled={theme === 'filled'}>
    <div class="panel-header__left">
        <slot name="left" />
    </div>
    <div class="panel-header__right">
        <slot name="right" />
    </div>
</div>

<style>
    .panel-header {
        position: relative;
        display: flex;
        align-items: center;
        justify-content: space-between;
        flex: 0 0 auto;
        height: 50px;
        padding: 0 19px;
    }

    .panel-header_theme_filled {
        background: var(--bg-secondary);
    }
</style>
