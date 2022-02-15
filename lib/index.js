const {Command} = require("commander");
const {iglog, igChrHelper} = require("./lib");

const _package = require('../package.json')

const iglogCommand = new Command()
    .name('iglog')
    .description('Extracts Iglog and gitlogs from git repositories')
    .allowUnknownOption()
    .action(iglog)

const igChrHelperCommand = new Command()
    .name('chr')
    .description('Processes iglogs and creates chronos size files')
    .allowUnknownOption()
    .action(igChrHelper)

exports.igCommand = new Command()
    .name('ig')
    .alias('inspectorGit')
    .version(_package.version)
    .description('Runs different commands of InspectorGit')
    .option('-wd --working-directory', 'Selects the directory where JaFaX will store the results folder.' +
        ` Defaults to the location where JaFaX is installed: ${__dirname}. If set to true it will use the current working directory process.cwd()`,
        false)
    .allowUnknownOption()
    .addCommand(iglogCommand)
    .addCommand(igChrHelperCommand)





