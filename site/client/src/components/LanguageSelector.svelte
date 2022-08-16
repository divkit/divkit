<script lang="ts">
    import { getContext } from 'svelte';
    import { fly } from 'svelte/transition';
    import { LANGUAGE_CTX, LanguageContext } from '../data/languageContext';

    const {l10n, lang, setLanguage, languagesList} = getContext<LanguageContext>(LANGUAGE_CTX);

    let popupShown = false;
    let timer: number;

    function onMouseEnter(): void {
        popupShown = true;
        clearTimeout(timer);
    }

    function onMouseLeave(): void {
        clearTimeout(timer);
        timer = window.setTimeout(() => {
            popupShown = false;
        }, 200);
    }

    function selectLanguage(language: string): void {
        setLanguage(language);
    }
</script>

<div
    class="language-selector"
    on:mouseenter={onMouseEnter}
    on:mouseleave={onMouseLeave}
>
    <button
        class="language-selector__button"
        aria-haspopup="true"
        aria-expanded={popupShown ? 'true' : 'false'}
        aria-label={$l10n('languageChooser')}
    >
        {$l10n('name')}
    </button>

    {#if popupShown}
        <div class="language-selector__popup" transition:fly={{y: 20, duration: 150}}>
            <ul class="language-selector__list">
                {#each languagesList() as language}
                    <li class="language-selector__item">
                        <button
                            class="language-selector__item-button"
                            class:language-selector__item-button_selected={language === $lang}
                            on:click={() => selectLanguage(language)}
                        >
                            {$l10n('name', language)}
                        </button>
                    </li>
                {/each}
            </ul>
        </div>
     {/if}
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

    .language-selector__popup {
        position: absolute;
        top: 100%;
        right: 0;
        padding: 10px 10px;
        border-radius: 22px;
        background: var(--bg-primary);
        box-shadow: 0 0 20px 2px rgba(0, 0, 0, .15);
    }

    .language-selector__list {
        display: flex;
        flex-direction: column;
        gap: 4px;
        margin: 0;
        padding: 0;
        list-style: none;
    }

    .language-selector__item-button {
        box-sizing: border-box;
        width: 100%;
        margin: 0;
        padding: 4px 32px 4px 16px;
        font: inherit;
        background: none;
        border: 1px solid transparent;
        border-radius: 1024px;
        appearance: none;
        color: inherit;
        text-align: left;
        cursor: pointer;

        transition: .15s ease-in-out;
        transition-property: border-color, color;
    }

    .language-selector__item-button_selected {
        background: var(--accent0);
        color: var(--accent0-text);
    }

    .language-selector__item-button:not(.language-selector__item-button_selected):hover {
        border-color: var(--accent0);
        color: var(--accent0);
    }
</style>
