from __future__ import annotations

import json
from dataclasses import dataclass
from datetime import datetime
from email.utils import parsedate_to_datetime
from pathlib import Path
from typing import Any


@dataclass(frozen=True)
class ParsedGitLog:
    commits: int
    authors: int
    first_commit_date: datetime | None
    last_commit_date: datetime | None
    had_parse_failure: bool
    invalid_dates_count: int


def extract_inspector_git_summary(results_directory: str | Path) -> dict[str, Any]:
    target = Path(results_directory)
    repositories: list[dict[str, Any]] = []
    has_parse_failures = False
    has_invalid_dates = False

    try:
        entries = list(target.iterdir())
    except Exception:
        return _create_summary_payload(
            target,
            [],
            [],
            [],
            {},
            has_data_quality_issues=True,
        )

    iglog_files = [entry for entry in entries if entry.is_file() and entry.suffix == '.iglog']
    gitlog_files = [entry for entry in entries if entry.is_file() and entry.suffix == '.git']

    index_by_prefix, has_index_mapping_issues = _read_index_mapping(target / 'index.json')

    for gitlog_file in gitlog_files:
        parsed = _parse_gitlog_file(gitlog_file)
        repo_key = gitlog_file.stem
        repositories.append(
            {
                'name': index_by_prefix.get(repo_key, repo_key),
                'repoKey': repo_key,
                'commits': parsed.commits,
                'authors': parsed.authors,
                'firstCommitDate': parsed.first_commit_date,
                'lastCommitDate': parsed.last_commit_date,
                'sourceFile': gitlog_file.name,
            }
        )
        has_parse_failures = has_parse_failures or parsed.had_parse_failure
        has_invalid_dates = has_invalid_dates or parsed.invalid_dates_count > 0

    has_zero_commit_repositories = any(int(repo.get('commits', 0)) == 0 for repo in repositories)
    has_data_quality_issues = has_index_mapping_issues or has_parse_failures or has_invalid_dates or has_zero_commit_repositories

    return _create_summary_payload(
        target,
        iglog_files,
        gitlog_files,
        repositories,
        index_by_prefix,
        has_data_quality_issues=has_data_quality_issues,
    )


def _read_index_mapping(index_path: Path) -> tuple[dict[str, str], bool]:
    if not index_path.exists():
        return {}, False

    try:
        parsed = json.loads(index_path.read_text(encoding='utf-8'))
    except Exception:
        return {}, True

    if isinstance(parsed, dict):
        return {str(key): str(value) for key, value in parsed.items()}, False

    return {}, True


def _parse_gitlog_file(file_path: Path) -> ParsedGitLog:
    authors: set[str] = set()
    commits = 0
    first_commit_date: datetime | None = None
    last_commit_date: datetime | None = None
    invalid_dates_count = 0
    had_parse_failure = False

    try:
        with file_path.open('r', encoding='utf-8', errors='replace') as handle:
            for raw_line in handle:
                line = raw_line.rstrip('\n')

                if line.startswith('commit:'):
                    commits += 1
                    continue

                if line.startswith('author:'):
                    author = line[len('author:') :].strip()
                    if author:
                        authors.add(author)
                    continue

                if line.startswith('date:'):
                    raw_date = line[len('date:') :].strip()
                    parsed_date = _parse_date(raw_date)
                    if parsed_date is None:
                        invalid_dates_count += 1
                        continue

                    if first_commit_date is None or parsed_date < first_commit_date:
                        first_commit_date = parsed_date

                    if last_commit_date is None or parsed_date > last_commit_date:
                        last_commit_date = parsed_date
    except Exception:
        had_parse_failure = True

    return ParsedGitLog(
        commits=commits,
        authors=len(authors),
        first_commit_date=first_commit_date,
        last_commit_date=last_commit_date,
        had_parse_failure=had_parse_failure,
        invalid_dates_count=invalid_dates_count,
    )


def _parse_date(value: str) -> datetime | None:
    normalized = value.strip()
    if not normalized:
        return None

    try:
        dt = datetime.fromisoformat(normalized.replace('Z', '+00:00'))
        return _ensure_aware_utc(dt)
    except ValueError:
        pass

    try:
        dt = parsedate_to_datetime(normalized)
        return _ensure_aware_utc(dt)
    except (TypeError, ValueError):
        pass

    patterns = [
        '%a %b %d %H:%M:%S %Y %z',
        '%Y-%m-%d %H:%M:%S %z',
        '%Y-%m-%dT%H:%M:%S%z',
        '%Y-%m-%d',
    ]
    for pattern in patterns:
        try:
            dt = datetime.strptime(normalized, pattern)
            return _ensure_aware_utc(dt)
        except ValueError:
            continue

    return None


