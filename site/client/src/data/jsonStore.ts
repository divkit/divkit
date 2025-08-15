import { get, writable } from 'svelte/store';
import type { ViewerError } from './externalViewers';
import { valueStore } from './valueStore';
import { editorMode } from './editorMode';

export const convertToJsonErrorsStore = writable<ViewerError[]>([]);
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const jsonStore = writable<any>({});

function updateJson(): void {
    const val = get(valueStore);
    const mode = get(editorMode);

    if (mode === 'json') {
        try {
            jsonStore.set(JSON.parse(val));
            convertToJsonErrorsStore.set([]);
        } catch (err) {
            const stack = (err instanceof Error) && err.stack || '';
            convertToJsonErrorsStore.set([{
                message: String(err),
                stack: stack.split('\n')
            }]);
            jsonStore.set({});
        }
    }
}

valueStore.subscribe(updateJson);
editorMode.subscribe(updateJson);
