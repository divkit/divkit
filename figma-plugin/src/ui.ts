import App from './views/App.svelte';

export const app = new App({
    target: document.getElementById('app') as HTMLElement,
});

export default app;
