import type { Shortcut } from './shortcut';
import type { Handler } from './keybinder';
import { globalHandler } from './keybinder';

export type ShortcutList = [Shortcut, Handler, number?][];

export function shortcuts(node: HTMLElement, list: ShortcutList) {
    const unbind = list.map(it => {
        return globalHandler(it[0], (event: KeyboardEvent) => {
            const isNodeInsideDialog = Boolean(node.closest('dialog'));
            const isEventInsideDialog = Boolean(document.querySelector('dialog'));

            if (isNodeInsideDialog === isEventInsideDialog) {
                it[1](event);
            } else {
                return false;
            }
        }, it[2]).unbind;
    });

    return {
        destroy() {
            for (const item of unbind) {
                item();
            }
        }
    };
}
