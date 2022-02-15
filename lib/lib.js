const {JavaCaller} = require('java-caller');

async function iglog(options) {
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

module.exports = {iglog, igChrHelper}
