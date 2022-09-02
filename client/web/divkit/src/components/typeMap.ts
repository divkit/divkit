import { SvelteComponent } from 'svelte';
import Text from './text/Text.svelte';
import Container from './container/Container.svelte';
import Separator from './separator/Separator.svelte';
import Image from './image/Image.svelte';
import Grid from './grid/Grid.svelte';
import Gallery from './gallery/Gallery.svelte';
import Tabs from './tabs/Tabs.svelte';
import State from './state/State.svelte';
import Pager from './pager/Pager.svelte';
import Indicator from './indicator/Indicator.svelte';
import Slider from './slider/Slider.svelte';
import Input from './input/Input.svelte';

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
