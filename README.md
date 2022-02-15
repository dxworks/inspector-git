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

The following environment variables are available:
* `IG_IGLOG` if `false` will disable iglog generation. (Is overridden by `--no-iglog`)
* `IG_GITLOG` if `false` will disable git log generation. (Is overridden by `--no-gitlog`)
* `IG_INCOGNITO` if `true` enables incognito extraction (anonymize author names)

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
      igFlags: '--no-iglog --no-gitlog --incognito'
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
```

## Run as dxw instrument
Inspector git Chronos helper is also available as part of the Inspector-Git releases and as a `dxw` CLI plugin.

InspectorGit exposes the `ig` command (aliased `inspector-git`), that contains 2 subcommands:

* `dxw ig iglog <path/to/folder>` will run the iglog command
* `dxw ig chr <path/to/folder/containing/iglogs>` will produce a `chr-help.json` file that can be uploaded to Chronos.