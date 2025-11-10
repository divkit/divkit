declare module '*.svelte' {
    import { ComponentType } from 'svelte';
    const content: ComponentType;
    export = content;
}

declare module '*.svg?raw' {
    const content: string;
    export = content;
}
