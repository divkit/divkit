import { namedTemplates } from '../data/templates';
import textIcon from '../../assets/components/text.svg?url';
import containerVerticalIcon from '../../assets/components/container-vertical.svg?url';
import containerHorizontalIcon from '../../assets/components/container-horizontal.svg?url';
import containerOverlapIcon from '../../assets/components/container-overlap.svg?url';
import gifIcon from '../../assets/components/gif.svg?url';
import imageIcon from '../../assets/components/image.svg?url';
import indicatorIcon from '../../assets/components/indicator.svg?url';
import separatorIcon from '../../assets/components/separator.svg?url';
import galleryHorizontalIcon from '../../assets/components/gallery-horizontal.svg?url';
import galleryVerticalIcon from '../../assets/components/gallery-vertical.svg?url';
import switchIcon from '../../assets/components/switch.svg?url';
import gridIcon from '../../assets/components/grid.svg?url';
import pagerIcon from '../../assets/components/pager.svg?url';
import tabsIcon from '../../assets/components/tabs.svg?url';
import stateIcon from '../../assets/components/state.svg?url';
import customIcon from '../../assets/components/custom.svg?url';
import sliderIcon from '../../assets/components/slider.svg?url';
import inputIcon from '../../assets/components/input.svg?url';
import selectIcon from '../../assets/components/select.svg?url';
import videoIcon from '../../assets/components/video.svg?url';
import unknownIcon from '../../assets/components/unknown.svg?url';

const MAP = {
    text: textIcon,
    container: containerVerticalIcon,
    gallery: galleryHorizontalIcon,
    gif: gifIcon,
    image: imageIcon,
    indicator: indicatorIcon,
    separator: separatorIcon,
    switch: switchIcon,
    grid: gridIcon,
    pager: pagerIcon,
    tabs: tabsIcon,
    state: stateIcon,
    custom: customIcon,
    slider: sliderIcon,
    input: inputIcon,
    select: selectIcon,
    video: videoIcon
};

const MAP_BY_ORIENTATION = {
    container: {
        horizontal: containerHorizontalIcon,
        overlap: containerOverlapIcon
    },
    gallery: {
        vertical: galleryVerticalIcon
    }
};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function componentIcon(type: string, json?: any): string {
    if (type in MAP) {
        const orientation = json?.orientation;

        if (orientation && type in MAP_BY_ORIENTATION) {
            const submap = MAP_BY_ORIENTATION[type as keyof typeof MAP_BY_ORIENTATION];
            if (orientation in submap) {
                return submap[orientation as keyof typeof submap];
            }
        }

        return MAP[type as keyof typeof MAP];
    }
    if (type in namedTemplates && namedTemplates[type].icon) {
        return namedTemplates[type].icon;
    }

    return unknownIcon;
}
