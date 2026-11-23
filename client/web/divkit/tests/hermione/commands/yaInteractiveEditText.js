'use strict';

module.exports = async function(step) {
    if (step.target?.type !== 'div_id') {
        throw new Error(`Unsupported edit_text target type: ${step.target?.type}`);
    }
    if (!['replace', 'append'].includes(step.mode) || typeof step.text !== 'string') {
        throw new Error(`Invalid edit_text step: ${JSON.stringify(step)}`);
    }

    const queryTarget = () => this.execute(id => {
        const targets = [...document.querySelectorAll('[data-test-id]')]
            .filter(element => element.getAttribute('data-test-id') === id);
        if (targets.length > 1) {
            throw new Error(`Expected one edit_text target for id '${id}', found ${targets.length}`);
        }
        const target = targets[0];
        const selector = 'input, textarea, [contenteditable="true"]';
        return target?.matches(selector) ? target : target?.querySelector(selector) || null;
    }, step.target.id);

    await this.waitUntil(() => queryTarget(), {
        timeout: 3000,
        timeoutMsg: `Editable target did not appear for id '${step.target.id}'`,
    });
    const target = await queryTarget();
    const element = await this.$(target);
    await element.waitForDisplayed();
    await element.click();
    const keyboardSelection = await this.execute((input, mode) => {
        input.focus();
        if (input instanceof HTMLInputElement && ['number', 'email'].includes(input.type)) {
            return true;
        }
        if (input instanceof HTMLInputElement || input instanceof HTMLTextAreaElement) {
            const end = input.value.length;
            input.setSelectionRange(mode === 'replace' ? 0 : end, end);
        } else {
            const range = document.createRange();
            range.selectNodeContents(input);
            if (mode === 'append') {
                range.collapse(false);
            }
            const selection = window.getSelection();
            selection.removeAllRanges();
            selection.addRange(range);
        }
        return false;
    }, target, step.mode);
    if (keyboardSelection) {
        if (step.mode === 'replace') {
            const modifier = await this.execute(() => /Mac/.test(navigator.platform) ? 'Meta' : 'Control');
            await this.keys([modifier, 'a']);
        } else {
            await this.keys('End');
        }
    }

    if (step.text) {
        await element.addValue(step.text);
    } else if (step.mode === 'replace') {
        await this.keys('Backspace');
    }
};
