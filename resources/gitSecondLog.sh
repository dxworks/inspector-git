gitSecondFolder=$(pwd)
cd "$1" || exit
if [["$2" = "-l"]]
git log -p -M5% -c -U0 --format="commit: %H%nparents: %P%nauthor name: %an%nauthor email: %ae%ndate: %cD%nmessage:%n%s%n%b"