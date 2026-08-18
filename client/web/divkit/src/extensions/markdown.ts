import type { DivExtension, DivExtensionContext } from '../../typings/common';

export type MarkdownProcessor = (markdown: string) => string;

export interface MarkdownOptions {
    cssClass?: string;
}

export function markdownExtensionBuilder(process: MarkdownProcessor, options: MarkdownOptions = {}) {
    return class Markdown implements DivExtension {
        private prevDOM: HTMLElement | null = null;
        private clonedRange: HTMLElement | null = null;

        private recalc(node: HTMLElement, context: DivExtensionContext): void {
            const textWrapper = node.firstElementChild;
            const firstRange = textWrapper?.firstElementChild;
            if (!firstRange || !(firstRange instanceof HTMLElement)) {
                return;
            }

            if (!this.prevDOM) {
                this.prevDOM = firstRange;
                this.clonedRange = firstRange.cloneNode(true) as HTMLElement;
                Array.from(textWrapper.children || []).forEach(child => {
                    if (child instanceof HTMLElement) {
                        child.style.display = 'none';
                    }
                });
                textWrapper.appendChild(this.clonedRange);
            }
            if (!this.clonedRange) {
                return;
            }

            const text = context.getComponentProperty<string>('text') || '';

            const html = process(text);
            const div = document.createElement('div');
            div.innerHTML = html;

            if (options?.cssClass) {
                div.classList.add(options.cssClass);
            }

            this.clonedRange.innerHTML = '';
            this.clonedRange.appendChild(div);
        }
        mountView(node: HTMLElement, context: DivExtensionContext): void {
            this.recalc(node, context);
        }
        updateView(node: HTMLElement, context: DivExtensionContext): void {
            this.recalc(node, context);
        }
        unmountView(node: HTMLElement): void {
            if (this.prevDOM && this.clonedRange) {
                const textWrapper = node.firstElementChild;
                Array.from(textWrapper?.children || []).forEach(child => {
                    if (child instanceof HTMLElement) {
                        child.style.display = '';
                    }
                });
                this.clonedRange.remove();
                this.prevDOM.style.display = '';
            }
            this.prevDOM = null;
            this.clonedRange = null;
        }
    };
}
