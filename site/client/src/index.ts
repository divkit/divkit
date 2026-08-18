import { mount } from 'svelte';
import App from './components/App.svelte';
import './styles/monaco-fixes.css';

mount(App, {
    target: document.body,
});
