import { getTypeBySchemaName, schema, translation } from './schema';
import { namedTemplates } from './templates';

export const DEFAULT_ALLOWED_COMPONENTS = [
    'image',
    'gif',
    'text',
    'container',
    'gallery',
    'separator'
];

const MAP = (schema.div.anyOf || []).reduce((acc, item) => {
    if (item.$ref) {
        const type = getTypeBySchemaName(item.$ref.replace(/\.json$/, '')) as string;

        acc[type] = translation(item.$description || '');
    }

    return acc;
}, {} as Record<string, Record<string, string>>);

export interface ComponentListItem {
    name?: string;
    nameKey?: string;
    type: string;
    description?: Record<string, string> | undefined;
}

export const smallComponentsList: ComponentListItem[] = DEFAULT_ALLOWED_COMPONENTS.map(type => {
    return {
        nameKey: `components.${type}`,
        type,
        description: MAP[type]
    };
});
export const additionalComponentsList: ComponentListItem[] = Object.keys(MAP)
    .filter(it => !DEFAULT_ALLOWED_COMPONENTS.includes(it))
    .map(type => {
        return {
            nameKey: `components.${type}`,
            type,
            description: MAP[type]
        };
    });
export const fullComponentsList: ComponentListItem[] = Object.keys(MAP).map(type => {
    return {
        nameKey: `components.${type}`,
        type,
        description: MAP[type]
    };
});

for (const key in namedTemplates) {
    const obj = namedTemplates[key];
    if (obj.visible) {
        if (obj.inShortList) {
            smallComponentsList.push({
                nameKey: obj.nameKey,
                type: key,
                description: obj.description
            });
        } else {
            additionalComponentsList.push({
                nameKey: obj.nameKey,
                type: key,
                description: obj.description
            });
        }
        fullComponentsList.push({
            nameKey: obj.nameKey,
            type: key,
            description: obj.description
        });
    }
}
