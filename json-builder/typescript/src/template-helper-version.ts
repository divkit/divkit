import { createHash } from 'crypto';
import { Div } from './generated/Div';
import { ITemplates } from './template';
import { rewriteNames } from './template-helper-rewrite-names';

export function getTemplateHash(template: Div): string {
    return createHash('md5').update(JSON.stringify(template), 'utf8').digest('hex');
}

function getHashes(templates: ITemplates): { [k: string]: string } {
    const hashes: { [k: string]: string } = {};

    for (const template of Object.keys(templates)) {
        hashes[template] = getTemplateHash(templates[template]);
    }

    return hashes;
}

function versionName(name: string, hash: string): string {
    return `${name}/${hash}`;
}

export function thelperVersion(templates: ITemplates): (name: string) => string {
    const hashes = getHashes(templates);

    return (name: string) => versionName(name, hashes[name]);
}

/**
 * Переписывает вызовы TemplateBlock в шаблонах,
 * добавляя к их именам версии (md5 хэш).
 * Имена корневых шаблонов (ключи объекта templates) не меняются!
 * @param templates шаблоны
 * @param resolvedNames ранее вычисленные имена, например общих шаблонов
 * @returns хэшмап новых имен шаблонов
 */
export function rewriteTemplateVersions<T extends ITemplates>(
    templates: T,
    resolvedNames?: { [k: string]: string },
): {
    templates: T;
    resolvedNames: { [k: string]: string };
} {
    const hashes: { [k: string]: string | undefined } = {};
    const rename = (name: string, templates: T): string => {
        const template = templates[name];

        if (!template) {
            throw new Error(`No template ${name}`);
        }

        const hash = hashes[name] ?? getTemplateHash(template);

        return versionName(name, hash);
    };

    return rewriteNames(templates, rename, resolvedNames);
}
