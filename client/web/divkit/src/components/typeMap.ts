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
import Select from './select/Select.svelte';

export const TYPE_MAP: Record<string, typeof SvelteComponent> = {};

if (process.env.ENABLE_COMPONENT_TEXT || process.env.ENABLE_COMPONENT_TEXT === undefined) {
    TYPE_MAP.text = Text;
}

if (process.env.ENABLE_COMPONENT_CONTAINER || process.env.ENABLE_COMPONENT_CONTAINER === undefined) {
    TYPE_MAP.container = Container;
}

if (process.env.ENABLE_COMPONENT_SEPARATOR || process.env.ENABLE_COMPONENT_SEPARATOR === undefined) {
    TYPE_MAP.separator = Separator;
}

if (process.env.ENABLE_COMPONENT_IMAGE || process.env.ENABLE_COMPONENT_IMAGE === undefined) {
    TYPE_MAP.image = Image;
}

if (process.env.ENABLE_COMPONENT_GIF || process.env.ENABLE_COMPONENT_GIF === undefined) {
    TYPE_MAP.gif = Image;
}

if (process.env.ENABLE_COMPONENT_GRID || process.env.ENABLE_COMPONENT_GRID === undefined) {
    TYPE_MAP.grid = Grid;
}

if (process.env.ENABLE_COMPONENT_GALLERY || process.env.ENABLE_COMPONENT_GALLERY === undefined) {
    TYPE_MAP.gallery = Gallery;
}

if (process.env.ENABLE_COMPONENT_TABS || process.env.ENABLE_COMPONENT_TABS === undefined) {
    TYPE_MAP.tabs = Tabs;
}

if (process.env.ENABLE_COMPONENT_STATE || process.env.ENABLE_COMPONENT_STATE === undefined) {
    TYPE_MAP.state = State;
}

if (process.env.ENABLE_COMPONENT_PAGER || process.env.ENABLE_COMPONENT_PAGER === undefined) {
    TYPE_MAP.pager = Pager;
}

if (process.env.ENABLE_COMPONENT_INDICATOR || process.env.ENABLE_COMPONENT_INDICATOR === undefined) {
    TYPE_MAP.indicator = Indicator;
}

if (process.env.ENABLE_COMPONENT_SLIDER || process.env.ENABLE_COMPONENT_SLIDER === undefined) {
    TYPE_MAP.slider = Slider;
}

if (process.env.ENABLE_COMPONENT_INPUT || process.env.ENABLE_COMPONENT_INPUT === undefined) {
    TYPE_MAP.input = Input;
}

if (process.env.ENABLE_COMPONENT_SELECT || process.env.ENABLE_COMPONENT_SELECT === undefined) {
    TYPE_MAP.select = Select;
}
