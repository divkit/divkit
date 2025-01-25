import type { DivExtension, DivExtensionContext } from '../../typings/common';

export type MarkdownProcessor = (markdown: string) => string;

export interface MarkdownOptions {
    cssClass?: string;
}

export function markdownExtensionBuilder(process: MarkdownProcessor, options: MarkdownOptions = {}) {
    return class Markdown implements DivExtension {
        private prevDOM: Node | null = null;

        private recalc(node: HTMLElement, context: DivExtensionContext): void {
            const textWrapper = node.firstElementChild;
            const firstRange = textWrapper?.firstElementChild;
            if (!firstRange) {
                return;
            }

            this.prevDOM = textWrapper.cloneNode(true);

            const text = context.getComponentProperty<string>('text') || '';

            const html = process(text);
            const div = document.createElement('div');
            div.innerHTML = html;

            if (options?.cssClass) {
                div.classList.add(options.cssClass);
            }

            const children = Array.from(textWrapper.childNodes);
            for (let i = 0, len = children.length; i < len; ++i) {
                const node = children[i];
                if (node.nodeType !== 1 || node !== firstRange) {
                    textWrapper.removeChild(node);
                }
            }

            firstRange.innerHTML = '';
            firstRange.appendChild(div);
        }
        mountView(node: HTMLElement, context: DivExtensionContext): void {
            this.recalc(node, context);
        }
        updateView(node: HTMLElement, context: DivExtensionContext): void {
            this.recalc(node, context);
        }
        unmountView(node: HTMLElement): void {
            if (this.prevDOM) {
                const textWrapper = node.firstElementChild;
                if (textWrapper) {
                    textWrapper.replaceWith(this.prevDOM);
                }

                this.prevDOM = null;
            }
        }
    };
}
