import { SvelteComponent } from 'svelte';
import Text from './text.svelte';
import Container from './container.svelte';
import Separator from './separator.svelte';
import Image from './image.svelte';
import Grid from './grid.svelte';
import Gallery from './gallery.svelte';
import Tabs from './tabs.svelte';
import State from './state.svelte';
import Pager from './pager.svelte';
import Indicator from './indicator.svelte';
import Slider from './slider.svelte';
import Input from './input.svelte';

export const TYPE_MAP: Record<string, typeof SvelteComponent> = {
    text: Text,
    container: Container,
    separator: Separator,
    image: Image,
    gif: Image,
    grid: Grid,
    gallery: Gallery,
    tabs: Tabs,
    state: State,
    pager: Pager,
    indicator: Indicator,
    slider: Slider,
    input: Input
};
