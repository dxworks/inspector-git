from __future__ import annotations

import html
from datetime import datetime, timezone
from pathlib import Path
from typing import Any


DEFAULT_TEMPLATE_PATH = Path(__file__).resolve().parent / 'templates' / 'summary.html'

FALLBACK_TEMPLATE = """<section class=\"inspector-git-summary {{statusClass}}\">\
  <h2>Inspector Git</h2>\
  <p>Status: <strong>{{status}}</strong></p>\
  <ul>\
    <li>Repositories: {{metrics.repositoriesCount}}</li>\
    <li>IGLOG files: {{metrics.iglogFiles}}</li>\
    <li>Git logs: {{metrics.gitlogFiles}}</li>\
    <li>Total commits: {{metrics.commitsTotal}}</li>\
    <li>Unique authors: {{metrics.authorsTotal}}</li>\
    <li>First commit: {{metrics.firstCommitDate}}</li>\
    <li>Latest commit: {{metrics.lastCommitDate}}</li>\
  </ul>\
  <h3>Repository Breakdown</h3>\
  <table>\
    <thead>\
      <tr>\
        <th>Repository</th>\
        <th>Commits</th>\
        <th>Authors</th>\
        <th>First Commit</th>\
        <th>Latest Commit</th>\
      </tr>\
    </thead>\
    <tbody>\
      {{#if repositories}}\
        {{#each repositories}}\
          <tr>\
            <td>{{this.name}}</td>\
            <td>{{this.commits}}</td>\
            <td>{{this.authors}}</td>\
            <td>{{this.firstCommitDate}}</td>\
            <td>{{this.lastCommitDate}}</td>\
          </tr>\
        {{/each}}\
      {{else}}\
        <tr><td colspan=\"5\">No repository metrics available.</td></tr>\
      {{/if}}\
    </tbody>\
  </table>\
</section>"""


def render_inspector_git_summary(
    results_directory: str | Path,
    extracted: dict[str, Any],
    template_path: str | Path | None = None,
) -> dict[str, Any]:
    template = FALLBACK_TEMPLATE
    effective_status = extracted.get('status') or 'unknown'

    template_file = Path(template_path) if template_path else DEFAULT_TEMPLATE_PATH
    try:
        template = template_file.read_text(encoding='utf-8')
    except Exception:
        template = FALLBACK_TEMPLATE

    target = Path(results_directory)
    target.mkdir(parents=True, exist_ok=True)

    model = _build_template_model(extracted, effective_status)
    rendered_html = _render_template(template, model)
    rendered_markdown = _build_markdown(extracted, effective_status)
    metadata_block = _build_metadata(extracted, effective_status)

    summary_md_path = target / 'summary.md'
    summary_html_path = target / 'summary.html'

    summary_html_path.write_text(rendered_html, encoding='utf-8')
    summary_md_path.write_text(f"{metadata_block}\n---\n{rendered_markdown}\n", encoding='utf-8')

    return {
        'status': effective_status,
        'summaryMdPath': str(summary_md_path),
        'summaryHtmlPath': str(summary_html_path),
    }


def _build_template_model(extracted: dict[str, Any], effective_status: str) -> dict[str, Any]:
    model: dict[str, Any] = dict(extracted)
    metrics = extracted.get('metrics', {})

    model['status'] = effective_status
    model['statusClass'] = _to_status_class(effective_status)
    model['generatedAt'] = extracted.get('generatedAt') or _iso_now()
    model['metrics'] = {
        'repositoriesCount': metrics.get('repositoriesCount', 0),
        'iglogFiles': metrics.get('iglogFiles', 0),
        'gitlogFiles': metrics.get('gitlogFiles', 0),
        'commitsTotal': metrics.get('commitsTotal', 0),
        'authorsTotal': metrics.get('authorsTotal', 0),
        'firstCommitDate': metrics.get('firstCommitDate') or 'unknown',
        'lastCommitDate': metrics.get('lastCommitDate') or 'unknown',
    }

    return model


