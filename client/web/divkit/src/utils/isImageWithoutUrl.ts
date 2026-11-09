import type { MaybeMissing } from '../expressions/json';
import type { DivBaseData } from '../types/base';
import type { DivImageData } from '../types/image';

export function isImageWithoutUrl(json: MaybeMissing<DivBaseData>): boolean {
    const image = json as MaybeMissing<DivImageData>;
    return image.type === 'image' && (image.image_url === null || image.image_url === undefined) ||
        image.type === 'gif' && (image.gif_url === null || image.gif_url === undefined);
}
