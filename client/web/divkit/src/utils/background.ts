import type { AlignmentHorizontal, AlignmentVertical } from '../types/alignment';
import type {
    Background,
    GradientBackground,
    ImageBackground,
    SolidBackground,
    RadialBackground,
    RadialGradientCenter,
    GradientColorPoint
} from '../types/background';
import type { ImageScale } from '../types/imageScale';
import type { MaybeMissing } from '../expressions/json';
import { correctColor } from './correctColor';
import { htmlFilter } from './htmlFilter';
import { Truthy } from './truthy';
import { pxToEmWithUnits } from './pxToEm';

export function getBackground(bgs: MaybeMissing<Background>[], direction: 'ltr' | 'rtl'): {
    color?: string;
    image?: string;
    size?: string;
    position?: string;
} {
    if (bgs.length === 1 && bgs[0].type === 'solid') {
        return solidUnique({
            bg: bgs[0]
        });
    }

    const res = bgs
        .map(bg => {
            if (bg.type === 'solid') {
                return solid({
                    bg
                });
            } else if (bg.type === 'gradient') {
                return gradient({
                    bg
                });
            } else if (bg.type === 'image') {
                return image({
                    bg,
                    direction
                });
            } else if (bg.type === 'radial_gradient') {
                return radial({
                    bg
                });
            }
        })
        .filter(Truthy)
        .reverse()
        .reduce(function(acc, item) {
            acc.image.push(item.image);
            acc.size.push(item.size || 'auto');
            acc.position.push(item.pos || '50% 50%');

            return acc;
        }, {
            image: [] as string[],
            size: [] as string[],
            position: [] as string[]
        });

    return {
        image: res.image.join(','),
        size: res.size.join(','),
        position: res.position.join(',')
    };
}

function solid(opts: {
    bg: MaybeMissing<SolidBackground>;
}): {
    size: string | undefined;
    pos: string | undefined;
    image: string;
} {
    const color = correctColor(opts.bg.color || 'transparent');

    return {
        size: undefined,
        pos: undefined,
        image: `linear-gradient(to bottom,${color},${color})`
    };
}

function solidUnique(opts: {
    bg: MaybeMissing<SolidBackground>;
}): {
    color: string;
    size: string;
    position: string;
} {
    const color = correctColor(opts.bg.color || 'transparent');

    return {
        color,
        size: 'auto',
        position: '50% 50%'
    };
}

function colorMapToList(colorMap: MaybeMissing<GradientColorPoint[]>, offset?: string): string | undefined {
    if (!colorMap.every(it => it.color && typeof it.position === 'number' && it.position >= 0 && it.position <= 1)) {
        return;
    }

    const colors = colorMap as {
        color: string;
        position: number;
    }[];

    const sortedColors = [...colors].sort((a, b) => {
        if (Math.abs(a.position - b.position) < 1e-6) {
            return 0;
        }
        return a.position - b.position;
    });

    return sortedColors
        .map(color => `${correctColor(color.color)} ${offsetGradientStop((color.position * 100).toFixed(2), offset)}`)
        .join(',');
}

function offsetGradientStop(position: string, offset?: string): string {
    return offset ? `calc(${position}% + ${offset})` : `${position}%`;
}

function createGradientColorList(
    bg: MaybeMissing<GradientBackground | RadialBackground>, offset?: string
): string | undefined {
    if (!Array.isArray(bg.colors) && !Array.isArray(bg.color_map)) {
        return;
    }

    if (bg.color_map) {
        return colorMapToList(bg.color_map, offset);
    }

    const colors = bg.colors?.filter(Truthy);
    return colors?.map((color, index) => {
        const stop = offset ? ` ${offsetGradientStop(String(index * 100 / Math.max(1, colors.length - 1)), offset)}` : '';
        return correctColor(color) + stop;
    }).join(',');
}

function gradient(opts: {
    bg: MaybeMissing<GradientBackground>;
    offset?: string;
}): {
    size: string | undefined;
    pos: string | undefined;
    image: string;
} | undefined {
    const list = createGradientColorList(opts.bg, opts.offset);
    if (!list) {
        return;
    }

    return {
        size: undefined,
        pos: undefined,
        image: `linear-gradient(${90 - Number(opts.bg.angle || 0)}deg,${list})`
    };
}

