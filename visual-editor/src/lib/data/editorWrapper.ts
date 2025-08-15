import type { EditorInstance, EditorOptions } from '../../lib';
import type { TypedRange } from '../data/editor';
import type { Loc } from '../utils/stringifyWithLoc';

export function editorFabric(opts: EditorOptions): EditorInstance {
    let editor: EditorInstance;
    let alive = true;
    let savedValue: string | undefined;
    let savedTheme: 'light' | 'dark' | undefined;
    let savedReadOnly: boolean | undefined;
    let savedLoc: Loc | undefined;
    let savedTypedRanges: TypedRange[] | undefined;

    import('../data/editor').then(({ createEditor }) => {
        if (!alive) {
            return;
        }

        editor = createEditor(opts);
        if (savedValue) {
            editor.setValue(savedValue);
        }
        if (savedTheme) {
            editor.setTheme(savedTheme);
        }
        if (savedReadOnly) {
            editor.setReadOnly(savedReadOnly);
        }
        if (savedLoc) {
            editor.revealLoc(savedLoc);
        }
        if (savedTypedRanges) {
            editor.decorateRanges(savedTypedRanges);
        }
    });

    return {
        setValue(value) {
            if (editor) {
                editor.setValue(value);
            } else {
                savedValue = value;
            }
        },
        setTheme(theme) {
            if (editor) {
                editor.setTheme(theme);
            } else {
                savedTheme = theme;
            }
        },
        setReadOnly(readOnly) {
            if (editor) {
                editor.setReadOnly(readOnly);
            } else {
                savedReadOnly = readOnly;
            }
        },
        revealLoc(loc) {
            if (editor) {
                editor.revealLoc(loc);
            } else {
                savedLoc = loc;
            }
        },
        decorateRanges(typedRanges) {
            if (editor) {
                editor.decorateRanges(typedRanges);
            } else {
                savedTypedRanges = typedRanges;
            }
        },
        isFocused() {
            return editor?.isFocused() || false;
        },
        destroy() {
            alive = false;
            editor?.destroy();
        }
    };
}
