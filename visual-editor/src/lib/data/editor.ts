import { EditorView, basicSetup } from 'codemirror';
import { Decoration, drawSelection, dropCursor, keymap, type DecorationSet, type ViewUpdate } from '@codemirror/view';
import { defaultKeymap, history, historyKeymap } from '@codemirror/commands';
import { closeBrackets, autocompletion, closeBracketsKeymap, completionKeymap } from '@codemirror/autocomplete';
import { highlightSelectionMatches, searchKeymap } from '@codemirror/search';
import { Compartment, EditorState, StateField, StateEffect, Transaction } from '@codemirror/state';
import { json, jsonParseLinter } from '@codemirror/lang-json';
import { linter, type Diagnostic } from '@codemirror/lint';
import { oneDark } from '@codemirror/theme-one-dark';
import { styleTags, tags as t } from '@lezer/highlight';
import { LRLanguage, LanguageSupport, bracketMatching, defaultHighlightStyle, syntaxHighlighting, syntaxTree } from '@codemirror/language';
import type { Loc, Range } from '../utils/stringifyWithLoc';
import type { EditorInstance } from '../../lib';
import { parser } from '../grammar/div';
import { submit } from '../utils/keybinder/shortcuts';

const parserWithMetadata = parser.configure({
    props: [
        styleTags({
            'Identifier StrictIdentifier': t.variableName,
            Boolean: t.bool,
            String: t.string,
            'Number Integer': t.number,
            '( )': t.paren,
            '@{ }': t.brace
        })
    ]
});

const exampleLanguage = LRLanguage.define({
    parser: parserWithMetadata
});

function divLangauge() {
    return new LanguageSupport(exampleLanguage, []);
}

export type RangeType = 'highlight' | 'select';

export interface TypedRange {
    range: Range;
    type: 'highlight' | 'select';
}

const darkTheme = oneDark;
const lightTheme = EditorView.baseTheme({});

const themeExt = EditorView.theme({
    '&': {
        backgroundColor: 'var(--background-primary) !important'
    },
    '& .cm-gutters': {
        backgroundColor: 'var(--fill-opaque-1) !important'
    },
    '& .cm-search': {
        fontSize: '20px',
        overflow: 'auto'
    }
});

const divThemeExt = EditorView.theme({
    '&': {
        backgroundColor: 'var(--background-primary) !important',
        color: 'var(--text-primary) !important'
    },
    '& .cm-gutters': {
        backgroundColor: 'var(--fill-opaque-1) !important'
    },
    '& .cm-search': {
        fontSize: '20px',
        overflow: 'auto'
    },
    '& cm-selectionBackground': {
        backgroundColor: 'var(--fill-transparent-2) !important'
    }
});

const addHighlight = StateEffect.define<{ from: number, to: number, type: RangeType }>({
    map: ({ from, to, type }, change) => ({
        type: type,
        from: change.mapPos(from),
        to: change.mapPos(to)
    })
});

const clearHighlight = StateEffect.define<object>({
    map: ({ }, _change) => ({})
});

const highlightField = StateField.define<DecorationSet>({
    create() {
        return Decoration.none;
    },
    update(highlights, tr: Transaction) {
        highlights = highlights.map(tr.changes);

        const hasClear = tr.effects.some(effect => effect.is(clearHighlight));
        const hasHighlight = tr.effects.some(effect => effect.is(addHighlight));

        if (hasClear || hasHighlight) {
            highlights = highlights.update({
                filter() {
                    return false;
                }
            });
        }

        const len = tr.state.doc.length;

        for (const effect of tr.effects) {
            if (effect.is(addHighlight)) {
                const startLine = tr.state.doc.lineAt(Math.min(len, effect.value.from)).number;
                const endLine = tr.state.doc.lineAt(Math.min(len, effect.value.to)).number;
                const list = [];
                for (let line = startLine; line <= endLine; ++line) {
                    list.push(
                        (effect.value.type === 'highlight' ? highlightMark : selectMark)
                            .range(tr.state.doc.line(line).from)
                    );
                }
                highlights = highlights.update({
                    add: list
                });
            }
        }
        return highlights;
    },
    provide: f => EditorView.decorations.from(f)
});

const highlightMark = Decoration.line({ class: 'cm-highlight' });
const selectMark = Decoration.line({ class: 'cm-select' });