const RELATIVE_SIZE_MAP = {
    nearest_corner: 'closest-corner',
    farthest_corner: 'farthest-corner',
    nearest_side: 'closest-side',
    farthest_side: 'farthest-side'
};

function radialCenterToCss(center: MaybeMissing<RadialGradientCenter> | undefined): string {
    if (center && typeof center === 'object' && 'type' in center && center.value !== undefined) {
        if (center.type === 'fixed') {
            return pxToEmWithUnits(center.value);
        } else if (center.type === 'relative') {
            return `${Number(center.value) * 100}%`;
        }
    }

    return '50%';
}

function radial(opts: {
    bg: MaybeMissing<RadialBackground>;
    offset?: string;
    radius?: string;
}): {
    size: string | undefined;
    pos: string | undefined;
    image: string;
} | undefined {
    const list = createGradientColorList(opts.bg);
    if (!list) {
        return;
    }

    const sizeVal = opts.bg.radius;
    let size;
    if (sizeVal && typeof sizeVal === 'object' && 'type' in sizeVal && sizeVal.value !== undefined) {
        if (sizeVal.type === 'fixed') {
            size = pxToEmWithUnits(sizeVal.value);
        } else if (sizeVal.type === 'relative') {
            size = RELATIVE_SIZE_MAP[sizeVal.value];
        }
    }

    const staticCenterX = radialCenterToCss(opts.bg.center_x);
    const centerX = opts.offset ? `calc(${staticCenterX} + ${opts.offset})` : staticCenterX;
    const centerY = radialCenterToCss(opts.bg.center_y);

    return {
        size: undefined,
        pos: undefined,
        image:
            'radial-gradient(' +
            `circle ${opts.radius || size || 'farthest-corner'} at ${centerX} ${centerY}` +
            ',' +
            list +
            ')'
    };
}

export function getAnimatedGradientBackground(
    bg: MaybeMissing<GradientBackground | RadialBackground>
): string | undefined {
    const offset = 'var(--divkit-text-gradient-offset)';
    if (bg.type === 'gradient') {
        return gradient({ bg, offset })?.image;
    }
    if (bg.type === 'radial_gradient') {
        return radial({ bg, offset, radius: 'var(--divkit-text-gradient-radius)' })?.image;
    }
}

function image(opts: {
    bg: MaybeMissing<ImageBackground>;
    direction: 'ltr' | 'rtl'
}): {
    size: string | undefined;
    pos: string | undefined;
    image: string;
} | undefined {
    const image = opts.bg?.image_url;

    if (!image) {
        return;
    }

    return {
        size: imageSize(opts.bg.scale),
        pos: imagePos(opts.bg, opts.direction),
        image: 'url("' + htmlFilter(image) + '")'
    };
}

export function imageSize(scale?: ImageScale): string {
    if (scale === 'fit') {
        return 'contain';
    } else if (scale === 'stretch') {
        return 'fill';
    } else if (scale === 'no_scale') {
        return 'none';
    }

    // 'fill' and default
    return 'cover';
}

export function objectFitToBackgroundSize(objectFit: string): string {
    if (objectFit === 'none') {
        return 'auto';
    } else if (objectFit === 'fill') {
        return '100% 100%';
    }
    return objectFit;
}

export function imagePos(obj: {
    content_alignment_horizontal?: AlignmentHorizontal;
    content_alignment_vertical?: AlignmentVertical;
}, direction: 'ltr' | 'rtl'): string {
    let hpos: string;
    let vpos: string;

    if (
        obj.content_alignment_horizontal === 'left' ||
        direction === 'ltr' && obj.content_alignment_horizontal === 'start' ||
        direction === 'rtl' && obj.content_alignment_horizontal === 'end'
    ) {
        hpos = '0%';
    } else if (
        obj.content_alignment_horizontal === 'right' ||
        direction === 'ltr' && obj.content_alignment_horizontal === 'end' ||
        direction === 'rtl' && obj.content_alignment_horizontal === 'start'
    ) {
        hpos = '100%';
    } else {
        hpos = '50%';
    }

    if (obj.content_alignment_vertical === 'top') {
        vpos = '0%';
    } else if (obj.content_alignment_vertical === 'bottom') {
        vpos = '100%';
    } else {
        vpos = '50%';
    }

    return hpos + ' ' + vpos;
}
