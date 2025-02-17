export const supportsPlainTextOnly = (function supportsPlaintextEditables() {
    const div = document.createElement('div');
    div.setAttribute('contenteditable', 'plaintext-only');

    return div.contentEditable === 'plaintext-only';
})();
