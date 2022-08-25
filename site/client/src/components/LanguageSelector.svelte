<script lang="ts">
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';

    const {l10n, lang, languagesList} = getContext<LanguageContext>(LANGUAGE_CTX);
</script>

<div class="language-selector">
    {#each languagesList() as language, index}
        {#if index >= 1}
            <div class="language-selector__separator">|</div>
        {/if}
        <label
            class="language-selector__item"
            class:language-selector__item_selected={language === $lang}
        >
            {$l10n('name', language)}
            <input class="language-selector__radio" type="radio" name="language" bind:group={$lang} value={language}>
        </label>
    {/each}
</div>

<style>
    .language-selector {
        position: relative;
        display: flex;
    }

    .language-selector__item {
        position: relative;
        font-size: 20px;
    }

    .language-selector__item_selected {
        font-weight: bold;
    }

    .language-selector__radio {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        opacity: 0;
    }

    .language-selector__separator {
        width: 14px;
        font-size: 20px;
        text-align: center;
    }
</style>
