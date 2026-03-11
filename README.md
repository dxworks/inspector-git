# Inspector Git

Visit us on [Github](https://github.com/MarioRivis/inspector-git/tree/compass-metrics).

## Use as Voyager Instrument:
To use with Voyenv as a Voyager instrument, add the following to the `instruments` in your `voyenv.yml`:

```yaml
  - name: dxworks/inspector-git
    asset: iglog.zip
```

The following flags are available: 
* `--no-iglog` disables the iglog generation
* `--no-gitlog` disables the git log generation
* `--incognito` enables incognito extraction (anonymize author names)
* `--recursive` enables recursive extraction of git logs for all .git folders in the provided path

The following environment variables are available:
* `IG_IGLOG` if `false` will disable iglog generation. (Is overridden by `--no-iglog`)
* `IG_GITLOG` if `false` will disable git log generation. (Is overridden by `--no-gitlog`)
* `IG_INCOGNITO` if `true` enables incognito extraction (anonymize author names) (Is overridden by `--incognito`)
* `IG_RECURSIVE` if `true` enables recursive extraction of git logs for all .git folders in the provided path (Is overridden by `--recursive`)

To use these values in Voyager, you can add configure any of the above-mentioned flags or environment variables in the `mission.yml` file as follows: 
```yaml

# A map of instrument names to commands and parameters.
# When 'runsAll' is false the mission will run only the instruments
# with the commands declared here, in this order.
instruments:
  iglog:
    # A map of parameter name to value
    parameters:
      # Only add the flags you need, separated by spaces
      igFlags: '--no-iglog --incognito --recursive'
```

or using environment variables:

```yaml
# A map of environment variables, name to value, for voyager missions
# overwrites the variables from global config, instrument and command
# Only set the environment variables you need
environment:
  IG_IGLOG: false
  IG_GITLOG: false
  IG_INCOGNITO: true
  IG_RECURSIVE: true
```

## Run as dxw instrument
Inspector git Chronos helper is also available as part of the Inspector-Git releases and as a `dxw` CLI plugin.

InspectorGit exposes the `ig` command (aliased `inspector-git`), that contains 3 subcommands:

* `dxw ig iglog <path/to/folder>` will run the iglog command
* `dxw ig chr <path/to/folder/containing/iglogs>` will produce a `chr-help.json` file that can be uploaded to Chronos.
* `dxw ig summary <path/to/results>` generates `summary.md` and `summary.html` from inspector-git outputs.

## Summary Command

The `summary` command reads inspector-git generated files from a results directory and creates:

- `results/summary.md` (Voyager summary metadata + markdown)
- `results/summary.html` (HTML template populated with metrics)

It extracts repository metrics such as commits, authors, and first/last commit dates from `*.git` files.

You can run it directly from the packaged Voyager instrument folder with Python:

```bash
python3 ig-summary.py <path/to/results>
```

On Windows, use:

```bash
py -3 ig-summary.py <path/to/results>
```
