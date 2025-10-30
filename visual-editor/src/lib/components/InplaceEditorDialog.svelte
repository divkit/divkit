<script lang="ts">
    import { afterUpdate } from 'svelte';
    import InplaceEditor from './InplaceEditor.svelte';
    import type { InplaceEditorShowProps } from '../ctx/appContext';

    function onClose(): void {
        if (!isShown || !editor) {
            return;
        }

        isShown = false;
        currentProps.callback(editor.getValue());
    }

    export function show(props: InplaceEditorShowProps): void {
        currentProps = props;
        isShown = true;
    }

    export function hide(): void {
        isShown = false;
    }

    export function setPosition(left: string, top: string): void {
        currentProps.style.left = left;
        currentProps.style.top = top;
        currentProps = currentProps;
    }

    let currentProps: InplaceEditorShowProps;
    let isShown = false;
    let dialog: HTMLDialogElement;
    let editor: InplaceEditor | undefined;

    afterUpdate(() => {
        if (dialog && isShown && !dialog.open) {
            dialog.showModal();
        }
    });

    function onEditorClose(): void {
        onClose();
    }

    function onEditorResize(): void {
        currentProps.onResoze({
            width: dialog.offsetWidth,
            height: dialog.offsetHeight
        });
    }

    function onDialogClick(event: MouseEvent): void {
        if (event.target === dialog) {
            onClose();
        }
    }
</script>

{#if isShown}
    <dialog
        class="inplace-editor-dialog"
        style:left={currentProps.style.left}
        style:top={currentProps.style.top}
        style:width={currentProps.style.width}
        style:max-width={currentProps.style.maxWidth}
        style:font-size={currentProps.style.fontSize}
        bind:this={dialog}
        on:close={onClose}
        on:click={onDialogClick}
        on:keydown|stopPropagation
    >
        <InplaceEditor
            text={currentProps.text}
            json={currentProps.json}
            leaf={currentProps.leaf}
            disabled={currentProps.disabled}
            textDisabled={currentProps.textDisabled}
            rotation={currentProps.rotation}
            scale={currentProps.scale}
            bind:this={editor}
            on:close={onEditorClose}
            on:resize={onEditorResize}
        />
    </dialog>
{/if}

<style>
    .inplace-editor-dialog {
        margin: 0;
        padding: 0;
        border: none;
        background: none;
        overflow: visible;
    }

    .inplace-editor-dialog::backdrop {
        background: none;
    }
</style>
