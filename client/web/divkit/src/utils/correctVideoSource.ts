import type { MaybeMissing } from '../expressions/json';
import type { VideoSource } from '../types/video';

export interface PreparedVideoSource {
    src: string;
    type?: string;
}

export function correctVideoSource(
    data: MaybeMissing<VideoSource>[] | undefined,
    defaultValue: PreparedVideoSource[]
): PreparedVideoSource[] {
    if (Array.isArray(data) && data.length) {
        return data.filter(it => {
            return it?.type === 'video_source' && typeof it.url === 'string' && typeof it.mime_type === 'string';
        }).map(it => {
            const res: PreparedVideoSource = {
                src: it.url as string
            };

            if (it.mime_type) {
                res.type = it.mime_type;
            }

            return res;
        });
    }

    return defaultValue;
}
