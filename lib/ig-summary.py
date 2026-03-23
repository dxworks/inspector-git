#!/usr/bin/env python3

from __future__ import annotations

import argparse
from pathlib import Path

from summary_extract import extract_inspector_git_summary
from summary_render import render_summary


def build_missing_payload() -> dict[str, object]:
    return {
        'tool': 'inspector-git',
        'status': 'missing',
        'metadata': {},
        'markdown': '\n'.join([
            '## Inspector Git',
            '',
            '- Status: missing',
            '- Summary input is missing',
        ]),
        'templateModel': {
            'status': 'missing',
            'statusClass': 'status-missing',
            'isMissing': True,
        },
    }


def main() -> int:
    parser = argparse.ArgumentParser(
        prog='ig-summary.py',
        description='Generates inspector-git summary artifacts for Voyager',
    )
    parser.add_argument('results_directory', nargs='?', default='results')
    args = parser.parse_args()

    target_directory = Path(args.results_directory).resolve()

    try:
        gitlog_files = list(target_directory.glob('*.git'))
        iglog_files = list(target_directory.glob('*.iglog'))
        if len(gitlog_files) == 0 and len(iglog_files) == 0:
            print(
                "summary input missing for inspector-git: expected '*.git' or '*.iglog' "
                f"files in '{target_directory}'; generating missing summary artifacts"
            )
            payload = build_missing_payload()
        else:
            payload = extract_inspector_git_summary(target_directory)
        rendered = render_summary(target_directory, payload)

        print(f"Generated summary markdown at {rendered['summaryMdPath']}")
        print(f"Generated summary html at {rendered['summaryHtmlPath']}")
        return 0
    except Exception as error:
        print(f"summary generation failed for '{target_directory}': {error}")
        return 1


if __name__ == '__main__':
    raise SystemExit(main())
