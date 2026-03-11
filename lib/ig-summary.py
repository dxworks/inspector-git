#!/usr/bin/env python3

from __future__ import annotations

import argparse
from pathlib import Path

from summary_extract import extract_inspector_git_summary
from summary_render import render_inspector_git_summary


def main() -> int:
    parser = argparse.ArgumentParser(
        prog='ig-summary.py',
        description='Generates inspector-git summary artifacts for Voyager',
    )
    parser.add_argument('results_directory', nargs='?', default='results')
    args = parser.parse_args()

    target_directory = Path(args.results_directory).resolve()

    try:
        extracted = extract_inspector_git_summary(target_directory)
        rendered = render_inspector_git_summary(target_directory, extracted)

        print(f"Generated summary markdown at {rendered['summaryMdPath']}")
        print(f"Generated summary html at {rendered['summaryHtmlPath']}")
        return 0
    except Exception as error:
        print(f"summary generation failed for '{target_directory}': {error}")
        return 1


if __name__ == '__main__':
    raise SystemExit(main())
