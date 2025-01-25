<script lang="ts">
    import { getContext } from 'svelte';
    import RadioSelector from './controls/RadioSelector.svelte';
    import Props from './Props.svelte';
    import TextEditor from './TextEditor.svelte';
    import { LANGUAGE_CTX, type LanguageContext } from '../ctx/languageContext';
    import propsIcon from '../../assets/props.svg?url';
    import complexPropsIcon from '../../assets/complexProps.svg?url';
    import codeIcon from '../../assets/code.svg?url';

    const { l10n } = getContext<LanguageContext>(LANGUAGE_CTX);

    let panel = 'props';
</script>

<div class="props-and-code">
    <RadioSelector bind:value={panel} name="props-code" options={[{
        text: $l10n('componentProperties'),
        value: 'props',
        icon: propsIcon
    }, {
        text: $l10n('complexComponentProperties'),
        value: 'complexProps',
        icon: complexPropsIcon
    }, {
        text: $l10n('code'),
        value: 'code',
        icon: codeIcon
    }]} theme="normal" />
</div>

{#if panel === 'props' || panel === 'complexProps'}
    <Props showComplex={panel === 'complexProps'} />
{:else}
    <TextEditor />
{/if}

<style>
    .props-and-code {
        margin: 16px 16px 4px;
    }
</style>
