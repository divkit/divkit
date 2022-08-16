<div class="panel-header">
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
</style>
