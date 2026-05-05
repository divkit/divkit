/* eslint-disable max-depth */
import * as path from 'node:path';
import * as fs from 'node:fs';
import {
    describe,
    expect,
    test
} from 'vitest';
import { applyTemplate } from '../../src/utils/applyTemplate';
import type { TemplateContext } from '../../typings/common';

const dir = path.resolve(__filename, '../../../../../../test_data/parsing_test_data/templates');

function applyTemplates(json: any, templates: Record<string, unknown>, context: TemplateContext) {
    if (!(json.type && json.type in templates)) {
        return json;
    }

    const result = applyTemplate(json, context, templates || {}, error => {
        throw error;
    });

    const walk = (obj: any, context: TemplateContext) => {
        if (obj && typeof obj === 'object') {
            if (obj.type && obj.type in templates) {
                const res = applyTemplate(obj, context, templates, error => {
                    throw error;
                });

                return walk(res.json, res.templateContext);
            }

            const copy: any = Array.isArray(obj) ? [] : {};
            for (const key in obj) {
                if (key === 'items' || Array.isArray(obj)) {
                    copy[key] = walk(obj[key], context);
                } else {
                    copy[key] = obj[key];
                }
            }
            return copy;
        }

        return obj;
    };

    return walk(result.json, result.templateContext);
}

function runCase(name: string, itemPath: string): void {
    const json = JSON.parse(fs.readFileSync(itemPath, 'utf8'));

    if (!json.platforms || json.platforms.includes('web')) {
        test(name.replace('.json', ''), () => {
            const cardJson = json.card.states[0].div;

            const result = applyTemplates(cardJson, json.templates || {}, {});

            expect(result).toMatchObject(json.expected.card.states[0].div);
        });
    }
}

function proc(dir: string): void {
    const items = fs.readdirSync(dir);
    for (const item of items) {
        const itemPath = path.join(dir, item);
        if (fs.statSync(itemPath).isDirectory()) {
            describe(item, () => {
                proc(itemPath);
            });
        } else {
            runCase(item, itemPath);
        }
    }
}

describe('parsing', () => {
    proc(dir);
});
