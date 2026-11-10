const path = require('node:path');
const {spawnSync} = require('node:child_process');

async function iglog(options) {
  const {JavaCaller} = require('java-caller');
  const java = new JavaCaller({
    jar: 'iglog.jar', // CLASSPATH referencing the package embedded jar files
    mainClass: 'org.dxworks.inspectorgit.gitclient.MainKt',// Main class to call, must be available from CLASSPATH,
    rootPath: __dirname,
    minimumJavaVersion: 11,
    output: 'console'
  });

  const args = [...process.argv];
  let index = Math.max(args.indexOf('iglog')); //if it is called from dxw cli
  if(index === -1)
    index = 1
  args.splice(0,  index + 1);
  const {status} = await java.run(args, {cwd: options?.workingDirectory? process.cwd(): __dirname});
  process.exitCode = status;
}

async function igChrHelper(options) {
  const {JavaCaller} = require('java-caller');
  const java = new JavaCaller({
    jar: 'ig-chr-helper.jar', // CLASSPATH referencing the package embedded jar files
    mainClass: 'org.dxworks.inspectorgit.chr.MainKt',// Main class to call, must be available from CLASSPATH,
    rootPath: __dirname,
    minimumJavaVersion: 11,
    output: 'console'
  });

  const args = [...process.argv];
  let index = args.indexOf('chr'); //if it is called from dxw cli
  if(index === -1)
    index = 1
  args.splice(0,  index + 1);
  const {status} = await java.run(args, {cwd: options?.workingDirectory? process.cwd(): __dirname});
  process.exitCode = status;
}

async function igSummary(options) {
  const args = [...process.argv];
  let index = args.indexOf('summary'); // if it is called from dxw cli
  if (index === -1)
    index = 1;
  args.splice(0, index + 1);

  const targetDirectory = path.resolve(process.cwd(), args[0] || 'results');

  const pythonScript = path.join(__dirname, 'ig-summary.py');
  const preferredCommands = process.platform === 'win32'
    ? [
      {command: 'py', args: ['-3']},
      {command: 'python', args: []},
      {command: 'python3', args: []}
    ]
    : [
      {command: 'python3', args: []},
      {command: 'python', args: []}
    ];

  for (const entry of preferredCommands) {
    const result = spawnSync(entry.command, [...entry.args, pythonScript, targetDirectory], {
      cwd: options?.workingDirectory ? process.cwd() : __dirname,
      stdio: 'inherit'
    });

    if (result.error && result.error.code === 'ENOENT')
      continue;

    if (result.error) {
      console.error(`summary generation failed for '${targetDirectory}': ${result.error.message}`);
      process.exitCode = 1;
      return;
    }

    process.exitCode = result.status ?? 1;
    return;
  }

  console.error("summary generation failed: could not find a Python interpreter ('py -3', 'python3' or 'python')");
  process.exitCode = 1;
}

module.exports = {iglog, igChrHelper, igSummary}