def _ensure_aware_utc(value: datetime) -> datetime:
    if value.tzinfo is None:
        return value.replace(tzinfo=datetime.now().astimezone().tzinfo)
    return value


def _create_summary_payload(
    results_directory: Path,
    iglog_files: list[Path],
    gitlog_files: list[Path],
    repositories: list[dict[str, Any]],
    index_by_prefix: dict[str, str],
    has_data_quality_issues: bool,
) -> dict[str, Any]:
    commits_total = sum(int(repo.get('commits', 0)) for repo in repositories)
    authors_total = sum(int(repo.get('authors', 0)) for repo in repositories)

    first_commit_date = _to_iso_string(_find_boundary_date(repositories, 'first'))
    last_commit_date = _to_iso_string(_find_boundary_date(repositories, 'last'))

    repositories_sorted = sorted(
        repositories,
        key=lambda repo: (-int(repo.get('commits', 0)), str(repo.get('name', ''))),
    )

    status = _resolve_status(
        gitlog_count=len(gitlog_files),
        has_data_quality_issues=has_data_quality_issues,
    )

    normalized_repositories: list[dict[str, Any]] = []
    for repository in repositories_sorted:
        normalized_repositories.append(
            {
                **repository,
                'firstCommitDate': _to_iso_string(repository.get('firstCommitDate')),
                'lastCommitDate': _to_iso_string(repository.get('lastCommitDate')),
            }
        )

    metadata = {
        'metadata.repositories.count': len(normalized_repositories),
        'metadata.iglog.files': len(iglog_files),
        'metadata.git.log.files': len(gitlog_files),
        'metadata.commits.total': commits_total,
        'metadata.authors.total': authors_total,
        'metadata.commits.first.date': first_commit_date,
        'metadata.commits.last.date': last_commit_date,
        'metadata.warnings.count': 0,
    }

    markdown_lines = [
        '## Inspector Git',
        '',
        f'- Repositories detected: {_format_int(len(normalized_repositories))}',
        f'- IGLOG files: {_format_int(len(iglog_files))}',
        f'- Git log files: {_format_int(len(gitlog_files))}',
        f'- Total commits: {_format_int(commits_total)}',
        f'- Unique authors: {_format_int(authors_total)}',
        f'- First commit date: {first_commit_date}',
        f'- Latest commit date: {last_commit_date}',
        '',
        '### Repository Breakdown',
        '',
        '| Repository | Commits | Authors | First Commit | Latest Commit |',
        '| --- | ---: | ---: | --- | --- |',
    ]

    if not normalized_repositories:
        markdown_lines.append('| _none_ | 0 | 0 | unknown | unknown |')
    else:
        for repository in normalized_repositories:
            markdown_lines.append(
                f"| {repository.get('name', 'unknown')} | {_format_int(int(repository.get('commits', 0)))} | {_format_int(int(repository.get('authors', 0)))} | "
                f"{repository.get('firstCommitDate', 'unknown')} | {repository.get('lastCommitDate', 'unknown')} |"
            )

    template_model = {
        'metrics': {
            'repositoriesCountFormatted': _format_int(len(normalized_repositories)),
            'iglogFilesFormatted': _format_int(len(iglog_files)),
            'gitlogFilesFormatted': _format_int(len(gitlog_files)),
            'commitsTotalFormatted': _format_int(commits_total),
            'authorsTotalFormatted': _format_int(authors_total),
            'firstCommitDate': first_commit_date,
            'lastCommitDate': last_commit_date,
        },
        'repositories': [
            {
                **repository,
                'commitsFormatted': _format_int(int(repository.get('commits', 0))),
                'authorsFormatted': _format_int(int(repository.get('authors', 0))),
            }
            for repository in normalized_repositories
        ],
    }

    return {
        'tool': 'inspector-git',
        'status': status,
        'metadata': metadata,
        'markdown': '\n'.join(markdown_lines),
        'templateModel': template_model,
    }


def _find_boundary_date(repositories: list[dict[str, Any]], edge: str) -> datetime | None:
    boundary: datetime | None = None
    for repository in repositories:
        key = 'firstCommitDate' if edge == 'first' else 'lastCommitDate'
        value = repository.get(key)
        if not isinstance(value, datetime):
            continue

        if boundary is None:
            boundary = value
            continue

        if edge == 'first' and value < boundary:
            boundary = value
        elif edge == 'last' and value > boundary:
            boundary = value

    return boundary


def _to_iso_string(value: Any) -> str:
    if isinstance(value, datetime):
        return value.astimezone().strftime('%Y-%m-%d')
    return 'unknown'


def _resolve_status(gitlog_count: int, has_data_quality_issues: bool) -> str:
    if gitlog_count == 0:
        return 'failed'
    if has_data_quality_issues:
        return 'partial'
    return 'success'


def _format_int(value: int) -> str:
    return f'{value:,}'
