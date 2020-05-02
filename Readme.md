#Ig-metrics

Visit us on [Github](https://github.com/MarioRivis/inspector-git/tree/compass-metrics).

##Features
Analyzes tasks, or a repository to output some specific metrics. 
Additionally, a period to analyze can be specified.

####Tasks output:
```androiddatabinding
{
  "Defect Density": 0.1358458884884443,
  "Mean Time To Repair": 654096.1037735849,
  "Defects Severity": 5.066037735849057,
  "Defects Frequency": 106.0
}
```
####Code output:
```androiddatabinding
{
  "Code Churn": 31716.0,
  "Merge Bottlenecks": 415.0
}
```

##Usage
Run `ig.sh` or `ig.bat` and provide these arguments
####Tasks
`-tasksIn=/path/to/jira-detailed-issues.json -tasksOut=/path/to/output/file.json`

where `jira-detailed-issues.json` is an output of [jiraminer](https://github.com/dxworks/jira-miner).

####Code
`-tasksIn=/path/to/repo -tasksOut=/path/to/output/file.json`

####Period
`-period=2020-01-20:2020-05-15`