def _build_metadata(extracted: dict[str, Any], effective_status: str) -> str:
    metrics = extracted.get('metrics', {})
    lines = [
        '---',
        'tool: inspector-git',
        'html-template: reference',
        f'status: {effective_status}',
        'metadata:',
        f"  repositories.count: {metrics.get('repositoriesCount', 0)}",
        f"  iglog.files: {metrics.get('iglogFiles', 0)}",
        f"  gitlog.files: {metrics.get('gitlogFiles', 0)}",
        f"  commits.total: {metrics.get('commitsTotal', 0)}",
        f"  authors.total: {metrics.get('authorsTotal', 0)}",
        f"  commits.first.date: {metrics.get('firstCommitDate') or 'unknown'}",
        f"  commits.last.date: {metrics.get('lastCommitDate') or 'unknown'}",
        '  warnings.count: 0',
        f"  generated.at: {extracted.get('generatedAt') or _iso_now()}",
    ]
    return '\n'.join(lines)


def _build_markdown(extracted: dict[str, Any], effective_status: str) -> str:
    metrics = extracted.get('metrics', {})
    repositories = extracted.get('repositories', [])

    lines = [
        '## Inspector Git',
        '',
        f'- Status: {effective_status}',
        f"- Repositories detected: {metrics.get('repositoriesCount', 0)}",
        f"- IGLOG files: {metrics.get('iglogFiles', 0)}",
        f"- Git logs: {metrics.get('gitlogFiles', 0)}",
        f"- Total commits: {metrics.get('commitsTotal', 0)}",
        f"- Unique authors: {metrics.get('authorsTotal', 0)}",
        f"- First commit date: {metrics.get('firstCommitDate') or 'unknown'}",
        f"- Latest commit date: {metrics.get('lastCommitDate') or 'unknown'}",
        '',
        '### Repository Breakdown',
        '',
        '| Repository | Commits | Authors | First Commit | Latest Commit |',
        '| --- | ---: | ---: | --- | --- |',
    ]

    if not repositories:
        lines.append('| _none_ | 0 | 0 | unknown | unknown |')
    else:
        for repository in repositories:
            lines.append(
                f"| {repository.get('name', 'unknown')} | {repository.get('commits', 0)} | {repository.get('authors', 0)} | "
                f"{_to_display_date(repository.get('firstCommitDate'))} | {_to_display_date(repository.get('lastCommitDate'))} |"
            )

    return '\n'.join(lines)


def _to_display_date(value: Any) -> str:
    if value is None:
        return 'unknown'
    return str(value)


def _to_status_class(status: str) -> str:
    if status == 'success':
        return 'status-success'
    if status == 'partial':
        return 'status-warning'
    if status == 'failed':
        return 'status-error'
    return 'status-unknown'


def _render_template(template: str, model: dict[str, Any]) -> str:
    tokens, _ = _parse_nodes(template, 0, set())
    return _render_nodes(tokens, model)


def _parse_nodes(template: str, start: int, stop_tags: set[str]) -> tuple[list[dict[str, Any]], int]:
    index = start
    nodes: list[dict[str, Any]] = []

    while index < len(template):
        marker = template.find('{{', index)
        if marker < 0:
            if index < len(template):
                nodes.append({'type': 'text', 'value': template[index:]})
            return nodes, len(template)

        if marker > index:
            nodes.append({'type': 'text', 'value': template[index:marker]})

        if template.startswith('{{{', marker):
            close = template.find('}}}', marker + 3)
            if close < 0:
                nodes.append({'type': 'text', 'value': template[marker:]})
                return nodes, len(template)

            expression = template[marker + 3 : close].strip()
            nodes.append({'type': 'raw', 'expression': expression})
            index = close + 3
            continue

        close = template.find('}}', marker + 2)
        if close < 0:
            nodes.append({'type': 'text', 'value': template[marker:]})
            return nodes, len(template)

        expression = template[marker + 2 : close].strip()
        index = close + 2

        if not expression:
            continue

        if expression in stop_tags:
            return nodes, marker

        if expression.startswith('#if '):
            condition = expression[4:].strip()
            true_nodes, branch_pos = _parse_nodes(template, index, {'else', '/if'})
            false_nodes: list[dict[str, Any]] = []

            branch_marker_end = _advance_tag_end(template, branch_pos)
            branch_expression = _read_tag_expression(template, branch_pos)

            if branch_expression == 'else':
                false_nodes, end_if_pos = _parse_nodes(template, branch_marker_end, {'/if'})
                index = _advance_tag_end(template, end_if_pos)
            else:
                index = branch_marker_end

            nodes.append(
                {
                    'type': 'if',
                    'condition': condition,
                    'true_nodes': true_nodes,
                    'false_nodes': false_nodes,
                }
            )
            continue

        if expression.startswith('#each '):
            collection = expression[6:].strip()
            each_nodes, end_each_pos = _parse_nodes(template, index, {'/each'})
            index = _advance_tag_end(template, end_each_pos)
            nodes.append({'type': 'each', 'collection': collection, 'nodes': each_nodes})
            continue

        nodes.append({'type': 'var', 'expression': expression})

    return nodes, index


