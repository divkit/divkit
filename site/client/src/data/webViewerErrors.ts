import { writable } from 'svelte/store';
import type { ViewerError } from './externalViewers';

export const webViewerErrors = writable<ViewerError[]>([]);
