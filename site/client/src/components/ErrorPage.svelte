<script lang="ts">
    import Header from './Header.svelte';
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';

    const {l10n} = getContext<LanguageContext>(LANGUAGE_CTX);
</script>

<Header />

<div class="error-page">
    {$l10n('loadError')}
</div>

<style>
    .error-page {
        display: flex;
        align-items: center;
        justify-content: center;
        flex: 1 1 auto;
        font-size: 32px;
    }
</style>
