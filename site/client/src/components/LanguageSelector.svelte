<script lang="ts">
    import { getContext } from 'svelte';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';
    import Select from './Select.svelte';

    const {l10n, lang, languagesList} = getContext<LanguageContext>(LANGUAGE_CTX);
</script>

<div class="language-selector">
    <Select
        bind:value={$lang}
        items={languagesList().map(language => ({name: $l10n('name', language), value: language}))}
        popupOrientation="right"
        toggledClass="language-selector__select_toggled"
    >
        <button class="language-selector__button">
            {$l10n('name')}
        </button>
    </Select>
</div>

<style>
    .language-selector {
        position: relative;
    }

    .language-selector__button {
        margin: 0;
        padding: 0;
        padding-right: 26px;
        font: inherit;
        font-size: 20px;
        cursor: pointer;
        color: inherit;
        border: none;
        border-radius: 0;
        background: no-repeat 100% 50% url(../assets/dropdown.svg);
        appearance: none;
    }
</style>
