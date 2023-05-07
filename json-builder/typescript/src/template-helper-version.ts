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
 * Replaces a TemplateBlock calls with the concat of the name with a hash (md5).
 * The names of the root templates are not changed! (templates object keys)
 * @param templates
 * @param resolvedNames Names cache
 * @returns A map with new names
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

        hashes[name] = hashes[name] ?? getTemplateHash(template);

        return versionName(name, hashes[name]);
    };

    return rewriteNames(templates, rename, resolvedNames);
}
