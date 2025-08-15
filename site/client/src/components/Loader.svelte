<script lang="ts">
    import { fade } from 'svelte/transition';
</script>

<div class="loader" out:fade={{duration: 200}}></div>

<style>
    .loader {
        position: absolute;
        z-index: 10;
        top: 0;
        left: 0;
        flex: 1 0 0;
        width: 100%;
        height: 100%;
        background: no-repeat 50% 50% var(--bg-primary) url(../assets/logo.svg);
        background-size: 128px;
    }
</style>