def _advance_tag_end(template: str, marker: int) -> int:
    if marker >= len(template):
        return marker

    close = template.find('}}', marker + 2)
    if close < 0:
        return len(template)
    return close + 2


def _read_tag_expression(template: str, marker: int) -> str:
    if marker >= len(template) or not template.startswith('{{', marker):
        return ''
    close = template.find('}}', marker + 2)
    if close < 0:
        return ''
    return template[marker + 2 : close].strip()


def _render_nodes(nodes: list[dict[str, Any]], context: dict[str, Any]) -> str:
    parts: list[str] = []

    for node in nodes:
        node_type = node.get('type')
        if node_type == 'text':
            parts.append(node.get('value', ''))
            continue

        if node_type == 'var':
            value = _resolve_expression(context, node.get('expression', ''))
            parts.append(_escape(_stringify(value)))
            continue

        if node_type == 'raw':
            value = _resolve_expression(context, node.get('expression', ''))
            parts.append(_stringify(value))
            continue

        if node_type == 'if':
            condition_value = _resolve_expression(context, node.get('condition', ''))
            branch = node.get('true_nodes', []) if _is_truthy(condition_value) else node.get('false_nodes', [])
            parts.append(_render_nodes(branch, context))
            continue

        if node_type == 'each':
            collection_value = _resolve_expression(context, node.get('collection', ''))
            if isinstance(collection_value, list):
                for item in collection_value:
                    loop_context = _child_context(context, item)
                    parts.append(_render_nodes(node.get('nodes', []), loop_context))
            continue

    return ''.join(parts)


def _child_context(parent: dict[str, Any], item: Any) -> dict[str, Any]:
    return {
        '__parent__': parent,
        'this': item,
    }


def _resolve_expression(context: dict[str, Any], expression: str) -> Any:
    path = expression.strip()
    if not path:
        return ''

    if path == 'this':
        return context.get('this', '')

    segments = path.split('.')
    value = _resolve_root_value(context, segments[0])
    for segment in segments[1:]:
        value = _resolve_segment(value, segment)
        if value is None:
            return ''
    return value


def _resolve_root_value(context: dict[str, Any], key: str) -> Any:
    if key == 'this':
        return context.get('this')

    if key in context:
        return context[key]

    current_item = context.get('this')
    if isinstance(current_item, dict) and key in current_item:
        return current_item[key]

    parent = context.get('__parent__')
    if isinstance(parent, dict):
        return _resolve_root_value(parent, key)

    return ''


def _resolve_segment(value: Any, segment: str) -> Any:
    if isinstance(value, dict):
        return value.get(segment)
    return getattr(value, segment, None)


def _is_truthy(value: Any) -> bool:
    return bool(value)


def _stringify(value: Any) -> str:
    if value is None:
        return ''
    return str(value)


def _escape(value: Any) -> str:
    return html.escape(str(value), quote=True)


def _iso_now() -> str:
    return datetime.now(timezone.utc).isoformat(timespec='milliseconds').replace('+00:00', 'Z')