export function createEditor(opts: {
    node: HTMLElement;
    shadowRoot?: ShadowRoot | undefined;
    value: string;
    theme: 'light' | 'dark';
    readOnly?: boolean;
    onChange(value: string): void;
    onOver(offset: number | null): void;
    onClick(offset: number): void;
}): EditorInstance {
    const editorTheme = new Compartment();
    const editorReadOnly = new Compartment();
    const jsonLinter = linter(jsonParseLinter());

    const editor = new EditorView({
        state: EditorState.create({
            doc: opts.value,
            extensions: [
                basicSetup,
                json(),
                jsonLinter,
                editorTheme.of(opts.theme === 'dark' ? darkTheme : lightTheme),
                themeExt,
                editorReadOnly.of(EditorState.readOnly.of(opts.readOnly || false)),
                EditorView.updateListener.of((update: ViewUpdate) => {
                    if (update.docChanged) {
                        const val = update.state.sliceDoc();

                        opts.onChange(val);
                    }
                })
            ]
        }),
        parent: opts.node,
        root: opts.shadowRoot
    });

    editor.dom.addEventListener('mousemove', (event: MouseEvent) => {
        const offset = editor.posAtCoords({
            x: event.clientX,
            y: event.clientY
        }, false);

        opts.onOver(offset);
    });

    editor.dom.addEventListener('mouseleave', () => {
        opts.onOver(null);
    });

    editor.dom.addEventListener('click', (event: MouseEvent) => {
        const offset = editor.posAtCoords({
            x: event.clientX,
            y: event.clientY
        }, false);

        opts.onClick(offset);
    });

    return {
        setValue(value: string): void {
            if (editor.state.sliceDoc() !== value) {
                editor.dispatch({
                    changes: {
                        from: 0,
                        to: editor.state.doc.length,
                        insert: value,
                    },
                });
            }
        },
        setTheme(theme: 'light' | 'dark'): void {
            editor.dispatch({
                effects: editorTheme.reconfigure(
                    theme === 'dark' ? darkTheme : lightTheme
                )
            });
        },
        setReadOnly(readOnly: boolean): void {
            editor.dispatch({
                effects: editorReadOnly.reconfigure(EditorState.readOnly.of(readOnly))
            });
        },
        revealLoc(loc: Loc): void {
            editor.dispatch({
                effects: EditorView.scrollIntoView(loc.offset, {
                    x: 'start',
                    xMargin: 40,
                    y: 'start',
                    yMargin: 40
                })
            });
        },
        decorateRanges(typedRanges: TypedRange[]): void {
            let effects;
            if (typedRanges.length) {
                effects = typedRanges.map(typedRange => addHighlight.of({
                    from: typedRange.range.start.offset,
                    to: typedRange.range.end.offset,
                    type: typedRange.type
                }));
            } else {
                effects = [clearHighlight.of({})];
            }

            if (!editor.state.field(highlightField, false)) {
                effects.push(StateEffect.appendConfig.of([
                    highlightField
                ]));
            }
            editor.dispatch({ effects });
        },
        isFocused(): boolean {
            return editor.hasFocus;
        },
        destroy(): void {
            editor.destroy();
        }
    };
}

export interface DivEditorInstance {
    setValue(value: string): void;
    setTheme(theme: 'light' | 'dark'): void;
    setReadOnly(readOnly: boolean): void;
    isFocused(): boolean;
    destroy(): void;
}

const divBasicSetup = () => [
    history(),
    drawSelection(),
    dropCursor(),
    EditorState.allowMultipleSelections.of(true),
    syntaxHighlighting(defaultHighlightStyle, { fallback: true }),
    bracketMatching(),
    closeBrackets(),
    autocompletion(),
    highlightSelectionMatches(),
    keymap.of([
        ...closeBracketsKeymap,
        ...defaultKeymap,
        ...searchKeymap,
        ...historyKeymap,
        ...completionKeymap
    ])
];

function divLint(view: EditorView): readonly Diagnostic[] {
    const diagnostics: Diagnostic[] = [];

    syntaxTree(view.state).iterate({
        enter(node) {
            if (node.type.isError) {
                diagnostics.push({
                    from: node.from,
                    to: node.to,
                    severity: 'error',
                    message: 'Syntax error.'
                });
            }
        },
    });

    return diagnostics;
}

export function createDivEditor(opts: {
    node: HTMLElement;
    shadowRoot?: ShadowRoot | undefined;
    value: string;
    theme: 'light' | 'dark';
    readOnly?: boolean;
    onChange(value: string): void;
    onEnter(): void;
}): DivEditorInstance {
    const editorTheme = new Compartment();
    const editorReadOnly = new Compartment();
    const divLinter = linter(divLint);

    const editor = new EditorView({
        state: EditorState.create({
            doc: opts.value,
            extensions: [
                divBasicSetup(),
                divLangauge(),
                divLinter,
                editorTheme.of(opts.theme === 'dark' ? darkTheme : lightTheme),
                divThemeExt,
                editorReadOnly.of(EditorState.readOnly.of(opts.readOnly || false)),
                EditorView.lineWrapping,
                EditorView.updateListener.of((update: ViewUpdate) => {
                    if (update.docChanged) {
                        const val = update.state.sliceDoc();

                        opts.onChange(val);
                    }
                })
            ]
        }),
        parent: opts.node,
        root: opts.shadowRoot,
    });

    editor.dom.addEventListener('keydown', (event: KeyboardEvent) => {
        if (submit.isPressed(event)) {
            event.preventDefault();
            opts.onEnter();
        }
    });

    editor.focus();

    return {
        setValue(value: string): void {
            if (editor.state.sliceDoc() !== value) {
                editor.dispatch({
                    changes: {
                        from: 0,
                        to: editor.state.doc.length,
                        insert: value,
                    },
                });
            }
        },
        setTheme(theme: 'light' | 'dark'): void {
            editor.dispatch({
                effects: editorTheme.reconfigure(
                    theme === 'dark' ? darkTheme : lightTheme
                )
            });
        },
        setReadOnly(readOnly: boolean): void {
            editor.dispatch({
                effects: editorReadOnly.reconfigure(EditorState.readOnly.of(readOnly))
            });
        },
        isFocused(): boolean {
            return editor.hasFocus;
        },
        destroy(): void {
            editor.destroy();
        }
    };
}
