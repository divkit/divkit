import { htmlFilter } from './htmlFilter';

export function prepareBase64(data: string): string {
    if (data.startsWith('data:')) {
        return htmlFilter(data);
    }
    return `data:image/jpg;base64,${htmlFilter(data)}`;
}
