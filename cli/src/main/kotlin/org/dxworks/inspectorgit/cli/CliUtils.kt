package org.dxworks.inspectorgit.cli

class Options {
    companion object {
        const val id = "id"
        const val computeAnnotatedLines = "computeAnnotatedLines"
        const val scripts = "scripts"
        const val iglogs = "iglogs"
        const val issues = "issues"
        const val remotes = "remotes"
        const val chronosSettings = "chrSettings"
    }
}

const val usageMessage = """
    arguments: 
        --id=<the id of the system to analyze (if it exists) / system that will be created with the help of  the next 3 fields (if it doesn't exist)> *required
        --iglogs=<comma separated list of paths to iglog files, git folders or folders containing iglogs or git folders on the first level>
        --issues=<comma separated list of paths to issue details json files or folders containing these files on the first level>
        --remotes=<comma separated list of paths to remote details json files or folders containing these files on the first level>
        --scripts=<comma separated list of paths to groovy script files you want to run on this system or folders containing these files on the first level>
        --chrSettings=<path to json file that contains author merge details>
        
    projects:
        the name of each project created in the system will be the name without extension of the file (or git folder) provided.
    scripts:
        each script will be executed and the exported files will be saved at ~/.inspectorgit/script-output/<system id>/<exported file name>.<json/csv>
        to write scripts we recommend using Intellij to open https://github.com/dxworks/inspector-git-scripts and write scripts in src starting from StartingPoint.groovy
    chrSettings:
        the file should be structured as follows
        {
            "authors": {
                "aliases": [
                    {
                        "name": "<Developer Name",
                        "aliases": [
                            {
                                "name": "<git author name>",
                                "email": "<git@author.email>"
                            },
                            {
                                "name": "<second git author name>",
                                "email": "<second.git@author.email>"
                            }
                        ]
                    }
                ],
            }
        }
"""
